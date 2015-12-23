//
//  myPostCell.swift
//  BountyHunter
//
//  Created by junchao zhang on 12/5/15.
//  Copyright © 2015 junchao zhang. All rights reserved.
//

import UIKit

class myPostCell: UITableViewCell {
    
    @IBOutlet weak var userImageView: UIImageView!
    
    @IBOutlet weak var userNameLabel: UILabel!
    
    @IBOutlet weak var priceLabel: UILabel!
    @IBOutlet weak var contentTextView: UITextView!
    
    @IBOutlet weak var timeLabel: UILabel!
    
    @IBOutlet weak var statusButton: UIButton!
    
    @IBOutlet weak var chatButton: UIButton!
    
    var acceptHandler:(() -> Void)?
    var chatHandler:(() -> Void)?
    
    
    @IBAction func changeStatus(sender: UIButton) {
        self.acceptHandler?()
    }
    
    @IBAction func chatAct(sender: UIButton) {
        self.chatHandler?()
        
    }
    
    
    

    override func awakeFromNib() {
        super.awakeFromNib()
        self.statusButton.layer.cornerRadius = 10
        self.chatButton.layer.cornerRadius = 10
        // Initialization code
    }

    override func setSelected(selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
