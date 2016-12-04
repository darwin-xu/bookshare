//
//  LibraryTableViewCell.swift
//  bookshare
//
//  Created by 许待文 on 2016/11/24.
//  Copyright © 2016年 darwin. All rights reserved.
//

import UIKit

class LibraryTableViewCell: UITableViewCell {

    @IBOutlet weak var category: UILabel!

    var bookListInCell: [String] = []
    var isbn2BookInCell: [String: Book] = [:]

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        // Configure the view for the selected state
    }

}

extension LibraryTableViewCell: UICollectionViewDelegate, UICollectionViewDataSource {

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return bookListInCell.count
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "bookCell", for: indexPath)

        if let bookCell = cell as? BookCollectionViewCell {
            NSLog("collection: %d", indexPath.row)
            let isbn = bookListInCell[indexPath.row]

            if (isbn2BookInCell[isbn] == nil) {
                isbn2BookInCell[isbn] = DataService.getBook(forISBN: isbn) { book in
                    self.isbn2BookInCell[isbn] = book
                    self.fill(bookCell: bookCell, withBook: book)
                    //collectionView.reloadData()
                }
            }

            if let book = isbn2BookInCell[isbn] {
                fill(bookCell: bookCell, withBook: book)
            }
        }
        
        return cell
    }

    func fill(bookCell: BookCollectionViewCell, withBook: Book) {
        if withBook.cover != nil {
            bookCell.ibBookCover.image = withBook.cover
        } else {
            DataService.getCoverImage(forISBN: withBook.isbn13!) { image in
                bookCell.ibBookCover.image = image
            }
            bookCell.ibBookTitle.text = withBook.title
            bookCell.ibBookAuthor.text = withBook.author
        }
    }
    
}
