//
//  structFile.swift
//  BountyHunter
//
//  Created by junchao zhang on 12/7/15.
//  Copyright © 2015 junchao zhang. All rights reserved.
//

import Foundation

struct nearbyTask{
    var content: String?
    var price: Int?
    var distance: String?
    var location: String?
    var iconUrl: String?
    var userName: String?
    var taskId: String?
    var posterId: String?
}


struct myHistory{
    var taskId: String?
    var taskcontent: String?
    var time: String?
    var status: String?
    var price: Int?
    var hunterURL: String?
    var hunterId: String?
    var hunterName: String?
    var hunterGrade: Int?
}

struct Profile{
    var birthday: String?
    var sex: String?
    var email: String?
    var iconUrl: String?
    var userName: String?
}
