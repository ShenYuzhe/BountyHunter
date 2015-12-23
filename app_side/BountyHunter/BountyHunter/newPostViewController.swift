//
//  newPostViewController.swift
//  BountyHunter
//
//  Created by junchao zhang on 12/9/15.
//  Copyright © 2015 junchao zhang. All rights reserved.
//

import UIKit
import Alamofire

class newPostViewController: UIViewController {

    @IBOutlet weak var postTextView: UITextView!
    
    @IBOutlet weak var priceTextField: UITextField!
    
    var tagGroup1 = TNCheckBoxGroup()
    var tagGroup2 = TNCheckBoxGroup()
    var tags = [String]()
    var test = "test"
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.setCheckBox()
        self.postTextView.layer.borderWidth = 1
        self.postTextView.layer.cornerRadius = 10
        self.postTextView.layer.borderColor = UIColor(rgba: "#28b4b4").CGColor
        self.priceTextField.layer.borderColor = UIColor(rgba: "#28b4b4").CGColor
        self.priceTextField.text = "12"
        

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func setCheckBox(){
        let coffee = TNCircularCheckBoxData()
        coffee.identifier = "coffee"
        coffee.labelText = "Coffee"
        coffee.checked = true
        coffee.borderColor = UIColor.blackColor()
        coffee.circleColor = UIColor.blackColor()
        coffee.borderRadius = 20
        coffee.circleRadius = 15
        
        let charge = TNCircularCheckBoxData()
        charge.identifier = "charge"
        charge.labelText = "Charge"
        charge.checked = false
        charge.borderColor = UIColor.blackColor()
        charge.circleColor = UIColor.blackColor()
        charge.borderRadius = 20
        charge.circleRadius = 15
        
        let borrow = TNCircularCheckBoxData()
        borrow.identifier = "borrow"
        borrow.labelText = "Borrow"
        borrow.checked = false
        borrow.borderColor = UIColor.blackColor()
        borrow.circleColor = UIColor.blackColor()
        borrow.borderRadius = 20
        borrow.circleRadius = 15
        
        let book = TNCircularCheckBoxData()
        book.identifier = "book"
        book.labelText = "Book"
        book.checked = false
        book.borderColor = UIColor.blackColor()
        book.circleColor = UIColor.blackColor()
        book.borderRadius = 20
        book.circleRadius = 15
        
        let laptop = TNCircularCheckBoxData()
        laptop.identifier = "laptop"
        laptop.labelText = "Laptop"
        laptop.checked = true
        laptop.borderColor = UIColor.blackColor()
        laptop.circleColor = UIColor.blackColor()
        laptop.borderRadius = 20
        laptop.circleRadius = 15
        
        let teach = TNCircularCheckBoxData()
        teach.identifier = "teach"
        teach.labelText = "Teach"
        teach.checked = false
        teach.borderColor = UIColor.blackColor()
        teach.circleColor = UIColor.blackColor()
        teach.borderRadius = 20
        teach.circleRadius = 15
        
        let yelp = TNCircularCheckBoxData()
        yelp.identifier = "yelp"
        yelp.labelText = "Yelp"
        yelp.checked = false
        yelp.borderColor = UIColor.blackColor()
        yelp.circleColor = UIColor.blackColor()
        yelp.borderRadius = 20
        yelp.circleRadius = 15
        
        self.tagGroup1 = TNCheckBoxGroup.init(checkBoxData: [coffee,charge,borrow,book], style: TNCheckBoxLayoutHorizontal)
        //tagGroup.
        tagGroup1.create()
        tagGroup1.position = CGPointMake(20, 270)

        self.tagGroup2 = TNCheckBoxGroup.init(checkBoxData: [laptop,teach,yelp], style: TNCheckBoxLayoutHorizontal)
        //tagGroup.
        tagGroup2.create()
        tagGroup2.position = CGPointMake(20, 310)
        
        self.view.addSubview(tagGroup1)
        self.view.addSubview(tagGroup2)
        print(tagGroup1.checkedCheckBoxes)
        print(tagGroup1.uncheckedCheckBoxes[0].identifier)
        
    }
    
    func postTask(){
        print(self.tagGroup1.checkedCheckBoxes)
        for i in self.tagGroup1.checkedCheckBoxes {
            self.tags.append(i.identifier!!)
            print("test")

        }
        for j in self.tagGroup2.checkedCheckBoxes{
            self.tags.append(j.identifier!!)
        }
        print(self.tags)
        
        let parameters =
        ["lat" : 50,
            "lng" : 50,
            "content" : self.postTextView.text,
            "price" : self.priceTextField.text!,
            "userId" : "19172426111",
            "tags" : self.tags
        ]
        
        print("click to post task1")
        Alamofire.request(.POST, webURL+"task/postreward", parameters:parameters as! [String : AnyObject], encoding: .JSON).responseJSON{ response in
            print(response)
            if((response.result.value?.valueForKey("responseCode"))! as! NSObject==200){
                dispatch_async(dispatch_get_main_queue(), { () -> Void in
                    let alert: UIAlertView = UIAlertView(title: "Success", message: "Post Successfully", delegate: nil, cancelButtonTitle: "OK")
                    alert.show()
                })
            }
        }
    }
    
    @IBAction func postBid(sender: UIBarButtonItem) {
        print("click to post task")
        self.postTask()
        
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
