import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators, NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { LoginAuthService } from '../service/auth.service';
import { MatDialog } from '@angular/material/dialog';
import { ChangePasswordDialogComponent } from '../change-password-dialog/change-password-dialog.component';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  constructor (
    private readonly router: Router, 
    private readonly authService: LoginAuthService,
    public changePasswordDialog: MatDialog) { }
    isDisabled: boolean = false

  loginForm = new FormGroup({
    username: new FormControl('', [Validators.required]),
    password: new FormControl(''),
    recaptcha: new FormControl()
  })

  errorMessage: string = ""
  codeSent: boolean = false
  code: string = ""
  matSelectValue: string = "email"
  usersEmail: string = ""
  usersPhone: string = ""
  userres:any = ''

  siteKey: string =  environment.recaptcha.siteKey;
  token: string = '';

  ngOnInit(): void {
  }

  withGoogle(): void {
    window.location.href = `${environment.apiHost}user/google/oauth`;
  }

  loginUser (): void {

    if (this.loginForm.invalid) {
      this.loginForm.controls.username.markAllAsTouched();
      return;
    }

    this.authService.validateCaptcha(this.token).subscribe((res: any) => {

      this.authService.login({
        email: this.loginForm.value.username,
        password: this.loginForm.value.password
      }).subscribe((res: any) => {

        if (res.refreshPassword) {
          localStorage.setItem('user', JSON.stringify(res.accessToken))
          localStorage.setItem('refreshPassword', "refresh")
          this.router.navigate(['/refresh-password'])
          return
        }

        this.errorMessage = "Please verify your code"
        this.codeSent = true      
        console.log(res)
        this.userres = res
        localStorage.setItem('user', JSON.stringify(res.accessToken))
        this.authService.setUser()

        this.usersEmail = this.authService.getEmail()
        this.usersPhone = this.authService.getPhone()

        this.authService.logout()
        this.sendCode()
      },
      (err: any) => {
        console.log(err)
        this.errorMessage = err.error.message
      })

    },(err: any) => {
      if (err.status == 400)  alert("Captcha error. Invalid captcha.")
    });

  }

  sendCode() {
    if (this.matSelectValue === 'phone'){
      this.authService.sendPhoneCode(this.usersPhone).subscribe((res: any) => {     
        console.log(res)
        this.errorMessage = res
      },
      (err: any) => {
        console.log(err)
        this.errorMessage = err.error.message
      })
    } else {
        this.authService.sendEmailCode(this.usersEmail).subscribe((res: any) => {     
          console.log(res)
          this.errorMessage = res
        },
        (err: any) => {
          console.log(err)
          this.errorMessage = err.error.message
        })
    }
  }

  verifyCode():void {
    let sender = this.usersEmail
    if (this.matSelectValue === 'phone') sender = this.usersPhone
    this.authService.verifyCode({phone: sender, code: this.code}).subscribe((res: any) => {     
      console.log(res)
      this.errorMessage = res
      localStorage.setItem('user', JSON.stringify(this.userres.accessToken))
      this.authService.setUser()
      console.log(this.authService.getRole())
    },
    (err: any) => {
      console.log(err)
      this.errorMessage = "Verification failed"
    })
  }

  goToRegister (): void { this.router.navigate(['/registration']) }

  changePassword(){
    this.sendCodePswChange()
    const dialog = this.changePasswordDialog.open(ChangePasswordDialogComponent, {data: {sender:this.loginForm.value.username, type: this.matSelectValue}})
  }

  sendCodePswChange() {
    if (this.matSelectValue === 'phone'&& this.loginForm.value.username){
      this.authService.sendPhoneCode(this.loginForm.value.username).subscribe((res: any) => {     
        console.log(res)
        this.errorMessage = res
      },
      (err: any) => {
        console.log(err)
        this.errorMessage = err.error.message
      })
    } else if(this.loginForm.value.username){
        this.authService.sendEmailCode(this.loginForm.value.username).subscribe((res: any) => {     
          console.log(res)
          this.errorMessage = res
        },
        (err: any) => {
          console.log(err)
          this.errorMessage = err.error.message
        })
    }
  }

}
