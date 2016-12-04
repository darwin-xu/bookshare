//
//  BookDetailViewController.swift
//  bookshare
//
//  Created by 许待文 on 2016/11/25.
//  Copyright © 2016年 darwin. All rights reserved.
//

import UIKit

class BookDetailViewController: UIViewController {

    var bookCover: UIImage?
    var bookTitle: String?
    var bookAuthor: String?

    @IBOutlet weak var ibBookCover: UIImageView!
    @IBOutlet weak var ibBookTitle: UILabel!
    @IBOutlet weak var ibBookAuthor: UILabel!

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        ibBookCover.image = bookCover
        ibBookTitle.text = bookTitle
        ibBookAuthor.text = bookAuthor
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
    }
    
}
