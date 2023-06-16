import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { LoginAuthService } from '../service/auth.service';
import { UserExpandedDTO } from 'src/app/models/models';
import { environment } from 'src/environments/environment';

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
  errorMessage: string = ""
  user:UserExpandedDTO = {
    name: '',
    lastName: '',
    email: ''
  }

  siteKey: string =  environment.recaptcha.siteKey;
  token: string = '';

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

    if (this.registerAccountForm.invalid) {
      this.registerAccountForm.controls['name'].markAllAsTouched();
      return;
    }

    this.authService.validateCaptcha(this.token).subscribe((res: any) => {

      this.registerAccountForm.disable()
      let sender = this.registerAccountForm.value.email
      if (this.matSelectValue === 'phone') sender = this.registerAccountForm.value.phone
      this.authService.verifyCode({phone: sender, code: this.registerAccountForm.value.code}).subscribe((res: any) => {     
        console.log(res)
        this.writeToBase()
      },
      (err: any) => {
        console.log(err)
        alert("Fail")
      })
      this.user = {
        name: this.registerAccountForm.value.name,
        lastName: this.registerAccountForm.value.surname,
        email: this.registerAccountForm.value.email, 
        phone: this.registerAccountForm.value.phone,
        password: this.registerAccountForm.value.password
      }

    },(err: any) => {
      if (err.status == 400)  alert("Captcha error. Invalid captcha.")
    });
    
 
  }

  writeToBase() {
    this.authService.register(this.user).subscribe((res: any) => {     
      console.log(res)
      alert("Account made! please log in")
      this.router.navigate(["/login"])
    },
    (err: any) => {
      console.log(err)
      this.errorMessage = "fail"
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
      this.errorMessage = 'Code for verification send'

      },
      (err: any) => {
        console.log(err)
        this.errorMessage = 'Code for verification send'

      })
    } else {
        this.authService.sendEmailCode(this.registerAccountForm.value.email).subscribe((res: any) => {     
          console.log(res)
          this.errorMessage = 'Code for verification send'

        },
        (err: any) => {
          console.log(err)
      this.errorMessage = 'Code for verification send'

        })
    }
  }

}
