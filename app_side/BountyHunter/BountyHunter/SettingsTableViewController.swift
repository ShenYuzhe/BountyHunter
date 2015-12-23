//
//  SettingsTableViewController.swift
//  BountyHunter
//
//  Created by junchao zhang on 12/16/15.
//  Copyright © 2015 junchao zhang. All rights reserved.
//

import UIKit
import Alamofire

class SettingsTableViewController: UITableViewController {
    let userID = NSUserDefaults.standardUserDefaults().stringForKey("userID")
    override func viewDidLoad() {
        super.viewDidLoad()

        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        switch indexPath.section {
        case 0:
            self.tableView.deselectRowAtIndexPath(indexPath, animated: true)
            switch indexPath.row{
            case 0:
                print("notifications")
            case 1:
                print("security")
                //self.performSegueWithIdentifier("changePassword", sender: self)
            default:
                print("no selection")
            }
            //self.performSegueWithIdentifier("EditExperience", sender: self)
        case 1:
            self.tableView.deselectRowAtIndexPath(indexPath, animated: true)
            switch indexPath.row {
            case 0:
                print("change password")
            case 1:
                print("choose clear cache")
                self.performSegueWithIdentifier("toEditBasics", sender: self)
            default:
                print("no selection")
            }
        case 2:
            self.tableView.deselectRowAtIndexPath(indexPath, animated: true)
            switch indexPath.row {
            case 0:
                print("terms")
            case 1:
                print ("private policy")
            case 2:
                print("website")
                UIApplication.sharedApplication().openURL(NSURL(string:"https://www.google.com/")!)
            case 3:
                print("contact us")
            default:
                print("no selection")
            }
        case 3:
            //print("select 2")
            self.tableView.deselectRowAtIndexPath(indexPath, animated: true)
            print("log out")
        default:
            print("no select")
            
        }
    }

    // MARK: - Table view data source

//    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
//        // #warning Incomplete implementation, return the number of sections
//        return 0
//    }
//
//    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
//        // #warning Incomplete implementation, return the number of rows
//        return 0
//    }

    /*
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("reuseIdentifier", forIndexPath: indexPath)

        // Configure the cell...

        return cell
    }
    */

    /*
    // Override to support conditional editing of the table view.
    override func tableView(tableView: UITableView, canEditRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    */

    /*
    // Override to support editing the table view.
    override func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath) {
        if editingStyle == .Delete {
            // Delete the row from the data source
            tableView.deleteRowsAtIndexPaths([indexPath], withRowAnimation: .Fade)
        } else if editingStyle == .Insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }
    */

    /*
    // Override to support rearranging the table view.
    override func tableView(tableView: UITableView, moveRowAtIndexPath fromIndexPath: NSIndexPath, toIndexPath: NSIndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(tableView: UITableView, canMoveRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    */

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
