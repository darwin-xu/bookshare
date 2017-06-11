//
//  LoginViewController.swift
//  bookshare
//
//  Created by Darwin Xu on 2017/5/25.
//  Copyright © 2017年 darwin. All rights reserved.
//

import Foundation
import SwiftyBeaver

class LoginViewController: UIViewController {

    @IBOutlet weak var phoneNumber: UITextField!
    @IBOutlet weak var password: UITextField!
    @IBOutlet weak var verifyCode: UITextField!
    @IBOutlet weak var verifyCodeLabel: UILabel!
    @IBOutlet weak var constraintPhoneVerifyCode: NSLayoutConstraint!
    @IBOutlet weak var constraintVerifyCodeHeight: NSLayoutConstraint!
    @IBOutlet weak var signUpButton: UIButton!

    var timeout = 0
    var changePassword = false
    var timer: Timer?

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        verifyCodeLabel.alpha = 0
        verifyCode.alpha = 0
        constraintVerifyCodeHeight.constant = 0
        constraintPhoneVerifyCode.constant = 0
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func signIn(_ sender: Any) {
        SwiftyBeaver.debug(phoneNumber.text! + ":" +  verifyCode.text! + ":" + password.text!)

        if changePassword {
            DataService.changePassword(for: phoneNumber.text!,
                                       verifyCode: verifyCode.text!,
                                       password: password.text!,
                                       callback: {result in
                                        if result {
                                            DataService.login(for: self.phoneNumber.text!,
                                                              password: self.password.text!,
                                                              callback: {cookie in
                                                                guard let cookie = cookie else {
                                                                    return
                                                                }
                                                                print("login ok: ")
                                                                print(cookie)
                                            })
                                        } else {
                                            let alert = UIAlertController(title: "Error",
                                                                          message: "Change password error, please check your verify code",
                                                                          preferredStyle: .alert)
                                            alert.addAction(UIAlertAction(title: "Ok", style: .default))
                                            self.present(alert, animated: true, completion: nil)
                                        }
            })
        } else {
            DataService.login(for: self.phoneNumber.text!,
                              password: self.password.text!,
                              callback: {cookie in
                                guard let cookie = cookie else {
                                    let alert = UIAlertController(title: "Error",
                                                                  message: "Login error, phone number or password is wrong.",
                                                                  preferredStyle: .alert)
                                    alert.addAction(UIAlertAction(title: "Ok", style: .default))
                                    return
                                }
                                print("login ok: ")
                                print(cookie)
            })
        }

    }

    @IBAction func signUp(_ sender: Any) {
        let alert = UIAlertController(title: "Notice",
                                      message: "This will send the verify code to " + phoneNumber.text!,
                                      preferredStyle: .alert)

        // Add Actions
        alert.addAction(UIAlertAction(title: "Yes", style: .default) { _ in
            DataService.getVerifyCode(for: self.phoneNumber.text!) { result in
                if result {
                    DispatchQueue.main.async {
                        self.timeout = 30
                        self.signUpButton.isEnabled = false
                        self.timer = Timer.scheduledTimer(withTimeInterval: 1,
                                                          repeats: true) {_ in
                                                            DispatchQueue.main.async {
                                                                self.signUpButton.setTitle(String(self.timeout) + "S",
                                                                                           for: .disabled)
                                                            }
                                                            self.timeout -= 1
                                                            if self.timeout == 0 {
                                                                DispatchQueue.main.async {
                                                                    self.signUpButton.isEnabled = true
                                                                }
                                                                self.timer!.invalidate()
                                                            }
                        }
                        self.phoneNumber.isEnabled = false
                        self.changePassword = true
                        UIView.animate(withDuration: 0.3,
                                       animations: {
                                        self.verifyCodeLabel.alpha = 1
                                        self.verifyCode.alpha = 1
                                        self.constraintVerifyCodeHeight.constant = 20
                                        self.constraintPhoneVerifyCode.constant = 20
                                        self.view.layoutIfNeeded()
                        })
                    }
                }
            }
        })
        alert.addAction(UIAlertAction(title: "No", style: .default) { _ in
        })

        self.present(alert, animated: true, completion: nil)
    }

}
