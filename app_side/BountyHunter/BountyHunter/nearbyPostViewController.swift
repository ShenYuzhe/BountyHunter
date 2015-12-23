//
//  nearbyPostViewController.swift
//  BountyHunter
//
//  Created by junchao zhang on 12/4/15.
//  Copyright © 2015 junchao zhang. All rights reserved.
//

import UIKit
import Alamofire
import CoreLocation

class nearbyPostViewController: UIViewController,CLLocationManagerDelegate{
    let userID = NSUserDefaults.standardUserDefaults().stringForKey("userID")
    //let userID = NSUserDefaults.standardUserDefaults().stringForKey("userID")!
    private var latitude: String = ""
    private var longitude: String = ""
    @IBOutlet weak var tableView: UITableView!
    private var searches:[nearbyTask]! = []
    
    @IBOutlet weak var neabyButton: UIButton!
    @IBOutlet weak var recommendButton: UIButton!
    var locManager = CLLocationManager()
    var taskId: String?
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.automaticallyAdjustsScrollViewInsets = false
        self.tabBarController?.tabBar.hidden = false
        //self.recommendButton.backgroundColor = UIColor.whiteColor()
        //self.recommendButton.titleLabel?.textColor = UIColor(rgba: "#28b4b4")
        
        locManager.delegate = self
        locManager.requestWhenInUseAuthorization()
        locManager.requestAlwaysAuthorization()
        if CLLocationManager.locationServicesEnabled() {
            print("location function enabled")
            locManager.delegate = self
            locManager.desiredAccuracy = kCLLocationAccuracyNearestTenMeters
            locManager.startUpdatingLocation()
        }
        self.configure()
        //self.automaticallyAdjustsScrollViewInsets = false
        self.get("50",long: "50",radius: "200")
        self.tableView.addPullToRefreshWithAction {
            NSOperationQueue().addOperationWithBlock {
                sleep(1)
                NSOperationQueue.mainQueue().addOperationWithBlock {
                    print("test")
                    self.get("50",long: "50",radius: "200")
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
    
    func locationManager(manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        print("location did update")
        let locValue:CLLocationCoordinate2D = manager.location!.coordinate
        print("locations = \(locValue.latitude) \(locValue.longitude)")
    }
    
    private func configure(){
        self.tableView.registerNib(UINib(nibName: "postCell", bundle: nil), forCellReuseIdentifier: "postCell")
        self.tableView.delegate = self
        self.tableView.dataSource = self
        self.tableView.allowsSelection = false
    }
    
    func setButton(flag: Int){
        if(flag==0){
            self.neabyButton.backgroundColor = UIColor(rgba: "#28b4b4")
            self.recommendButton.backgroundColor = UIColor.whiteColor()
            self.neabyButton.titleLabel?.textColor = UIColor.whiteColor()
            self.recommendButton.titleLabel?.textColor = UIColor(rgba: "#28b4b4")
            //self.recommendButton.titleLabel?.backgroundColor = UIColor.redColor()
            
        } else if (flag==1){
            self.recommendButton.backgroundColor = UIColor(rgba: "#28b4b4")
            self.neabyButton.backgroundColor = UIColor.whiteColor()
            self.recommendButton.titleLabel!.textColor = UIColor.whiteColor()
            self.neabyButton.titleLabel!.textColor = UIColor(rgba: "#28b4b4")
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func searchNearby(sender: UIButton) {
        setButton(0)
        self.get("50",long: "50",radius: "200")
    }
    
    @IBAction func searchRecommend(sender: UIButton) {
        setButton(1)
        self.getRecommend("19172426111",lat:"50", long: "50")
        
    }
    
    func getRecommend(userID: String,lat:String, long: String){
        Alamofire.request(.GET, "http://BuntyEnv-vxf9vauhjj.elasticbeanstalk.com/task/recommend?userid="+userID+"&lat="+lat+"&lng="+long).responseJSON { response in
            print(response.result)
            if(response.result.value != nil){
                let recommendList = (response.result.value!.valueForKey("tasks") as! [NSDictionary]).map{
                    nearbyTask(content:$0["content"] as? String, price:$0["price"] as? Int, distance:String($0["distance"]!),location:$0["location"] as? String, iconUrl: $0["iconUrl"] as? String, userName: $0["userName"] as? String, taskId:$0["taskId"] as? String,posterId: $0["userId"] as? String)
                }
                self.searches = []
                self.searches = recommendList
                self.do_table_refresh()
            }

        }
        
    }

    func applyTask(taskId: String,userId: String, price: Int){
        let parameters =
        ["taskId": taskId,
            "userId": userId,
            "price":price
        ]
        Alamofire.request(.POST, webURL+"/task/apply",parameters: parameters as? [String : AnyObject], encoding: .JSON).responseJSON{
            response in
            print(response)
            dispatch_async(dispatch_get_main_queue(), { () -> Void in
                var alert: UIAlertView = UIAlertView(title: "OK", message: "You have successfully apply this task", delegate: nil, cancelButtonTitle: "OK")
                alert.show()
            })
        }
    }
    
    func get(lat:String, long: String, radius: String){
        Alamofire.request(.GET, webURL+"task/nearby?lat="+lat+"&lng="+long+"&radius="+radius)
            .responseJSON { response in
                //print(response.response) // URL response
                //print(response.result.value!.valueForKey("tasks"))
                if(response.result.value!.valueForKey("tasks") != nil){
                    let nearbyList = (response.result.value!.valueForKey("tasks") as! [NSDictionary]).map{
                        nearbyTask(content:$0["content"] as? String, price:$0["price"] as? Int, distance:String($0["distance"]!),location:$0["location"] as? String, iconUrl: $0["iconUrl"] as? String, userName: $0["userName"] as? String, taskId:$0["taskId"] as? String, posterId: $0["userId"] as? String)
                    }
                    self.searches = []
                    self.searches = nearbyList
                    print(self.searches.count)
                    self.do_table_refresh()
                }

        }
    }
    
    func do_table_refresh() {
        dispatch_async(dispatch_get_main_queue(), {
            self.tableView.reloadData()
            return
        })
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

extension nearbyPostViewController: UITableViewDelegate, UITableViewDataSource{
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell{
        let cell = self.tableView.dequeueReusableCellWithIdentifier("postCell", forIndexPath: indexPath) as! postCell
        let singleTask = self.searches[indexPath.row]
        cell.contentTextView.text = singleTask.userName! + ": " + singleTask.content!
        cell.distanceTextField.text = singleTask.distance! + "m"
        cell.locationTextField.text = singleTask.location
        print(singleTask.iconUrl)
        cell.userImageView.setZYHWebImage(singleTask.iconUrl, defaultImage: "yoona", isCache: true)
        //cell.userImageView.sizeThatFits(CGSizeMake(40, 40))
        cell.chatButtonHandler={
            print(singleTask.posterId)
            print("I click chat")            
            let conVC = RCConversationViewController()
            conVC.targetId = singleTask.posterId
            conVC.userName = singleTask.userName
            //conVC.targetId = contact.phone
            //conVC.userName = contact.first_name! + " " + contact.last_name!
            conVC.conversationType = RCConversationType.ConversationType_PRIVATE
            conVC.setMessageAvatarStyle(RCUserAvatarStyle.USER_AVATAR_CYCLE)
            conVC.title = conVC.userName
            self.navigationController?.pushViewController(conVC, animated: true)
            self.tabBarController?.tabBar.hidden = true
            return
        }
        let taskId = singleTask.taskId
        
        cell.bidButtonHandler = {
            print("bid this task")
            self.applyTask(taskId!, userId: "19172426111", price: 10)
            cell.bidButton.backgroundColor = UIColor.grayColor()
            cell.bidButton.titleLabel?.text = "Bidded"
            cell.bidButton.enabled = false
            
            
            
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
        
    }
    
    func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return 100
    }
}


