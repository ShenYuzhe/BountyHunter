//
//  ChatViewController.swift
//  BountyHunter
//
//  Created by junchao zhang on 12/9/15.
//  Copyright © 2015 junchao zhang. All rights reserved.
//

import UIKit

class ChatViewController: RCConversationViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        self.setMessageAvatarStyle(RCUserAvatarStyle.USER_AVATAR_CYCLE)

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
