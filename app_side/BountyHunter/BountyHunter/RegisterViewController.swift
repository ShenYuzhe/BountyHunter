//
//  RegisterViewController.swift
//  BountyHunter
//
//  Created by junchao zhang on 12/7/15.
//  Copyright © 2015 junchao zhang. All rights reserved.
//

import UIKit
import Alamofire
import MobileCoreServices
import VPImageCropper


class RegisterViewController: UIViewController, VPImageCropperDelegate,UIImagePickerControllerDelegate,UIAlertViewDelegate, UINavigationControllerDelegate {
    
    
    @IBOutlet weak var phoneField: UITextField!
    @IBOutlet weak var passwordField: UITextField!
    @IBOutlet weak var repasswordField: UITextField!
    @IBOutlet weak var doneButton: UIBarButtonItem!
    @IBOutlet weak var userNameField: UITextField!
    
    
    @IBOutlet weak var portriatImageView: UIImageView!
    let tapRec1 = UITapGestureRecognizer()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.portriatImageView.addGestureRecognizer(tapRec1)
        tapRec1.addTarget(self, action: "editPortrait")
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func register(){
        print("register")
        //self.progressHUD.show()
        let image = imageResize(self.portriatImageView.image!,sizeChange: CGSizeMake(200,200))
        let imageData = UIImagePNGRepresentation(image)
        print(String(stringInterpolationSegment: NSDate().timeIntervalSince1970))
        Alamofire.upload(
            .POST,
            "http://160.39.254.207:8080/BountyHunter/Register",
            multipartFormData: { multipartFormData in
                multipartFormData.appendBodyPart(data:self.phoneField.text!.dataUsingEncoding(NSUTF8StringEncoding, allowLossyConversion: false)!, name :"userId")
                multipartFormData.appendBodyPart(data:self.userNameField.text!.dataUsingEncoding(NSUTF8StringEncoding, allowLossyConversion: false)!, name :"userName")
                multipartFormData.appendBodyPart(data:self.passwordField.text!.dataUsingEncoding(NSUTF8StringEncoding, allowLossyConversion: false)!, name :"password")
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
                    }
                case .Failure(let encodingError):
                    //self.progressHUD.hide()
                    dispatch_async(dispatch_get_main_queue(), { () -> Void in
                        let alert: UIAlertView = UIAlertView(title: "Error", message: "Your profile is updated unsuccessfully", delegate: nil, cancelButtonTitle: "OK")
                        alert.show()
                    })
                    print("error")
                }
            }
        )
    }
    
    
    @IBAction func finishRegister(sender: UIBarButtonItem) {
        print("finish register1")
        self.register()
    }
    
    
    func editPortrait() {
        print("edit portrait")
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
        self.portriatImageView.image = editedImage
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

    
    

    

    
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
        if sender as! UIBarButtonItem == self.doneButton{
            print("test done button")
//            if(self.register(self.phoneField.text!,password:self.passwordField.text!)){
//                NSUserDefaults.standardUserDefaults().setObject(self.phoneField.text, forKey: "phoneNumber")
//                NSUserDefaults.standardUserDefaults().setObject(self.passwordField.text, forKey: "password")
//            }
//            NSUserDefaults.standardUserDefaults().synchronize()
        }
    }


}
