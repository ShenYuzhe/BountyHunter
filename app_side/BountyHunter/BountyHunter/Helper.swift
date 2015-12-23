//
//  Helper.swift
//  BountyHunter
//
//  Created by junchao zhang on 12/7/15.
//  Copyright © 2015 junchao zhang. All rights reserved.
//

import Foundation

func imageResize (imageObj:UIImage, sizeChange:CGSize)-> UIImage{
    
    let hasAlpha = false
    let scale: CGFloat = 0.0 // Automatically use scale factor of main screen
    
    UIGraphicsBeginImageContextWithOptions(sizeChange, !hasAlpha, scale)
    imageObj.drawInRect(CGRect(origin: CGPointZero, size: sizeChange))
    
    let scaledImage = UIGraphicsGetImageFromCurrentImageContext()
    return scaledImage
}