import { Component, OnInit, AfterViewInit } from '@angular/core';
import { CertifDTO } from 'src/app/models/models';
import { CertService } from 'src/app/services/cert-service.service';

@Component({
  selector: 'app-certif-overview',
  templateUrl: './certif-overview.component.html',
  styleUrls: ['./certif-overview.component.css']
})
export class CertifOverviewComponent implements AfterViewInit, OnInit {

  certs: CertifDTO[] = []

  constructor( 
    private certService: CertService
  ) {}

  ngOnInit(): void {
  }

  ngAfterViewInit (): void {
    this.certService.getAll().subscribe(res => {
      this.certs = res
    })
  }

}
