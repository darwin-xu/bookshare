//
//  BookDetailViewController.swift
//  bookshare
//
//  Created by 许待文 on 2016/11/25.
//  Copyright © 2016年 darwin. All rights reserved.
//

import UIKit

class BookDetailViewController: UIViewController {

    var cover: UIImage?
    var name: String?
    var auth: String?
    
    @IBOutlet weak var bookCover: UIImageView!
    @IBOutlet weak var bookName: UILabel!
    @IBOutlet weak var author: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        NSLog("%@", self)
        // Do any additional setup after loading the view.
        bookCover.image = cover
        bookName.text = name
        author.text = auth 
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
    }

}
