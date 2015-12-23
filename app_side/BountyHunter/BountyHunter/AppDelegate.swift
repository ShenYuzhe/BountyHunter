//
//  AppDelegate.swift
//  BountyHunter
//
//  Created by junchao zhang on 11/11/15.
//  Copyright © 2015 junchao zhang. All rights reserved.
//

import UIKit
import Alamofire

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate, RCIMUserInfoDataSource {
    
    var token = "GPG2NfqY5TXrr56z80q7EJyra1wQGG2bFrlFhVzPgfLD5RKyxcPmxsEBWD4mOa1BYK4oVR2LdZdYOjKhzc5+3Th9y4FH9UPh"
    var username = "vinokimi"
    var userID = "19172426111"
    var window: UIWindow?
    
    func getUserInfoWithUserId(userId: String!, completion: ((RCUserInfo!) -> Void)!) {
        let userInfo = RCUserInfo()
        userInfo.userId = userId
        Alamofire.request(.GET, webURL+"/profile/"+userId)
            .responseJSON() { response in
                if (response.result.value != nil) {
                    let iconUrl  = response.result.value!.valueForKey("profile")!.valueForKey("iconUrl")
                    let userName = response.result.value!.valueForKey("profile")!.valueForKey("userName")
                    userInfo.name = userName as! String
                    userInfo.portraitUri = iconUrl as! String
                    return completion(userInfo)
                }
        }
//        userInfo.name = username
//        userInfo.portraitUri = "http://img.name2012.com/uploads/allimg/2015-06/30-023131_451.jpg"
        return completion(userInfo)
    }
    

    func connectServer(completion:()->Void){
        
        //init appKey
        //RCIM.sharedRCIM().initWithAppKey("pvxdm17jx88cr")
        //self.username = NSUserDefaults.standardUserDefaults().stringForKey("userName")!
        //self.username = "kimi"
        //self.token = NSUserDefaults.standardUserDefaults().stringForKey("userToken")!
        //self.userID = NSUserDefaults.standardUserDefaults().stringForKey("userID")!
        
        
        
        //connect to rongcloud with token
        RCIM.sharedRCIM().connectWithToken(self.token, success: { (str:String!) -> Void in
            
            let currentUserInfo = RCUserInfo(userId: self.userID, name: self.username, portrait:nil)
            //let currentUserInfo = RCUserInfo(userId: "kimi", name: "kimi", portrait:nil)
            RCIMClient.sharedRCIMClient().currentUserInfo = currentUserInfo
            
            print("link successfully1")
            
            dispatch_async(dispatch_get_main_queue(), { () -> Void in
                completion()
            })
            
            
            }, error: { (code:RCConnectErrorCode) -> Void in
                print("link unsuccessfully\(code)")
            }) { () -> Void in
                print("incorrect token")
        }
    }

    func application(application: UIApplication, didFinishLaunchingWithOptions launchOptions: [NSObject: AnyObject]?) -> Bool {
        // Override point for customization after application launch.
        RCIM.sharedRCIM().initWithAppKey("3argexb6r2dke")
        RCIM.sharedRCIM().userInfoDataSource = self
        return true
    }

    func applicationWillResignActive(application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
    }

    func applicationDidEnterBackground(application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }

    func applicationWillEnterForeground(application: UIApplication) {
        // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
    }

    func applicationDidBecomeActive(application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }

    func applicationWillTerminate(application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }


}

