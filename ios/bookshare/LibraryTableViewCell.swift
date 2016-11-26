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
    var bookNumber = 0
    
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
        return bookNumber
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "bookCell", for: indexPath)
        
        if let bookCell = cell as? BookCollectionViewCell {
            switch indexPath.row {
            case 0:
                bookCell.bookCover.image = UIImage(named: "cover1")
                bookCell.bookName.text = "国富论"
                bookCell.author.text = "亚当·斯密"
            case 1:
                bookCell.bookCover.image = UIImage(named: "cover2")
                bookCell.bookName.text = "隋唐五代史讲义"
                bookCell.author.text = "邓广明"
            case 2:
                bookCell.bookCover.image = UIImage(named: "cover3")
                bookCell.bookName.text = "明史十二讲"
                bookCell.author.text = "赵毅"
            case 3:
                bookCell.bookCover.image = UIImage(named: "cover4")
                bookCell.bookName.text = "隋唐五代史讲义"
                bookCell.author.text = "邓广铭"
            case 4:
                bookCell.backgroundColor = UIColor.brown
            case 5:
                bookCell.backgroundColor = UIColor.gray
            case 6:
                bookCell.backgroundColor = UIColor.cyan
            case 7:
                bookCell.backgroundColor = UIColor.purple
            case 8:
                bookCell.backgroundColor = UIColor.green
            default:
                bookCell.backgroundColor = UIColor.blue
            }
        }
        
        
        return cell
    }
}
