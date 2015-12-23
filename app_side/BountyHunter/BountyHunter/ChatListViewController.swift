//
//  ChatListViewController.swift
//  BountyHunter
//
//  Created by junchao zhang on 12/9/15.
//  Copyright © 2015 junchao zhang. All rights reserved.
//

import UIKit

class ChatListViewController: RCConversationListViewController {
    let conVC = RCConversationViewController()
    
    
    @IBAction func strartChat(sender: UIBarButtonItem) {
        let conVC = RCConversationViewController()
        conVC.targetId = "19172426111"
        conVC.userName = "vinokimi"
        conVC.conversationType = RCConversationType.ConversationType_PRIVATE
        conVC.title = conVC.userName
        self.navigationController?.pushViewController(conVC, animated: true)
        self.tabBarController?.tabBar.hidden = true
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.setDisplayConversationTypes([
            RCConversationType.ConversationType_APPSERVICE.rawValue,
            RCConversationType.ConversationType_CHATROOM.rawValue,
            RCConversationType.ConversationType_CUSTOMERSERVICE.rawValue,
            RCConversationType.ConversationType_DISCUSSION.rawValue,
            RCConversationType.ConversationType_GROUP.rawValue,
            RCConversationType.ConversationType_PRIVATE.rawValue,
            RCConversationType.ConversationType_PUBLICSERVICE.rawValue,
            RCConversationType.ConversationType_SYSTEM.rawValue
            ])
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(true)
        self.tabBarController?.tabBar.hidden = false
        print("will apear")
        //self.refreshConversationTableViewIfNeeded()
    }
    
    override func onSelectedTableRow(conversationModelType: RCConversationModelType, conversationModel model: RCConversationModel!, atIndexPath indexPath: NSIndexPath!) {
        print("i tap it")
        //jump segue by code
        /*
        self.navigationController?.pushViewController(conVC, animated: true)
        self.tabBarController?.tabBar.hidden = true*/
        conVC.targetId = model.targetId
        conVC.userName = model.conversationTitle
        conVC.conversationType = RCConversationType.ConversationType_PRIVATE
        conVC.title = model.conversationTitle
        self.performSegueWithIdentifier("tapOnCell", sender: self)
    }
    


    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
        let destVC = segue.destinationViewController as? RCConversationViewController
        destVC?.targetId = self.conVC.targetId
        destVC?.userName = self.conVC.userName
        destVC?.conversationType = self.conVC.conversationType
        destVC?.title = self.conVC.title
        
        self.tabBarController?.tabBar.hidden = true
    }


}
