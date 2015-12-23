//
//  ProgressHUD.swift
//  plusco2.0
//
//  Created by junchao zhang on 8/17/15.
//  Copyright (c) 2015-2015 We Plus Plus, Inc. All rights reserved.
//

import UIKit

class ProgressHUD: UIVisualEffectView {


    var text: String? {
        didSet {
            label.text = text
        }
    }
    let activityIndictor: UIActivityIndicatorView = UIActivityIndicatorView(activityIndicatorStyle: UIActivityIndicatorViewStyle.White)
    let label: UILabel = UILabel()
    let blurEffect = UIBlurEffect(style: .Dark)
    let vibrancyView: UIVisualEffectView
    
    init(text: String) {
        self.text = text

        self.vibrancyView = UIVisualEffectView(effect: UIVibrancyEffect(forBlurEffect: blurEffect))
        super.init(effect: blurEffect)
        self.setup()
    }
    
    required init?(coder aDecoder: NSCoder) {
        self.text = ""
        self.vibrancyView = UIVisualEffectView(effect: UIVibrancyEffect(forBlurEffect: blurEffect))
        super.init(coder: aDecoder)
        self.setup()
        
    }
    
    func setup() {
        contentView.addSubview(vibrancyView)
        vibrancyView.contentView.addSubview(activityIndictor)
        vibrancyView.contentView.addSubview(label)
        activityIndictor.startAnimating()
    }
    
    override func didMoveToSuperview() {
        super.didMoveToSuperview()
        
        if let superview = self.superview {
            
            let width = superview.frame.size.width / 2
            let height: CGFloat = 50.0
            self.frame = CGRectMake(superview.frame.size.width / 2 - width / 2,
                superview.frame.height / 2 - height / 2,
                width,
                height)
            vibrancyView.frame = self.bounds
            
            let activityIndicatorSize: CGFloat = 40
            activityIndictor.frame = CGRectMake(5, height / 2 - activityIndicatorSize / 2,
                activityIndicatorSize,
                activityIndicatorSize)
            
            layer.cornerRadius = 8.0
            layer.masksToBounds = true
            label.text = text
            label.textAlignment = NSTextAlignment.Center
            label.frame = CGRectMake(activityIndicatorSize + 5, 0, width - activityIndicatorSize - 15, height)
            label.textColor = UIColor.whiteColor()
            label.font = UIFont.boldSystemFontOfSize(16)
        }
    }
    
    func show() {
        self.hidden = false
    }
    
    func hide() {
        self.hidden = true
    }
    /*
    // Only override drawRect: if you perform custom drawing.
    // An empty implementation adversely affects performance during animation.
    override func drawRect(rect: CGRect) {
        // Drawing code
    }
    */
}
