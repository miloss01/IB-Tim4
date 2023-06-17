import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LoginAuthService } from '../../auth/service/auth.service';

@Component({
  selector: 'app-toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.css']
})
export class ToolbarComponent implements OnInit {

  constructor (
    private readonly router: Router,
    private route: ActivatedRoute,
    private authService: LoginAuthService
  ) {}

  user: any = null

  ngOnInit(): void {
    this.authService.user$.subscribe((res: any) => {
      this.user = res
      console.log("pro")
      console.log(this.user)
    })
  }

  logout(): void {
    this.authService.logout()
    this.router.navigate(["/"])
  }
}
