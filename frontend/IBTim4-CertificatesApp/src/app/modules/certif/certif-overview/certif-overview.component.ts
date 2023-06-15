import { Component, OnInit, AfterViewInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { CertifDTO } from 'src/app/models/models';
import { CertService } from 'src/app/services/cert-service.service';
import { LoginAuthService } from '../../auth/service/auth.service';

@Component({
  selector: 'app-certif-overview',
  templateUrl: './certif-overview.component.html',
  styleUrls: ['./certif-overview.component.css']
})
export class CertifOverviewComponent implements AfterViewInit, OnInit {

  certs: CertifDTO[] = []
  reason: string = ""
  loggedIn: string = ""
  loggedInRole: string = ""

  constructor( 
    private certService: CertService,
    private authService: LoginAuthService
  ) {}

  ngOnInit(): void {
    this.loggedIn = this.authService.getEmail()
    this.loggedInRole = this.authService.getRole()
    console.log("AAAAAAAAAAAAAAA")
  }

  ngAfterViewInit (): void {
    this.certService.getAll().subscribe(res => {
      this.certs = res
    })
  }

  downloadCert(serialNumber: number): void {
    this.certService.downloadCertificate(serialNumber).subscribe((data: any) => {
      const blob = new Blob([data], {
        type: 'application/zip'
      });
      const url = window.URL.createObjectURL(blob);
      window.open(url);
    })
  }

  retractCert(serialNumber: number): void {
    if (this.reason == "") {
      alert("reason must not be empty")
      return
    }
    
    this.certService.retractCertificate(serialNumber, this.reason).subscribe((res: any) => {
      alert("certificate retracted")
      this.certService.getAll().subscribe(res => {
        this.certs = res
      })
    })
  }

}
