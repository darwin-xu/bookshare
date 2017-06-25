//
//  MyBookshelfController.swift
//  bookshare
//
//  Created by Darwin Xu on 2017/6/25.
//  Copyright © 2017年 darwin. All rights reserved.
//

import UIKit
import Foundation
import SwiftyBeaver

class MyBookshelfViewController: UITableViewController {

    var isbns: [String] = []

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.

        DataService.getBookShelf() { isbns in
            self.isbns = isbns
            self.tableView.reloadData()
        }

        self.tableView.allowsMultipleSelectionDuringEditing = false
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    override func viewWillAppear(_ animated: Bool) {
        self.tableView.reloadData()
    }

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if section == 0 {
            return isbns.count
        } else {
            return 0
        }
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "BookshelfIdentifer", for: indexPath)

        if let cell = cell as? MyBookshelfViewCell {
            let book = DataService.getBook(forISBN: isbns[indexPath.row]) { book in
                // TODO: this part maybe never called.
                cell.name.text = book.title
                cell.author.text = book.author
                if book.cover != nil {
                    cell.cover.image = UIImage(data: book.cover! as Data)
                }
                tableView.reloadData()
            }
            if book != nil {
                cell.name.text = book!.title
                cell.author.text = book!.author
                if book!.cover != nil {
                    cell.cover.image = UIImage(data: book!.cover! as Data)
                }
            }
        }

        return cell
    }

    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            DataService.deleteBookShelf(isbn: isbns[indexPath.row]) { result in
                if result == true {
                    DispatchQueue.main.async {
                        self.isbns.remove(at: indexPath.row)
                        tableView.deleteRows(at: [indexPath], with: .fade)
                    }
                }
            }
        }
    }

}
