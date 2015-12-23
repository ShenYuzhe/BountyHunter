//
//  EditProfileViewController.swift
//  BountyHunter
//
//  Created by junchao zhang on 12/19/15.
//  Copyright © 2015 junchao zhang. All rights reserved.
//

import UIKit
import Alamofire
import MobileCoreServices
import VPImageCropper

extension String
{
    func toDateTime() -> NSDate
    {
        //Create Date Formatter
        let dateFormatter = NSDateFormatter()
        
        //Specify Format of String to Parse
        dateFormatter.dateFormat = "yyyy-MM-dd"
        
        //Parse into NSDate
        let dateFromString : NSDate = dateFormatter.dateFromString(self)!
        
        //Return Parsed Date
        return dateFromString
    }
}

class EditProfileViewController: UIViewController,UITextFieldDelegate, VPImageCropperDelegate, UIImagePickerControllerDelegate, UIAlertViewDelegate, UINavigationControllerDelegate {
    
    @IBOutlet weak var portraitImageView: RoundImageView!
    @IBOutlet weak var userNameTextField: UITextField!
    @IBOutlet weak var dateofBirthLabel: UILabel!
    @IBOutlet weak var genderSegment: UISegmentedControl!
    var Gender: String?
    
    let tapRec1 = UITapGestureRecognizer()
    let tapRec2 = UITapGestureRecognizer()
    let progressHUD = ProgressHUD(text: "Saving Portrait")
    
    
    
    @IBOutlet weak var emailTextField: UITextField!
    
    @IBAction func genderChoice(sender: UISegmentedControl) {
        switch genderSegment.selectedSegmentIndex
        {
        case 0 :
            self.Gender = "male"
        case 1 :
            self.Gender = "female"
        default:
            break;
        }
    }
    
    func setdefaultDate(dateTime: String?) -> String {
        if (dateTime == "0000-00-00") {
            print("setdefaultDate")
            let currentDate = NSDate()
            let dateFormatter = NSDateFormatter()
            dateFormatter.dateFormat = "yyyy-MM-dd"
            let defualtTime = dateFormatter.stringFromDate(currentDate)
            return defualtTime
        } else {
            print("no setdefaultDate")
            return dateTime!
        }
    }
    
    func showPicker () {
        DatePickerDialog().show(title: "Pick Date", doneButtonTitle: "Done", cancelButtonTitle: "Cancel",defaultDate: self.setdefaultDate(self.dateofBirthLabel.text).toDateTime(),  datePickerMode: .Date) {
            (date) -> Void in
            let dateFormatter = NSDateFormatter()
            dateFormatter.dateFormat = "yyyy-MM-dd"
            let currentDate = NSDate()
            if date.compare(currentDate) == .OrderedAscending{
                let day = dateFormatter.stringFromDate(date)
                self.dateofBirthLabel.text = day}
            else {
                let alert: UIAlertView = UIAlertView(title: "Error", message: "Please enter true date of birth", delegate: nil, cancelButtonTitle: "OK")
                alert.show()
            }
        }
    }
    
    
    @IBAction func saveProfile(sender: UIButton) {
        print("update profile")
        self.progressHUD.show()
        let image = imageResize(self.portraitImageView.image!,sizeChange: CGSizeMake(200,200))
        let imageData = UIImagePNGRepresentation(image)
        print(String(stringInterpolationSegment: NSDate().timeIntervalSince1970))
        Alamofire.upload(
            .POST,
            webURL+"ProfileUpdater",
            multipartFormData: { multipartFormData in
                multipartFormData.appendBodyPart(data:"19172426111".dataUsingEncoding(NSUTF8StringEncoding, allowLossyConversion: false)!, name :"userId")
                multipartFormData.appendBodyPart(data:self.userNameTextField.text!.dataUsingEncoding(NSUTF8StringEncoding, allowLossyConversion: false)!, name :"userName")
                multipartFormData.appendBodyPart(data:self.Gender!.dataUsingEncoding(NSUTF8StringEncoding, allowLossyConversion: false)!, name :"sex")
                multipartFormData.appendBodyPart(data:self.dateofBirthLabel.text!.dataUsingEncoding(NSUTF8StringEncoding, allowLossyConversion: false)!, name :"birthday")
                multipartFormData.appendBodyPart(data:self.emailTextField.text!.dataUsingEncoding(NSUTF8StringEncoding, allowLossyConversion: false)!, name :"email")
                multipartFormData.appendBodyPart(
                    data: imageData!,
                    name: "file",
                    fileName: String(stringInterpolationSegment: NSDate().timeIntervalSince1970) + ".png",
                    mimeType: "image/png"
                )
            },
            encodingCompletion: { encodingResult in
                switch encodingResult {
                case .Success(let upload, _, _):
                    upload.responseJSON { response in
                        print(response)
                        self.progressHUD.hide()
                        dispatch_async(dispatch_get_main_queue(), { () -> Void in
                            let alert: UIAlertView = UIAlertView(title: "Success", message: "Your profile is updated successfully", delegate: nil, cancelButtonTitle: "OK")
                            alert.show()
                        })
                    }
                case .Failure(let encodingError):
                    self.progressHUD.hide()
                    dispatch_async(dispatch_get_main_queue(), { () -> Void in
                        let alert: UIAlertView = UIAlertView(title: "Error", message: "Your profile is updated unsuccessfully", delegate: nil, cancelButtonTitle: "OK")
                        alert.show()
                    })
                    print("error")
                }
            }
        )
    }
    
    
    
    func getProfile(){
        Alamofire.request(.GET, webURL+"profile/19172426111").responseJSON{ response in
            print(response.result.value!.valueForKey("profile")!.valueForKey("userName"))
            self.userNameTextField.text = response.result.value!.valueForKey("profile")!.valueForKey("userName") as? String
            if(response.result.value!.valueForKey("profile")!.valueForKey("birthday") as? String != ""){
                self.dateofBirthLabel.text = response.result.value!.valueForKey("profile")!.valueForKey("birthday") as? String
            }
            self.emailTextField.text = response.result.value!.valueForKey("profile")!.valueForKey("email") as? String
            if(response.result.value!.valueForKey("profile")!.valueForKey("sex") as? String == "female"){
                self.Gender = "female"
                self.genderSegment.selectedSegmentIndex = 1
            } else {
                self.genderSegment.selectedSegmentIndex = 0
                self.Gender = "male"
            }
            
            self.portraitImageView.setZYHWebImage(response.result.value!.valueForKey("profile")!.valueForKey("iconUrl") as? String, defaultImage: "yoona", isCache: true)
            

            
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "Edit Profile"
        tapRec1.addTarget(self, action: "showPicker")
        self.dateofBirthLabel.addGestureRecognizer(tapRec1)
        self.view.addSubview(progressHUD)
        tapRec2.addTarget(self, action: "editPortrait")
        self.portraitImageView.addGestureRecognizer(tapRec2)
        self.progressHUD.layer.zPosition = 400
        self.progressHUD.hide()
        self.getProfile()
        //self.userNameTextField.text = "test"

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func imageResize (imageObj:UIImage, sizeChange:CGSize)-> UIImage{
        
        let hasAlpha = false
        let scale: CGFloat = 0.0 // Automatically use scale factor of main screen
        
        UIGraphicsBeginImageContextWithOptions(sizeChange, !hasAlpha, scale)
        imageObj.drawInRect(CGRect(origin: CGPointZero, size: sizeChange))
        
        let scaledImage = UIGraphicsGetImageFromCurrentImageContext()
        return scaledImage
    }
    
    
    func editPortrait() {
        print("work")
        let choiceSheet = UIAlertController(title: "Portrait", message: "edit", preferredStyle: UIAlertControllerStyle.ActionSheet)
        let cancelAction: UIAlertAction = UIAlertAction(title: "Cancel", style: .Cancel) { action -> Void in
            choiceSheet.dismissViewControllerAnimated(true, completion: nil)
            //Just dismiss the action sheet
        }
        choiceSheet.addAction(cancelAction)
        
        let takePictureAction: UIAlertAction = UIAlertAction(title: "take pictures", style: .Default) { action -> Void in
            //Code for launching the camera goes here
            if (self.isCameraAvailable()) && (self.doesCameraSupportTakingPhotos()){
                let controller = UIImagePickerController()
                controller.sourceType = UIImagePickerControllerSourceType.Camera
                if self.isFrontCameraavailable() {
                    controller.cameraDevice = UIImagePickerControllerCameraDevice.Front
                }
                var mediaTypes = [String]()
                mediaTypes.append(kUTTypeImage as String)
                controller.mediaTypes = mediaTypes
                controller.delegate = self
                self.presentViewController(controller, animated: true, completion: { () -> Void in
                    print("picker view controller is presented")
                })
            }
            
        }
        choiceSheet.addAction(takePictureAction)
        
        let choosePictureAction: UIAlertAction = UIAlertAction(title: "Choose From Album", style: .Default) { action -> Void in
            //Code for picking from camera roll goes here
            print("here")
            if (self.isPhotoLibraryAvailable()) {
                let controller = UIImagePickerController()
                controller.sourceType = UIImagePickerControllerSourceType.PhotoLibrary
                var mediaTypes = [String]()
                mediaTypes.append(kUTTypeImage as String)
                controller.mediaTypes = mediaTypes
                controller.delegate = self
                self.presentViewController(controller, animated: true, completion: { () -> Void in
                    print("picker view controller is presented")
                })
                print("photo ava")
                
            }
        }
        choiceSheet.addAction(choosePictureAction)
        self.presentViewController(choiceSheet, animated: true, completion: nil)
    }
    
    func imageCropper(cropperViewController: VPImageCropperViewController!, didFinished editedImage: UIImage!) {
        self.portraitImageView.image = editedImage
        cropperViewController.dismissViewControllerAnimated(true, completion: nil)
        
    }
    
    func imageCropperDidCancel(cropperViewController: VPImageCropperViewController!) {
        cropperViewController.dismissViewControllerAnimated(true, completion: { () -> Void in
            
        })
    }
    
    
    func imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : AnyObject]) {
        picker.dismissViewControllerAnimated(true, completion: { () -> Void in
            let image = info[UIImagePickerControllerOriginalImage] as! UIImage
            let imgCropperVC = VPImageCropperViewController(image: image, cropFrame: CGRectMake(0.0, 100.0, self.view.frame.size.width, self.view.frame.size.width), limitScaleRatio: 20)
            imgCropperVC.delegate = self
            imgCropperVC.confirmTitle = "confirm"
            imgCropperVC.cancelTitle = "cancell"
            imgCropperVC.btnBgColor = UIColor.clearColor()
            self.presentViewController(imgCropperVC, animated: true, completion: { () -> Void in
                
            })
        })
    }
    
    
    func cameraSupportsMedia(mediaType: String,
        sourceType: UIImagePickerControllerSourceType) -> Bool{
            
            let availableMediaTypes =
            UIImagePickerController.availableMediaTypesForSourceType(sourceType)
            
            if let types = availableMediaTypes{
                for type in types{
                    if type == mediaType{
                        return true
                    }
                }
            }
            return false
    }
    
    func isCameraAvailable() -> Bool {
        return UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.Camera)
    }
    func isRearCameraAvailabel() -> Bool{
        return UIImagePickerController.isCameraDeviceAvailable(UIImagePickerControllerCameraDevice.Rear)
    }
    func isFrontCameraavailable() -> Bool {
        return UIImagePickerController.isCameraDeviceAvailable(UIImagePickerControllerCameraDevice.Front)
    }
    func doesCameraSupportTakingPhotos() -> Bool{
        return cameraSupportsMedia(kUTTypeImage as String, sourceType: .Camera)
    }
    func isPhotoLibraryAvailable() -> Bool{
        return UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.Camera)
    }
    func canUserPickVideosFromPhotoLibrary() -> Bool {
        return cameraSupportsMedia(kUTTypeImage as String, sourceType: UIImagePickerControllerSourceType.PhotoLibrary)
    }
    
    override func touchesBegan(touches: Set<UITouch>, withEvent event: UIEvent?) {
        self.view.endEditing(true)
    }
    
    func textFieldShouldReturn(textField: UITextField) -> Bool {
        self.view.endEditing(true)
        self.view.frame = CGRect(x: 0, y: 0, width: self.view.frame.width, height: self.view.frame.height)
        return false
    }
    
    
    func textFieldDidBeginEditing(textField: UITextField) {
        self.view.frame = CGRect(x: 0, y: -120, width: self.view.frame.width, height: self.view.frame.height)
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
