//
//  BackendService.swift
//  bookshare
//
//  Created by 许待文 on 2016/11/30.
//  Copyright © 2016年 darwin. All rights reserved.
//

import Foundation
import CoreData
import UIKit
import SwiftyBeaver

class DataService {

    static let session = URLSession(configuration: .default)
    static let host = "112.213.117.196"
    static let port = "8080"
    static let serialQueueSection = DispatchQueue(label: "serial.DataService.section")
    static let group = DispatchGroup()

    enum PageName : String {
        case Library
        case Personal
    }

    enum SheetName: String {
        case Library
        case Personal
    }

    static var uiContext: NSManagedObjectContext?
    static var sectionsCache: [String] = []
    static var section2IsbnCache: [String: [String]] = [:]
    static var isbn2BookCache: [String: Book] = [:]

    static private func getUrl(for relativePath: String) -> URL {
        let urlPath = "http://" + host + ":" + port + relativePath
        return URL(string: urlPath.addingPercentEncoding(withAllowedCharacters: .urlFragmentAllowed)!)!
    }

    static public func getSection2Isbn(for sheetName: SheetName,
                                       callback: @escaping (_ sections: [String], _ section2Isbns: [String: [String]]) -> Void = {_ in }) -> ([String], [String: [String]]) {
        if sectionsCache == [] {
            let urlString = "/bookshare/app/sheets/" + sheetName.rawValue
            SwiftyBeaver.info("Backend: " + urlString)
            let url = getUrl(for: urlString)
            let task = session.dataTask(with: url) { data, response, error in
                if let error = error {
                    SwiftyBeaver.error(error.localizedDescription)
                } else if let httpResponse = response as? HTTPURLResponse {
                    if httpResponse.statusCode == 200 {
                        do {
                            let json = try JSONSerialization.jsonObject(with: data!) as! [String: Any]
                            // TODO: This update occurs at OperationQueue and background thread.
                            // It may have problem when main thread and background thread access the
                            // same variable at the same time.
                            sectionsCache = json["sections"] as! [String]
                            SwiftyBeaver.verbose("Get isbns for section: \(sectionsCache)")
                            DispatchQueue.global(qos: .background).async {
                                for section: String in sectionsCache {
                                    getIsbns(for: section, group: group)
                                }
                                group.wait()
                                SwiftyBeaver.verbose("Isbns for section: \(sectionsCache) is ready.")
                                callback(sectionsCache, section2IsbnCache)
                            }
                        } catch let error as NSError {
                            SwiftyBeaver.error("Could not fetch \(error), \(error.userInfo)")
                        }
                    }
                }
            }
            task.resume()
        }
        // TODO: This may have conflict with the completion handler.
        return (sectionsCache, section2IsbnCache)
    }

    static private func getIsbns(for sectionName: String, group: DispatchGroup) {
        let urlString = "/bookshare/app/sections/" + sectionName
        SwiftyBeaver.info("Backend: " + urlString)
        let url = getUrl(for: urlString)
        SwiftyBeaver.verbose("Send the request to get [\(sectionName)].")
        group.enter()
        let task = session.dataTask(with: url) { data, response, error in
            if let error = error {
                SwiftyBeaver.error(error.localizedDescription)
            } else if let httpResponse = response as? HTTPURLResponse {
                if httpResponse.statusCode == 200 {
                    do {
                        let json = try JSONSerialization.jsonObject(with: data!) as! [String: Any]
                        let isbns = json["isbns"] as! [String]
                        serialQueueSection.async(group: group) {
                            group.leave()
                            section2IsbnCache[sectionName] = isbns
                            SwiftyBeaver.verbose("Data for [\(sectionName)] is returned.")
                        }
                    } catch let error as NSError {
                        SwiftyBeaver.error("Could not fetch \(error), \(error.userInfo)")
                    }
                }
            }
        }
        task.resume()
    }

    static public func getBook(forISBN: String, notify: @escaping (_ book: Book) -> Void) -> Book? {
        if let book = isbn2BookCache[forISBN] {
            return book
        } else {
            let urlString = "/bookshare/books/" + forISBN
            SwiftyBeaver.info("Backend: " + urlString)
            let url = getUrl(for: urlString)
            let task = session.dataTask(with: url) { data, response, error in
                if let error = error {
                    SwiftyBeaver.error(error.localizedDescription)
                } else if let httpResponse = response as? HTTPURLResponse {
                    if httpResponse.statusCode == 200 {
                        if let book = parseData(forBook: data) {
                            getCoverImage(for: book, callback: notify)
                        }
                    }
                }
            }
            task.resume()
            return nil
        }
    }

    static private func getCoverImage(for book: Book, callback notify: @escaping (_ book: Book) -> Void) {
        if let coverUrl = book.coverURL {
            let url = getUrl(for: "/bookshare/files/" + coverUrl)
            let task = session.dataTask(with: url) { data, response, error in
                if let error = error {
                    SwiftyBeaver.error(error.localizedDescription)
                } else if let httpResponse = response as? HTTPURLResponse {
                    if httpResponse.statusCode == 200 {
                        // Update local cache
                        book.cover = data as NSData?
                        do {
                            try uiContext!.save()
                        } catch let error as NSError {
                            SwiftyBeaver.error("Could not save \(error), \(error.userInfo)")
                        }
                    }
                }
                DispatchQueue.main.async {
                    notify(book)
                }
            }
            task.resume()
        } else {
            DispatchQueue.main.async {
                notify(book)
            }
        }
    }

    static public func getVerifyCode(for user: String, callback: @escaping (_ result: Bool) -> Void ) {
        let urlString = "/bookshare/users/getVerifyCode"
        SwiftyBeaver.info("Backend: " + urlString)
        var request = URLRequest(url: getUrl(for: urlString))
        request.httpMethod = "POST"
        request.setValue("application/json; charset=utf-8", forHTTPHeaderField: "Content-Type")
        request.httpBody = try! JSONSerialization.data(withJSONObject: ["username": user])
        let task = session.dataTask(with: request) { data, response, error in
            if let error = error {
                SwiftyBeaver.error(error.localizedDescription)
                callback(false)
            } else if let httpResponse = response as? HTTPURLResponse {
                if httpResponse.statusCode == 201 {
                    callback(true)
                } else {
                    callback(false)
                }
            }
        }
        task.resume()
    }

    static public func changePassword(for user: String, verifyCode: String, password: String, callback: @escaping (_ result: Bool) -> Void) {
        let urlString = "/bookshare/users/changePassword"
        SwiftyBeaver.info("Backend: " + urlString)
        var request = URLRequest(url: getUrl(for: urlString))
        request.httpMethod = "PATCH"
        request.setValue("application/json; charset=utf-8", forHTTPHeaderField: "Content-Type")
        request.httpBody = try! JSONSerialization.data(withJSONObject: ["username": user,
                                                                        "verifyCode": verifyCode,
                                                                        "password": password])
        let task = session.dataTask(with: request) { data, response, error in
            if let error = error {
                SwiftyBeaver.error(error.localizedDescription)
                callback(false)
            } else if let httpResponse = response as? HTTPURLResponse {
                if httpResponse.statusCode == 200 {
                    callback(true)
                } else {
                    callback(false)
                }
            }
        }
        task.resume()
    }

    static public func login(for user: String, password: String, callback: @escaping (_ result: Bool) -> Void) {
        let urlString = "/bookshare/sessions/login"
        SwiftyBeaver.info("Backend: " + urlString)
        var request = URLRequest(url: getUrl(for: urlString))
        request.httpMethod = "POST"
        request.setValue("application/json; charset=utf-8", forHTTPHeaderField: "Content-Type")
        request.httpBody = try! JSONSerialization.data(withJSONObject: ["username": user,
                                                                        "password": password])
        let task = session.dataTask(with: request) { data, response, error in
            if let error = error {
                SwiftyBeaver.error(error.localizedDescription)
                callback(false)
            } else if let httpResponse = response as? HTTPURLResponse {
                if httpResponse.statusCode == 200 {
                    let cookies = HTTPCookie.cookies(withResponseHeaderFields: httpResponse.allHeaderFields as! [String : String],
                                                     for: httpResponse.url!)
                    let cookie = cookies[0]
                    UserDefaults.standard.set(cookie.properties, forKey: "cookie")
                    callback(true)
                } else {
                    callback(false)
                }
            }
        }
        task.resume()

    }

    // Parse JSON data of book detail information.
    static private func parseData(forBook: Data?) -> Book? {
        var book: Book? = nil

        do {
            if let data = forBook,
                let response = try JSONSerialization.jsonObject(with: data,
                                                                options: JSONSerialization.ReadingOptions(rawValue: 0))
                    as? [String: AnyObject] {
                let isbn13 = response["isbn13"] as! String
                if isbn2BookCache[isbn13] == nil {
                    let entity = NSEntityDescription.entity(forEntityName: "Book", in: uiContext!)
                    book = (NSManagedObject(entity: entity!, insertInto: uiContext!) as! Book)
                    book?.isbn13 = response["isbn13"] as? String
                    book?.isbn10 = response["isbn10"] as? String
                    book?.title = response["title"] as? String
                    book?.subtitle = response["subtitle"] as? String
                    book?.author = response["author"] as? String
                    book?.translator = response["translator"] as? String
                    //book?.publicationDate = result["publicationDate"] as? String
                    book?.publisher = response["publisher"] as? String
                    book?.price = ((response["price"] as? NSString)?.floatValue)!
                    book?.summary = response["summary"] as? String
                    book?.coverURL = response["images_medium"] as? String

                    isbn2BookCache[isbn13] = book

                    do {
                        try uiContext!.save()
                    } catch let error as NSError  {
                        SwiftyBeaver.error("Could not save \(error), \(error.userInfo)")
                    }
                }
            }
        } catch let error as NSError {
            SwiftyBeaver.error("Error parsing results: \(error.localizedDescription)")
        }

        return book
    }

    static public func getBookShelf(callback notify: @escaping (_ books: [String]) -> Void) {
        let urlString = "/bookshare/users/bookshelf"
        SwiftyBeaver.info("Backend: " + urlString)
        let url = getUrl(for: urlString)
        let task = session.dataTask(with: url) { data, response, error in
            if let error = error {
                SwiftyBeaver.error(error.localizedDescription)
            } else if let httpResponse = response as? HTTPURLResponse {
                if httpResponse.statusCode == 200 {
                    do {
                        let result = try JSONSerialization.jsonObject(with: data!,
                                                                      options: JSONSerialization.ReadingOptions(rawValue: 0))
                        notify(result as! [String])
                    } catch let error as NSError {
                        SwiftyBeaver.error("Error parsing results: \(error.localizedDescription)")
                    }
                }
            }
        }
        task.resume()
    }

    static public func postBookShelf(isbn: String, callback notify: @escaping (_ result: Bool) -> Void) {
        let urlString = "/bookshare/users/bookshelf/" + isbn
        SwiftyBeaver.info("Backend: " + urlString)
        var request = URLRequest(url: getUrl(for: urlString))
        request.httpMethod = "POST"
        let task = session.dataTask(with: request) { data, response, error in
            if let error = error {
                notify(false)
                SwiftyBeaver.error(error.localizedDescription)
            } else if let httpResponse = response as? HTTPURLResponse {
                if httpResponse.statusCode == 200 {
                    notify(true)
                } else {
                    SwiftyBeaver.error("Status code: " + String(httpResponse.statusCode))
                    notify(false)
                }
            }
        }
        task.resume()
    }

    static public func deleteBookShelf(isbn: String, callback notify: @escaping (_ result: Bool) -> Void) {
        let urlString = "/bookshare/users/bookshelf/" + isbn
        SwiftyBeaver.info("Backend: " + urlString)
        var request = URLRequest(url: getUrl(for: urlString))
        request.httpMethod = "DELETE"
        let task = session.dataTask(with: request) { data, response, error in
            if let error = error {
                notify(false)
                SwiftyBeaver.error(error.localizedDescription)
            } else if let httpResponse = response as? HTTPURLResponse {
                if httpResponse.statusCode == 200 {
                    notify(true)
                } else {
                    notify(false)
                }
            }
        }
        task.resume()
    }

    static public func loadData(context: NSManagedObjectContext) {
        if let cookieProperties = UserDefaults.standard.object(forKey: "cookie") as? [HTTPCookiePropertyKey : Any] {
            HTTPCookieStorage.shared.setCookie(HTTPCookie(properties: cookieProperties)!)
        }

        uiContext = context
        // TODO: try to put this into background to improve performance.
        let fetchRequest = NSFetchRequest<NSFetchRequestResult>(entityName: "Book")
        do {
            let results = try context.fetch(fetchRequest)
            for book in results as! [Book] {
                SwiftyBeaver.verbose("title: \(book.title!)")
                isbn2BookCache[book.isbn13!] = book
            }
        } catch let error as NSError {
            SwiftyBeaver.error("Could not fetch \(error), \(error.userInfo)")
        }
    }

}
