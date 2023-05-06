import { Component } from '@angular/core';
import { CertificateRequestDTO } from 'src/app/models/models';
import { CertService } from 'src/app/services/cert-service.service';

@Component({
  selector: 'app-manage-pending-requests',
  templateUrl: './manage-pending-requests.component.html',
  styleUrls: ['./manage-pending-requests.component.css']
})
export class ManagePendingRequestsComponent {
  
  requests: CertificateRequestDTO[] = []

  declineReason: string = ''

  panelOpenCondition: boolean = false
  selectedChoiceToggleVal: string = ''

  constructor( 
    private certService: CertService
  ) {}

  ngOnInit(): void {
  }

  ngAfterViewInit (): void {
    this.certService.getRequestsForManaging().subscribe(res => {
      this.requests = res
    })
  }

  onAcceptClick(reqId: string) {
    this.certService.acceptCertificateRequest(reqId).subscribe(res => {

    }, (err: any) => {
      console.log(err)
      alert(err.error.message)
    })
  }

  onDeclineClick(reqId: string) {
    this.certService.declineCertificateRequest(reqId, this.declineReason).subscribe(res => {

    }, (err: any) => {
      console.log(err)
      alert(err.error.message)
    })
  }

  public onChoiceToggleValChange(val: string) {
    this.selectedChoiceToggleVal = val;
    if (this.selectedChoiceToggleVal == 'DECLINE') this.panelOpenCondition = true
    else this.panelOpenCondition = false
  }

}
