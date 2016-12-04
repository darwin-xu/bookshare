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
            let isbn = bookListInCell[indexPath.row]

            if (isbn2BookInCell[isbn] == nil) {
                isbn2BookInCell[isbn] = DataService.getBook(forISBN: isbn) {book in
                    self.isbn2BookInCell[isbn] = book
                    collectionView.reloadData()
                }
            }

            if (isbn2BookInCell[isbn] != nil) {
                if isbn2BookInCell[isbn]?.cover != nil {
                    bookCell.ibBookCover.image = isbn2BookInCell[isbn]?.cover
                } else {
                    DataService.getCoverImage(forISBN: isbn) { image in
                        bookCell.ibBookCover.image = image
                    }
                }

                bookCell.ibBookTitle.text = isbn2BookInCell[isbn]?.title
                bookCell.ibBookAuthor.text = isbn2BookInCell[isbn]?.author
            }
        }
        
        return cell
    }
    
}
