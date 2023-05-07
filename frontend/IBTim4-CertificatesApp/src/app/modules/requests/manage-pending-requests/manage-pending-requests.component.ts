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
  selectedDecline: boolean = false
  selectedReqId: string = ''

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

  onConfirmClick(req: CertificateRequestDTO) {
    if (this.selectedChoiceToggleVal === 'ACCEPT') this.accept(req)
    if (this.selectedChoiceToggleVal === 'DECLINE') this.decline(req)
  }

  private accept(req: CertificateRequestDTO) {
    this.certService.acceptCertificateRequest(this.selectedReqId).subscribe(res => {
      this.removeReqFromList(req)
      alert('Successfully accepted.')
    }, (err: any) => {
      console.log(err)
      alert(err.error.message)
    })
  }

  private decline(req: CertificateRequestDTO) {
    this.certService.declineCertificateRequest(this.selectedReqId, this.declineReason).subscribe(res => {
      this.declineReason = ''
      this.removeReqFromList(req)
      alert('Successfully declined.')
    }, (err: any) => {
      console.log(err)
      alert(err.error.message)
    })
  }

  private removeReqFromList(req: CertificateRequestDTO) {
    const index = this.requests.indexOf(req, 0);
    if (index > -1) {
      this.requests.splice(index, 1);
}
  }

  public onChoiceToggleValChange(val: string, id: string) {
    this.selectedReqId = id;
    this.selectedChoiceToggleVal = val;
    this.panelOpenCondition = true
    if (this.selectedChoiceToggleVal === 'DECLINE') this.selectedDecline = true
    else this.selectedDecline = false
  }

}
