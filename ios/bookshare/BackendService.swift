//
//  BackendService.swift
//  bookshare
//
//  Created by 许待文 on 2016/11/30.
//  Copyright © 2016年 darwin. All rights reserved.
//

import Foundation

class BackendService {

    static let session = URLSession(configuration: URLSessionConfiguration.default)

    enum ColumnType {
        case Library
    }

    static var columnList: [String] = []

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
            return ["9787532772322", "9787553805900", "9787203079729", "9787108056153", "9787308161459"]
        case "青春":
            return ["9787557812546", "9787122260277", "9787553764320", "9787518409211", "9787553755571", "9787518407156", "9787545911305"]
        default:
            return [];
        }
    }

    static func getBook(forISBN: String, notify: @escaping (_ book: Book) -> Void = {_ in }) -> Book? {
        // Try to get the data from local firstly
        // TODO:
        // If not, get it from backend, use callback function to notify.
        let url = URL(string: "http://feedback.api.juhe.cn/ISBN?key=c00c86633d0b3a7d13a850cbe87d1a98&sub=" + forISBN)
        let task = session.dataTask(with: url! as URL) {
            (data, response, error) in
            if let error = error {
                print(error.localizedDescription)
            } else if let httpResponse = response as? HTTPURLResponse {
                if httpResponse.statusCode == 200 {
                    let book = parseData(forBook: data)
                    if (book != nil) {
                        notify(book!)
                    }
                }
            }
        }
        task.resume()
        return nil
    }

    static func parseData(forBook: Data?) -> Book? {
        var book: Book? = nil
        do {
            if let data = forBook,
                let response = try JSONSerialization.jsonObject(with: data, options:JSONSerialization.ReadingOptions(rawValue: 0)) as? [String: AnyObject] {
                if let errorCode = response["error_code"] as? Int {
                    if errorCode == 0 {
                        book = Book()
                        if let result  = response["result"] {
                            book!.name = result["title"] as? String
                            book!.subtitle = result["subtitle"] as? String
                            book!.author = result["author"] as? String
                            book!.translator = result["translator"] as? String
                            //book!.pubdate = result["pubdate"] as? String
                            book!.publisher = result["publisher"] as? String
                            book!.price = result["price"] as? Float
                            book!.summary = result["summary"] as? String
                            //book!.cover = result["images_medium"] as? String
                            book!.ISBN10 = result["isbn10"] as? String
                            book!.ISBN13 = result["isbn13"] as? String
                        }
                    }
                }
            }
        } catch let error as NSError {
            print("Error parsing results: \(error.localizedDescription)")
        }
        
        return book
    }
    
}