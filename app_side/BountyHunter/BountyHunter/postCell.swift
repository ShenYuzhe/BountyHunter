//
//  postCell.swift
//  BountyHunter
//
//  Created by junchao zhang on 12/4/15.
//  Copyright © 2015 junchao zhang. All rights reserved.
//

import UIKit

class postCell: UITableViewCell {


    @IBOutlet weak var userImageView: UIImageView!
    @IBOutlet weak var locationTextField: UITextField!
    @IBOutlet weak var distanceTextField: UITextField!
    @IBOutlet weak var contentTextView: UITextView!
    @IBOutlet weak var bidButton: UIButton!
    @IBOutlet weak var chatButton: UIButton!
    
    var chatButtonHandler:(() -> Void)?
    var bidButtonHandler:(() -> Void)?
    
    
    @IBAction func bidTask(sender: UIButton) {
        print("bid")
        self.bidButtonHandler?()
    }
    
    
    @IBAction func chatAction(sender: UIButton) {
        print("chat")
        self.chatButtonHandler?()
    }
    
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        self.bidButton.layer.cornerRadius = 10
        self.chatButton.layer.cornerRadius = 10
        //self.locationTextField.text = "test location"
        // Initialization code
    }

    override func setSelected(selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
