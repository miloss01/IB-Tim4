import { Component, OnInit } from '@angular/core';
import { AuthService } from '@auth0/auth0-angular';
import { CertificateRequestDTO } from 'src/app/models/models';
import { CertService } from 'src/app/services/cert-service.service';
import { LoginAuthService } from '../../auth/service/auth.service';

@Component({
  selector: 'app-request-overview',
  templateUrl: './request-overview.component.html',
  styleUrls: ['./request-overview.component.css']
})
export class RequestOverviewComponent implements OnInit{

  requests: CertificateRequestDTO[] = []
  adminLoggedIn: boolean = false;

  constructor( 
    private certService: CertService,
    private authService: LoginAuthService
  ) {
    this.adminLoggedIn = authService.getRole() == 'ADMIN'
  }

  ngOnInit(): void {
  }

  ngAfterViewInit (): void {
    this.certService.getRequestsForOverview().subscribe(res => {
      this.requests = res
    })
  }

}
