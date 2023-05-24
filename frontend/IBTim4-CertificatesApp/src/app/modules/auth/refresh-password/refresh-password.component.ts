import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { LoginAuthService } from '../service/auth.service';

@Component({
  selector: 'app-refresh-password',
  templateUrl: './refresh-password.component.html',
  styleUrls: ['./refresh-password.component.css']
})
export class RefreshPasswordComponent implements OnInit {

  constructor (
    private readonly router: Router, 
    private readonly authService: LoginAuthService) { }

  token: string | null = ""
  errorMsg: string = ""

  refreshPasswordForm = new FormGroup({
    pass: new FormControl(),
    confirmPass: new FormControl()
  })

  ngOnInit(): void {
    this.token = localStorage.getItem("user")
  }

  refresh(): void {
    if (this.refreshPasswordForm.value.pass != this.refreshPasswordForm.value.confirmPass) {
      this.errorMsg = "Passwords must match"
      return
    }

    this.authService.refreshPassword(this.refreshPasswordForm.value.pass).subscribe((res: any) => {
      console.log("promenjeno")
      console.log(res)
      localStorage.removeItem("refreshPassword")
      localStorage.removeItem("user")
      this.router.navigate(['/login'])
    })
  }

}
