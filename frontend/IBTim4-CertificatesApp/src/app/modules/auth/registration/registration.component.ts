import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { LoginAuthService } from '../service/auth.service';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit{

  constructor(private router: Router,
              private authService: LoginAuthService) { }

  registerAccountForm: FormGroup = new FormGroup({
  name: new FormControl('', Validators.required),
  surname: new FormControl('', Validators.required),
  email: new FormControl('', [Validators.email, Validators.required]),
  password: new FormControl('', Validators.required),
  confirmPassword: new FormControl('', Validators.required),
  code: new FormControl(),
  phone: new FormControl('', Validators.required),
  })

  codeSent: boolean = false
  code: string = ""
  matSelectValue: string = "email"

  ngOnInit(): void {
  }

  registerAccount(): void {
  if (!this.registerAccountForm.valid){
  alert("Please fill in the corect data first")
  return
  }
  if (this.registerAccountForm.value.password != this.registerAccountForm.value.confirmPassword) {
  alert("Lozinke nisu iste")
  return
  }
  let sender = this.registerAccountForm.value.email
    if (this.matSelectValue === 'phone') sender = this.registerAccountForm.value.phone
    this.authService.verifyCode({phone: sender, code: this.registerAccountForm.value.code}).subscribe((res: any) => {     
      console.log(res)
    },
    (err: any) => {
      console.log(err)
      alert("Fail")
    })
 
  }
  sendCode() {
    if (!this.registerAccountForm.valid){
      alert("Please fill in the corect data first")
      return
      }
    if (this.matSelectValue === 'phone'){
      this.authService.sendPhoneCode(this.registerAccountForm.value.phone).subscribe((res: any) => {     
        console.log(res)
      },
      (err: any) => {
        console.log(err)
      })
    } else {
        this.authService.sendEmailCode(this.registerAccountForm.value.email).subscribe((res: any) => {     
          console.log(res)
        },
        (err: any) => {
          console.log(err)
        })
    }
  }

}
