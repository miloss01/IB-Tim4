import { Component } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { LoginAuthService } from '../service/auth.service';
import { MatDialog } from '@angular/material/dialog';
import { ChangePasswordDialogComponent } from '../change-password-dialog/change-password-dialog.component';

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
    username: new FormControl(),
    password: new FormControl()
  })

  errorMessage: string = ""
  codeSent: boolean = false
  code: string = ""
  matSelectValue: string = "email"
  usersEmail: string = ""
  usersPhone: string = ""
  userres:any = ''


  ngOnInit(): void {
  }

  loginUser (): void {
    this.authService.login({
      email: this.loginForm.value.username,
      password: this.loginForm.value.password
    }).subscribe((res: any) => {
      this.errorMessage = "Please verify your code"
      this.codeSent = true      
      console.log(res)
      this.userres = res
      localStorage.setItem('user', JSON.stringify(res.accessToken))
      this.authService.setUser()
      console.log(this.authService.getRole())
      this.usersEmail = this.authService.getEmail()
      this.usersPhone = this.authService.getPhone()
      console.log("AAAAAAAAAAAAA")
      console.log(this.usersEmail)
      this.authService.logout()
      console.log(this.usersEmail)

      this.sendCode()
    },
    (err: any) => {
      console.log(err)
      this.errorMessage = err.error.message
    })
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
    const dialog = this.changePasswordDialog.open(ChangePasswordDialogComponent, {data: {sender:this.loginForm.value.username, type: this.matSelectValue}})

  }


}
