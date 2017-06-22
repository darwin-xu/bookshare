//
//  BookInfoController.swift
//  bookshare
//
//  Created by Darwin Xu on 2017/6/22.
//  Copyright © 2017年 darwin. All rights reserved.
//

import UIKit
import Foundation
import SwiftyBeaver

class BookInfoController: UIViewController {

    var bookISBN: String?

    @IBOutlet weak var bookCover: UIImageView!

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        SwiftyBeaver.debug("bookISBN: " + bookISBN!)
        let book = DataService.getBook(forISBN: bookISBN!) { book in
            if let cover = book.cover {
                self.bookCover.image = UIImage(data: cover as Data)
            } else {
                DataService.getCoverImage(forISBN: self.bookISBN!) { image in
                    self.bookCover.image = image
                }
            }
        }
        if let book = book {
            if let cover = book.cover {
                self.bookCover.image = UIImage(data: cover as Data)
            } else {
                DataService.getCoverImage(forISBN: self.bookISBN!) { image in
                    self.bookCover.image = image
                }
            }
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

}
