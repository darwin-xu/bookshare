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

    enum ColumnType {
        case Library
    }

    static var uiContext: NSManagedObjectContext?

    static var columnList: [String] = []
    static var isbn2BookCache: [String: Book] = [:]

    static func getColumnList(forPageName: ColumnType, callback: @escaping (_ columnList: [String]) -> Void = {_ in }) -> [String] {
        switch forPageName {
        case ColumnType.Library:

            Timer.scheduledTimer(withTimeInterval: 5, repeats: false) {_ in
                columnList = ["热门", "经典", "流行", "青春"]
                callback(columnList)
            }

            return columnList
        }
    }

    static func getBookISBNList(forColumnName: String) -> [String] {
        switch forColumnName {
        case "热门":
            return ["9787500648192", "9787505417731", "9787508622545", "9787301150894"]
        case "经典":
            return ["9787516810941", "9787509766989", "9787553805900", "9787550278998", "9787508665450", "9787301268711"]
        case "流行":
            return ["9787532772322", "9787540477783", "9787203079729", "9787108056153", "9787308161459"]
        case "青春":
            return ["9787540478025", "9787539962887", "9787515804743", "9787508654812"]
        default:
            return [];
        }
    }

    static func getBook(forISBN: String, notify: @escaping (_ book: Book) -> Void = {_ in }) -> Book? {
        if let book = isbn2BookCache[forISBN] {
            // Try to get the data from local firstly
            return book
        }
        else {
            // If not, get it from backend, use callback function to notify.
            NSLog("fetch data for " + forISBN)
            let url = URL(string: "http://feedback.api.juhe.cn/ISBN?key=c00c86633d0b3a7d13a850cbe87d1a98&sub=" + forISBN)
            let task = session.dataTask(with: url! as URL) { data, response, error in
                if let error = error {
                    print(error.localizedDescription)
                } else if let httpResponse = response as? HTTPURLResponse {
                    if httpResponse.statusCode == 200 {
                        let book = parseData(forBook: data)
                        if (book != nil) {
                            DispatchQueue.main.async {
                                // Update local cache
                                isbn2BookCache[book!.isbn13!] = book

                                // Save it into CoreData
                                // Check if it is exist first
                                var exist = false
                                let fetchRequest = NSFetchRequest<NSFetchRequestResult>(entityName: "CDBook")
                                fetchRequest.predicate = NSPredicate(format: "isbn13 == %@", forISBN)
                                do {
                                    if let books = try uiContext!.fetch(fetchRequest) as? [NSManagedObject] {
                                        for _ in books {
                                            exist = true
                                            break
                                        }
                                    }
                                } catch let error as NSError {
                                    print("Could not fetch \(error), \(error.userInfo)")
                                }

                                if (!exist) {
                                    let entity = NSEntityDescription.entity(forEntityName: "CDBook", in: uiContext!)
                                    let cdBook = NSManagedObject(entity: entity!, insertInto: uiContext!)
                                    cdBook.setValue(book!.title, forKey: "title")
                                    cdBook.setValue(book!.subtitle, forKey: "subtitle")
                                    cdBook.setValue(book!.author, forKey: "author")
                                    cdBook.setValue(book!.translator, forKey: "translator")
                                    //cdBook.setValue(book!.publicationDate, forKey: "publicationDate")
                                    cdBook.setValue(book!.publisher, forKey: "publisher")
                                    cdBook.setValue(book!.price, forKey: "price")
                                    cdBook.setValue(book!.summary, forKey: "summary")
                                    cdBook.setValue(book!.coverURL, forKey: "coverURL")
                                    cdBook.setValue(book!.isbn10, forKey: "isbn10")
                                    cdBook.setValue(book!.isbn13, forKey: "isbn13")
                                    do {
                                        try uiContext!.save()
                                    } catch let error as NSError  {
                                        print("Could not save \(error), \(error.userInfo)")
                                    }
                                }

                                // Notify UI
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
                                book.cover = UIImage(data: data!)

                                // Save it into CoreData
                                let fetchRequest = NSFetchRequest<NSFetchRequestResult>(entityName: "CDBook")
                                fetchRequest.predicate = NSPredicate(format: "isbn13 == %@", forISBN)

                                do {
                                    if let books = try uiContext!.fetch(fetchRequest) as? [NSManagedObject] {
                                        NSLog("begin")
                                        for book in books {
                                            NSLog("find = %@", book.value(forKey: "title") as! String)
                                            book.setValue(data, forKey: "cover")
                                        }
                                        NSLog("end")
                                    }
                                    try uiContext!.save()
                                } catch let error as NSError {
                                    print("Could not fetch \(error), \(error.userInfo)")
                                }

                                notify(book.cover!)
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
                let response = try JSONSerialization.jsonObject(with: data, options:JSONSerialization.ReadingOptions(rawValue: 0)) as? [String: AnyObject] {
                if let errorCode = response["error_code"] as? Int {
                    if errorCode == 0 {
                        book = Book()
                        if let result  = response["result"] {
                            book!.title = result["title"] as? String
                            book!.subtitle = result["subtitle"] as? String
                            book!.author = result["author"] as? String
                            book!.translator = result["translator"] as? String
                            //book!.publicationDate = result["publicationDate"] as? String
                            book!.publisher = result["publisher"] as? String
                            book!.price = result["price"] as? Float
                            book!.summary = result["summary"] as? String
                            book!.coverURL = result["images_medium"] as? String
                            book!.isbn10 = result["isbn10"] as? String
                            book!.isbn13 = result["isbn13"] as? String
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
        let fetchRequest = NSFetchRequest<NSFetchRequestResult>(entityName: "CDBook")
        do {
            let results = try context.fetch(fetchRequest)
            let books = results as! [NSManagedObject]
            for book in books {
                // context.delete(book)

                let isbn13 = book.value(forKey: "isbn13") as! String
                isbn2BookCache[isbn13] = Book()
                isbn2BookCache[isbn13]!.title = book.value(forKey: "title") as? String
                isbn2BookCache[isbn13]!.subtitle = book.value(forKey: "subtitle") as? String
                isbn2BookCache[isbn13]!.author = book.value(forKey: "author") as? String
                isbn2BookCache[isbn13]!.translator = book.value(forKey: "translator") as? String
                //isbn2BookCache[isbn13]!.publicationDate = book.value(forKey: "publicationDate") as? String
                isbn2BookCache[isbn13]!.publisher = book.value(forKey: "publisher") as? String
                isbn2BookCache[isbn13]!.price = book.value(forKey: "price") as? Float
                isbn2BookCache[isbn13]!.summary = book.value(forKey: "summary") as? String
                isbn2BookCache[isbn13]!.coverURL = book.value(forKey: "coverURL") as? String
                isbn2BookCache[isbn13]!.isbn10 = book.value(forKey: "isbn10") as? String
                isbn2BookCache[isbn13]!.isbn13 = book.value(forKey: "isbn13") as? String

                NSLog("Title: %@, Author: %@", isbn2BookCache[isbn13]!.title!, isbn2BookCache[isbn13]!.author!)

                // Read image from CoreData
                if let data = book.value(forKey: "cover") {
                    isbn2BookCache[isbn13]!.cover = UIImage(data: data as! Data)
                }
            }
        } catch let error as NSError {
            print("Could not fetch \(error), \(error.userInfo)")
        }
    }
    
}