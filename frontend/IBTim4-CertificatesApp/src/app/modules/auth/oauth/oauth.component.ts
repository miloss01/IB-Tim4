import { Component, OnInit } from '@angular/core';
import { LoginAuthService } from '../service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-oauth',
  templateUrl: './oauth.component.html',
  styleUrls: ['./oauth.component.css']
})
export class OauthComponent implements OnInit {

  constructor(
    private authService: LoginAuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.withGoogle().subscribe((res: any) => {

      localStorage.setItem('user', JSON.stringify(res.accessToken))
      this.authService.setUser()
      console.log("ulogavao se")

    });
  }

}
