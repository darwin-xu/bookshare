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

class DataService {

    static let session = URLSession(configuration: URLSessionConfiguration.default)
    static let host = "112.213.117.196"
    static let port = "8080"
    static let serialQueue = DispatchQueue(label: "serial.DataService")
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

    static func currentThread() -> String {
        let name = __dispatch_queue_get_label(nil)
        return String(cString: name, encoding: .utf8)! + "___" + Thread.current.description
    }

    static public func getSection2Isbn(for sheetName: SheetName,
                                       callback: @escaping (_ sections: [String], _ section2Isbns: [String: [String]]) -> Void = {_ in }) -> ([String], [String: [String]]) {
        if sectionsCache == [] {
            let url = URL(string: "http://" + host + ":" + port + "/bookshare/app/sheet/" + sheetName.rawValue)
            let task = session.dataTask(with: url!) { data, response, error in
                if let error = error {
                    print(error.localizedDescription)
                } else if let httpResponse = response as? HTTPURLResponse {
                    if httpResponse.statusCode == 200 {
                        do {
                            let json = try JSONSerialization.jsonObject(with: data!) as! [String: Any]
                            // TODO: this update occurs at OperationQueue and background thread.
                            // It may have problem when main thread and background thread access the
                            // same variable at the same time.
                            sectionsCache = json["sections"] as! [String]
                            DispatchQueue.global(qos: .background).async {
                                for section: String in sectionsCache {
                                    getIsbns(for: section, group: group)
                                }
                                group.wait()
                                print("sectionsCache \(sectionsCache)")
                                print("= section2IsbnCache \(section2IsbnCache)")
                                callback(sectionsCache, section2IsbnCache)
                            }
                        } catch let error as NSError {
                            print("Could not fetch \(error), \(error.userInfo)")
                        }
                    }
                }
            }
            task.resume()
        }
        return (sectionsCache, section2IsbnCache)
    }

    static private func getIsbns(for sectionName: String, group: DispatchGroup) {
        //let url = URL(string: "http://" + host + ":" + port + "/bookshare/app/section/" + sectionName)
        let url = getUrl(for: "/bookshare/app/section/" + sectionName)
        print("getIsbns: \(url)")
        print("getIsbns:"+currentThread())
        group.enter()
        let task = session.dataTask(with: url) { data, response, error in
            print("---")
            group.leave()
            if let error = error {
                print(error.localizedDescription)
            } else if let httpResponse = response as? HTTPURLResponse {
                if httpResponse.statusCode == 200 {
                    do {
                        print("getIsbns return:"+currentThread())
                        let json = try JSONSerialization.jsonObject(with: data!) as! [String: Any]
                        let isbns = json["isbns"] as! [String]
                        //print("getIsbns return: \(isbns)")
                        serialQueue.async(group: group) {
                            section2IsbnCache[sectionName] = isbns
                            print("* section2IsbnCache \(section2IsbnCache)")
                        }
                    } catch let error as NSError {
                        print("Could not fetch \(error), \(error.userInfo)")
                    }
                }
            }
        }
        task.resume()
    }

    static func getBook(forISBN: String, notify: @escaping (_ book: Book) -> Void = {_ in }) -> Book? {
        if let book = isbn2BookCache[forISBN] {
            return book
        } else {
            NSLog("fetch data for " + forISBN)
            let url = URL(string: "http://feedback.api.juhe.cn/ISBN?key=c00c86633d0b3a7d13a850cbe87d1a98&sub=" + forISBN)
            let task = session.dataTask(with: url! as URL) { data, response, error in
                if let error = error {
                    print(error.localizedDescription)
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

    static func getCoverImage(forISBN: String, notify: @escaping (_ image: UIImage) -> Void = {_ in }) {
        NSLog("Get cover image for %@", forISBN)
        if let book = isbn2BookCache[forISBN] {
            if book.coverURL != nil {
                let url = URL(string: book.coverURL!)
                let task = session.dataTask(with: url! as URL) { data, response, error in
                    if let error = error {
                        print(error.localizedDescription)
                    } else if let httpResponse = response as? HTTPURLResponse {
                        if httpResponse.statusCode == 200 {
                            DispatchQueue.main.async {
                                // Update local cache
                                book.cover = data as NSData?

                                do {
                                    try uiContext!.save()
                                } catch let error as NSError {
                                    print("Could not fetch \(error), \(error.userInfo)")
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

    // Parse JSON data of book detail information.
    static func parseData(forBook: Data?) -> Book? {
        var book: Book? = nil

        do {
            if let data = forBook,
                let response = try JSONSerialization.jsonObject(with: data,
                                                                options: JSONSerialization.ReadingOptions(rawValue: 0))
                    as? [String: AnyObject] {
                if let errorCode = response["error_code"] as? Int {
                    if errorCode == 0 {
                        if let result  = response["result"] {
                            let isbn13 = result["isbn13"] as! String
                            if isbn2BookCache[isbn13] == nil {
                                let entity = NSEntityDescription.entity(forEntityName: "Book", in: uiContext!)
                                book = (NSManagedObject(entity: entity!, insertInto: uiContext!) as! Book)
                                book?.title = result["title"] as? String
                                book?.subtitle = result["subtitle"] as? String
                                book?.author = result["author"] as? String
                                book?.translator = result["translator"] as? String
                                //book?.publicationDate = result["publicationDate"] as? String
                                book?.publisher = result["publisher"] as? String
                                book?.price = ((result["price"] as? NSString)?.floatValue)!
                                book?.summary = result["summary"] as? String
                                book?.coverURL = result["images_medium"] as? String
                                book?.isbn10 = result["isbn10"] as? String
                                book?.isbn13 = result["isbn13"] as? String

                                isbn2BookCache[isbn13] = book

                                do {
                                    try uiContext!.save()
                                } catch let error as NSError  {
                                    print("Could not save \(error), \(error.userInfo)")
                                }
                            }
                        }
                    }
                }
            }
        } catch let error as NSError {
            print("Error parsing results: \(error.localizedDescription)")
        }

        return book
    }

    static func loadData(context: NSManagedObjectContext) {
        uiContext = context
        // TODO: try to put this into background to improve performance.
        let fetchRequest = NSFetchRequest<NSFetchRequestResult>(entityName: "Book")
        do {
            let results = try context.fetch(fetchRequest)
            for b in results as! [Book] {
                NSLog("title: %@", b.title!)
                isbn2BookCache[b.isbn13!] = b
            }
        } catch let error as NSError {
            print("Could not fetch \(error), \(error.userInfo)")
        }
    }
    
}
