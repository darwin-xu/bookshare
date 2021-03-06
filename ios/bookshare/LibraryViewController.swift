//
//  LibraryViewController.swift
//  bookshare
//
//  Created by 许待文 on 2016/11/17.
//  Copyright © 2016年 darwin. All rights reserved.
//

import UIKit

class LibraryViewController: UITableViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return 3
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "LibraryIdentifier", for: indexPath)
            
        if let libraryCell = cell as? LibraryTableViewCell {
            switch indexPath.row {
            case 0:
                libraryCell.category?.text = "Trending"
                libraryCell.bookNumber = 4
            case 1:
                libraryCell.category?.text = "Popular Fiction"
                libraryCell.bookNumber = 6
            case 2:
                libraryCell.category?.text = "儿童诗歌"
                libraryCell.bookNumber = 2
            default:
                libraryCell.category?.text = "Unknown"
                libraryCell.bookNumber = 0
            }
        }
  
        return cell
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        NSLog("..")
        if segue.identifier == "selectBook" {
            if let cell = sender as? LibraryTableViewCell {
                NSLog("%@ as LibraryTableViewCell", cell)
            }
            if let libraryCell = sender as? BookCollectionViewCell {
                NSLog("1")
                if let bookDetail = segue.destination as? BookDetailViewController {
                    NSLog("2 %@", libraryCell)
                    bookDetail.cover = libraryCell.bookCover.image
                    bookDetail.name = libraryCell.bookName.text
                    bookDetail.auth = libraryCell.author.text
                    //bookDetail.bookName?.text = libraryCell.bookName?.text
                    //bookDetail.author?.text = libraryCell.author?.text
                }
            }
            
        }
    }
    
    /*
    // Override to support conditional editing of the table view.
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    */

    /*
    // Override to support editing the table view.
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            // Delete the row from the data source
            tableView.deleteRows(at: [indexPath], with: .fade)
        } else if editingStyle == .insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }
    */

    /*
    // Override to support rearranging the table view.
    override func tableView(_ tableView: UITableView, moveRowAt fromIndexPath: IndexPath, to: IndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(_ tableView: UITableView, canMoveRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    */

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
