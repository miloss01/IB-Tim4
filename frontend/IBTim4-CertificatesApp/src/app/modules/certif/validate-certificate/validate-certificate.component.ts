import { Component, OnInit } from '@angular/core';
import { CertService } from 'src/app/services/cert-service.service';
import { LoginAuthService } from '../../auth/service/auth.service';

@Component({
  selector: 'app-validate-certificate',
  templateUrl: './validate-certificate.component.html',
  styleUrls: ['./validate-certificate.component.css']
})
export class ValidateCertificateComponent implements OnInit {

  serialNumber: string = ""
  result: string = ""
  fileToUpload: FormData = new FormData()
  toUpload: any

  constructor( 
    private certService: CertService,
    private authService: LoginAuthService
  ) {}

  ngOnInit(): void {
    
  }

  validateBySerialNumber(): void {
    this.certService.validateBySerialNumber(Number(this.serialNumber)).subscribe((res: boolean) => {
      console.log(res)
      if (res)
        this.result = "Certificate IS valid"
      else
        this.result = "Certificate IS NOT valid"
    })
  }

  onFileSelected(event: any) {
    const selectedFile = event.target.files[0];

    let mb5size = 1024 * 1024 * 5
    if (selectedFile.size > mb5size) {
      this.result = "File is bigger than 5MB"
      let el = document.getElementById("certFileInput") as HTMLInputElement
      el.value = ""
      return
    }

    const reader = new FileReader();
    reader.onload = (e: any) => {
      const base64String = btoa(e.target.result);
      this.toUpload = base64String;
    };
    reader.readAsBinaryString(selectedFile);
  }


  validateByFileUpload(): void {
    this.certService.validateByFileUpload(this.toUpload).subscribe((res: boolean) => {
      console.log(res)
      if (res)
        this.result = "Certificate IS valid"
      else
        this.result = "Certificate IS NOT valid"
    })
  }

}
