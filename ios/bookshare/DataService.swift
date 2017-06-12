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

    static let session = URLSession(configuration: URLSessionConfiguration.default)
    static let host = "112.213.117.196"
    static let port = "8080"
    static let serialQueue = DispatchQueue(label: "serial.DataService")
    static let group = DispatchGroup()
    static var cookie: HTTPCookie?

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
            let url = getUrl(for: "/bookshare/app/sheets/" + sheetName.rawValue)
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
        let url = getUrl(for: "/bookshare/app/sections/" + sectionName)
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
                        serialQueue.async(group: group) {
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

    static public func getBook(forISBN: String, notify: @escaping (_ book: Book) -> Void = {_ in }) -> Book? {
        if let book = isbn2BookCache[forISBN] {
            return book
        } else {
            SwiftyBeaver.verbose("fetch data for " + forISBN)
            let url = getUrl(for: "/bookshare/books/" + forISBN)
            let task = session.dataTask(with: url) { data, response, error in
                if let error = error {
                    SwiftyBeaver.error(error.localizedDescription)
                } else if let httpResponse = response as? HTTPURLResponse {
                    if httpResponse.statusCode == 200 {
                        DispatchQueue.main.async {
                            let book = parseData(forBook: data)
                            if (book != nil) {
                                notify(book!)
                            }
                        }
                    }
                }
            }
            task.resume()
            return nil
        }
    }

    static public func getCoverImage(forISBN: String, notify: @escaping (_ image: UIImage) -> Void = {_ in }) {
        SwiftyBeaver.verbose("Get cover image for \(forISBN)")
        if let book = isbn2BookCache[forISBN] {
            if book.coverURL != nil {
                let url = getUrl(for: "/bookshare/files/" + book.coverURL!)
                let task = session.dataTask(with: url) { data, response, error in
                    if let error = error {
                        SwiftyBeaver.error(error.localizedDescription)
                    } else if let httpResponse = response as? HTTPURLResponse {
                        if httpResponse.statusCode == 200 {
                            DispatchQueue.main.async {
                                // Update local cache
                                book.cover = data as NSData?

                                do {
                                    try uiContext!.save()
                                } catch let error as NSError {
                                    SwiftyBeaver.error("Could not fetch \(error), \(error.userInfo)")
                                }

                                notify(UIImage(data: book.cover! as Data)!)
                            }
                        }
                    }
                }
                task.resume()
            }
        }
    }

    static public func getVerifyCode(for user: String, callback: @escaping (_ result: Bool) -> Void ) {
        var request = URLRequest(url: getUrl(for: "/bookshare/users/getVerifyCode"))
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
        var request = URLRequest(url: getUrl(for: "/bookshare/users/changePassword"))
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
        var request = URLRequest(url: getUrl(for: "/bookshare/sessions/login"))
        request.httpMethod = "POST"
        request.setValue("application/json; charset=utf-8", forHTTPHeaderField: "Content-Type")
        request.httpBody = try! JSONSerialization.data(withJSONObject: ["username": user,
                                                                        "password": password])
        let task = session.dataTask(with: request) { data, response, error in
            if let error = error {
                SwiftyBeaver.error(error.localizedDescription)
                callback(false)
            } else if let httpResponse = response as? HTTPURLResponse {
                print(httpResponse.statusCode)
                if httpResponse.statusCode == 200 {
                    let cookies = HTTPCookie.cookies(withResponseHeaderFields: httpResponse.allHeaderFields as! [String : String],
                                                     for: httpResponse.url!)
                    cookie = cookies[0]
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

    static public func loadData(context: NSManagedObjectContext) {
        uiContext = context
        // TODO: try to put this into background to improve performance.
        let fetchRequest = NSFetchRequest<NSFetchRequestResult>(entityName: "Book")
        do {
            let results = try context.fetch(fetchRequest)
            for b in results as! [Book] {
                SwiftyBeaver.verbose("title: \(b.title!)")
                isbn2BookCache[b.isbn13!] = b
            }
        } catch let error as NSError {
            SwiftyBeaver.error("Could not fetch \(error), \(error.userInfo)")
        }
    }

}
