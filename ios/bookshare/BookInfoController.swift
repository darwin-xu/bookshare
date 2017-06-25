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
            }
        }
        if let book = book {
            if let cover = book.cover {
                self.bookCover.image = UIImage(data: cover as Data)
            }
        }

        navigationController?.viewControllers.remove(at: (navigationController?.viewControllers.count)! - 2)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    @IBAction func addToLibrary(_ sender: Any) {
        DataService.postBookShelf(isbn: bookISBN!) { result in
            SwiftyBeaver.info("Post book [" + self.bookISBN! + "] to library " + (result ? "success" : "fail"))
            if result == true {
                let idx = (self.navigationController?.viewControllers.count)! - 2
                if let myBookshelf = self.navigationController?.viewControllers[idx] as? MyBookshelfViewController {
                    myBookshelf.isbns.insert(self.bookISBN!, at: 0)
                }
            }
            DispatchQueue.main.async {
                self.navigationController!.popViewController(animated: true)
            }
        }
    }

}
