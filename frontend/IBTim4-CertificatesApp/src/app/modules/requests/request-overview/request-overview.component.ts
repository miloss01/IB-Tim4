import { Component, OnInit } from '@angular/core';
import { CertificateRequestDTO } from 'src/app/models/models';
import { CertService } from 'src/app/services/cert-service.service';

@Component({
  selector: 'app-request-overview',
  templateUrl: './request-overview.component.html',
  styleUrls: ['./request-overview.component.css']
})
export class RequestOverviewComponent implements OnInit{

  requests: CertificateRequestDTO[] = []

  constructor( 
    private certService: CertService
  ) {}

  ngOnInit(): void {
  }

  ngAfterViewInit (): void {
    this.certService.getRequestsForOverview().subscribe(res => {
      this.requests = res
    })
  }

}
