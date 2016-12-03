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

    var bookList: [String] = []
    var isbn2Book: [String: Book] = [:]

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
        return bookList.count
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "bookCell", for: indexPath)

        if let bookCell = cell as? BookCollectionViewCell {
            let isbn = bookList[indexPath.row]

            if (isbn2Book[isbn] == nil) {
                isbn2Book[isbn] = BackendService.getBook(forISBN: isbn) {book in
                    self.isbn2Book[isbn] = book
                    DispatchQueue.main.async {
                        collectionView.reloadData()
                    }
                }
            }

            if (isbn2Book[isbn] != nil) {
                bookCell.bookCover.image = isbn2Book[isbn]?.cover
                bookCell.bookName.text = isbn2Book[isbn]?.name
                bookCell.author.text = isbn2Book[isbn]?.author
            }
        }
        
        return cell
    }
    
}
