import { Component } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { LoginAuthService } from '../service/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  constructor (
    private readonly router: Router, 
    private readonly authService: LoginAuthService) { }

  loginForm = new FormGroup({
    username: new FormControl(),
    password: new FormControl()
  })

  errorMessage: string = ''

  ngOnInit(): void {
  }

  loginUser (): void {
    this.authService.login({
      email: this.loginForm.value.username,
      password: this.loginForm.value.password
    }).subscribe((res: any) => {
      console.log(res)
      localStorage.setItem('user', JSON.stringify(res.accessToken))
      this.authService.setUser()
      console.log(this.authService.getRole())
    },
    (err: any) => {
      console.log(err)
      this.errorMessage = err.error.message
    })
  }

  goToRegister (): void { this.router.navigate(['/register-account']) }


}
