//
//  PostViewController.swift
//  BountyHunter
//
//  Created by junchao zhang on 11/11/15.
//  Copyright © 2015 junchao zhang. All rights reserved.
//

import UIKit
import Alamofire

class PostViewController: UIViewController {

    @IBOutlet weak var tableView: UITableView!
    private var searches:[myHistory]! = []
    var taskId: String?
    var bidUrl: String?
    var bidName: String?
    var bidPrice: String?
    var bidderId: String?
    var bidderGrade: String?
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.automaticallyAdjustsScrollViewInsets = false
        self.configure()
        self.getMyHistory()
        self.tableView.addPullToRefreshWithAction {
            NSOperationQueue().addOperationWithBlock {
                sleep(1)
                NSOperationQueue.mainQueue().addOperationWithBlock {
                    self.getMyHistory()
                    self.tableView.stopPullToRefresh()
                }
            }
        }
        // Do any additional setup after loading the view.
    }
    
    override func viewDidAppear(animated: Bool) {
        super.viewWillAppear(true)
        self.tabBarController?.tabBar.hidden = false
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func getMyHistory(){
        Alamofire.request(.GET, webURL+"task/posterHistory/19172426111").responseJSON{ response in
            let myHistoryList = (response.result.value!.valueForKey("hunters") as! [NSDictionary]).map{
                myHistory(taskId:$0["taskId"] as? String, taskcontent:$0["taskContent"]?.valueForKey("content") as? String, time:$0["modifiedAt"] as? String, status:$0["status"] as? String,price:$0["posterPrice"] as? Int, hunterURL: $0["hunterProfile"]?.valueForKey("iconUrl") as? String, hunterId: $0["hunterId"] as? String, hunterName:$0["hunterProfile"]?.valueForKey("userName") as? String, hunterGrade: $0["hunterProfile"]?.valueForKey("grade") as? Int)
            }
            self.searches = myHistoryList
            self.do_table_refresh()
            
        }
    }
    
    func do_table_refresh() {
        dispatch_async(dispatch_get_main_queue(), {
            self.tableView.reloadData()
            return
        })
    }
    
    private func configure(){
        self.tableView.registerNib(UINib(nibName: "myPostCell", bundle: nil), forCellReuseIdentifier: "myPostCell")
        self.tableView.delegate = self
        self.tableView.dataSource = self
    }
    
    func acceptBid(taskId: String, posterId:String, hunterId:String){
        let parameters =
        ["taskId": taskId,
            "posterId": posterId,
            "hunterId":hunterId
        ]
        Alamofire.request(.POST, webURL+"task/confirm",parameters: parameters, encoding: .JSON)
            .responseJSON { response in
                print(response.result.value)
        }
    }
    

    
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
        if(segue.identifier == "toBidDetail"){
            NSUserDefaults.standardUserDefaults().setObject(self.taskId, forKey: "taskId")
            NSUserDefaults.standardUserDefaults().setObject(self.bidUrl, forKey: "bidUrl")
            NSUserDefaults.standardUserDefaults().setObject(self.bidName, forKey: "bidName")
            NSUserDefaults.standardUserDefaults().setObject(self.bidPrice, forKey: "bidPrice")
            NSUserDefaults.standardUserDefaults().setObject(self.bidderId, forKey: "bidderId")
            NSUserDefaults.standardUserDefaults().setObject(self.bidderGrade, forKey: "bidderGrade")
        }
    }
    
    
    @IBAction func cancelToPostListController(segue:UIStoryboardSegue) {
        print("cancel to Post List")
    }
    
    @IBAction func postmyFeed(segue:UIStoryboardSegue) {
        print("test post my feed")
//        if let newPostViewController = segue.sourceViewController as? newPostViewController{
//            print(newPostViewController.test)
//        }
//        let parameters =
//        ["name": "hello",
//            "userid": "123",
//            "ffhui":"131"
//        ]
//        Alamofire.request(.POST, "http://BuntyEnv-vxf9vauhjj.elasticbeanstalk.com/task/postreward",parameters: parameters, encoding: .JSON)
//            .responseJSON { response in
//                //print(response.result.value!.valueForKey("aa")!)
//                
//        }
        
        
    }
    
    

}

extension PostViewController: UITableViewDelegate, UITableViewDataSource{
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell{
        let cell = self.tableView.dequeueReusableCellWithIdentifier("myPostCell", forIndexPath: indexPath) as! myPostCell
        let singleTHistory = self.searches[indexPath.row]
        cell.userNameLabel.text = singleTHistory.hunterName
        cell.timeLabel.text = singleTHistory.time
        cell.contentTextView.text = singleTHistory.taskcontent
        cell.priceLabel.text = "Price($):"+String(singleTHistory.price!)
        cell.userImageView.setZYHWebImage(singleTHistory.hunterURL, defaultImage: "yoona", isCache: true)
        cell.statusButton.setTitle(singleTHistory.status, forState: .Normal)
        
        if singleTHistory.status == "REJECTED" ||  singleTHistory.status == "SELECTED"{
            cell.statusButton.backgroundColor = UIColor.grayColor()
            cell.statusButton.userInteractionEnabled = false
        } else if singleTHistory.status == "CANDIDATE" {
            cell.statusButton.backgroundColor = UIColor(rgba: "#28b4b4")
            cell.statusButton.userInteractionEnabled = true
        } else if singleTHistory.status == "POSTED" {
            cell.statusButton.setTitle("Waiting", forState: .Normal)
            cell.chatButton.enabled = false
            cell.chatButton.backgroundColor = UIColor.grayColor()
            cell.chatButton.userInteractionEnabled = false
        }
        
        cell.acceptHandler = {
            print("accept this offer")
            self.acceptBid(singleTHistory.taskId!, posterId: "19178600218", hunterId: singleTHistory.hunterId!)
            return
        }
        
        cell.chatHandler = {
            print("I click chat")
            let conVC = RCConversationViewController()
            //conVC.targetId = "19172426111"
            //conVC.userName = "Kimi Zhang"
            conVC.targetId = singleTHistory.hunterId
            conVC.userName = singleTHistory.hunterName
            //conVC.targetId = contact.phone
            //conVC.userName = contact.first_name! + " " + contact.last_name!
            conVC.conversationType = RCConversationType.ConversationType_PRIVATE
            conVC.setMessageAvatarStyle(RCUserAvatarStyle.USER_AVATAR_CYCLE)
            conVC.title = conVC.userName
            self.navigationController?.pushViewController(conVC, animated: true)
            self.tabBarController?.tabBar.hidden = true
            return
        }        

        return cell
        
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.searches.count
    }
    
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        print("select this row")
        self.tableView.deselectRowAtIndexPath(indexPath, animated: true)
        let singleTHistory = self.searches[indexPath.row]
        self.taskId = singleTHistory.taskId
        self.bidUrl = singleTHistory.hunterURL
        self.bidPrice = String(singleTHistory.price!)
        self.bidderGrade = String(singleTHistory.hunterGrade!)
        self.bidName = singleTHistory.hunterName
        self.bidderId = singleTHistory.hunterId
        self.performSegueWithIdentifier("toBidDetail", sender: self)
        
    }
    
    func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return 100
    }
}
