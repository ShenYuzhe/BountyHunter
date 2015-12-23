//
//  LoginController.swift
//  BountyHunter
//
//  Created by junchao zhang on 12/7/15.
//  Copyright © 2015 junchao zhang. All rights reserved.
//

import UIKit

class LoginController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationController?.navigationBarHidden = true

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func Signin(sender: UIButton) {
        self.login()
    }
    
    
    func login(){
        let appDelegate = UIApplication.sharedApplication().delegate as? AppDelegate
        appDelegate?.connectServer({ () -> Void in
            print("link successfully2")
            
        })
        self.performSegueWithIdentifier("toHome", sender: self)
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */
    
    @IBAction func cancelToLoginViewController(segue:UIStoryboardSegue) {
        print("go back")
    }
    
    @IBAction func finishRegister(segue:UIStoryboardSegue) {
        print("finish register")
    }

}
