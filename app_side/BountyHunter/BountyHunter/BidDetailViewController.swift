//
//  BidDetailViewController.swift
//  BountyHunter
//
//  Created by junchao zhang on 12/7/15.
//  Copyright © 2015 junchao zhang. All rights reserved.
//

import UIKit
import Alamofire


class BidDetailViewController: UIViewController {

    @IBOutlet weak var bidderImageView: UIImageView!
    
    @IBOutlet weak var bidderName: UITextField!
    
    @IBOutlet weak var bidPriceTextField: UITextField!
    
    @IBOutlet weak var acceptButton: UIButton!
    
    @IBOutlet weak var rejectButton: UIButton!
    
    
    @IBOutlet weak var callButton: UIButton!
    @IBOutlet weak var bidderPhoneTextField: UITextField!
    
    @IBOutlet weak var locationTextField: UITextField!
    
    @IBOutlet weak var gradeTextField: UITextField!
    
    @IBOutlet weak var rateButton: UIButton!
    
    
    var star = StarReview()
    var taskId: String?
    var bidUrl: String?
    var bidName: String?
    var bidPrice: String?
    var bidderId: String?
    var bidderGrade: String?
    
    
    
    @IBAction func accpetBid(sender: UIButton) {
        print("accept this one")
        self.accept(self.taskId!, posterId: "19172426111", hunterId: self.bidderId!)
    }
    
    @IBAction func rejectBid(sender: UIButton) {
        print("reject this one")
        self.accept(self.taskId!, posterId: "19172426111", hunterId: self.bidderId!)
    }
    
    
    @IBAction func rateBid(sender: UIButton) {
        //self.rate()
        self.rate(self.taskId!, posterId: "19172426111")
    }
    
    
    @IBAction func callPhone(sender: UIButton) {
        UIApplication.sharedApplication().openURL(NSURL(string: "tel://" + self.bidderPhoneTextField.text!)!)
    }
    
    func accept(taskId:String,posterId:String,hunterId:String){
        let parameters =
        ["taskId" : taskId,
            "posterId" : posterId,
            "hunterId": hunterId
        ]
        Alamofire.request(.POST, webURL+"task/confirm", parameters: parameters, encoding: .JSON).responseJSON{ response in
            print(response.result.value)
            dispatch_async(dispatch_get_main_queue(), { () -> Void in
                let alert: UIAlertView = UIAlertView(title: "Success", message: "You have successfully confirmed", delegate: nil, cancelButtonTitle: "OK")
                alert.show()
            })
            
            
        }
    }
    
    func reject(taskId:String,posterId:String,hunterId:String){
        let parameters =
        ["taskId" : taskId,
            "posterId" : posterId,
            "hunterId": hunterId
        ]
        Alamofire.request(.POST, webURL+"task/reject", parameters: parameters, encoding: .JSON).responseJSON{ response in
            print(response.result.value)
            dispatch_async(dispatch_get_main_queue(), { () -> Void in
                let alert: UIAlertView = UIAlertView(title: "Success", message: "You have successfully reject application", delegate: nil, cancelButtonTitle: "OK")
                alert.show()
            })
            
        }
    }
    
    func rate(taskId:String,posterId:String){
        print(self.star.value)
        let parameters =
        ["taskId" : taskId,
            "posterId" : posterId,
            "grade": Int(self.star.value)
        ]
        Alamofire.request(.POST, webURL+"task/finish",parameters:parameters as? [String : AnyObject], encoding: .JSON).responseJSON { response in
            print(response.result.value)
            dispatch_async(dispatch_get_main_queue(), { () -> Void in
                let alert: UIAlertView = UIAlertView(title: "Success", message: "You have successfully rated this hunter", delegate: nil, cancelButtonTitle: "OK")
                alert.show()
            })
            
        }
    }
    
    func setStar(){
        self.star = StarReview(frame: CGRect(x: 15, y: 330, width: 300, height: 50))
        star.starMarginScale = 0.3
        star.value = 5
        star.starCount = 10
        star.allowAccruteStars = false
        star.allowAccruteStars = false
        star.starFillColor = UIColor(rgba: "#28b4b4")
        self.view.addSubview(star)
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "Bid Detail"
        self.setStar()
        taskId = NSUserDefaults.standardUserDefaults().stringForKey("taskId")
        bidName = NSUserDefaults.standardUserDefaults().stringForKey("bidName")
        bidPrice = NSUserDefaults.standardUserDefaults().stringForKey("bidPrice")
        bidderId = NSUserDefaults.standardUserDefaults().stringForKey("bidderId")
        bidderGrade = NSUserDefaults.standardUserDefaults().stringForKey("bidderGrade")
        bidUrl = NSUserDefaults.standardUserDefaults().stringForKey("bidUrl")
        
        self.bidderName.text = bidName
        self.bidPriceTextField.text = bidPrice
        self.bidderPhoneTextField.text = bidderId
        self.gradeTextField.text = bidderGrade
        
        self.bidderImageView.setZYHWebImage(bidUrl, defaultImage: "yoona", isCache: true)
        self.acceptButton.layer.cornerRadius = 10
        self.rejectButton.layer.cornerRadius = 10
        self.rateButton.layer.cornerRadius = 10
        self.callButton.layer.cornerRadius = 10
        
        
        

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
