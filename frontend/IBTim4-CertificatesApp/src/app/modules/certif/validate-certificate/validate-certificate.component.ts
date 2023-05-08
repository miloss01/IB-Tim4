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

  readFile(file: any) {
    return new Promise((resolve, reject) => {
      // Create file reader
      let reader = new FileReader()
  
      // Register event listeners
      reader.addEventListener("loadend", e => resolve(e.target?.result))
      reader.addEventListener("error", reject)
  
      // Read file
      reader.readAsArrayBuffer(file)
    })
  }

  fileChange(target: any) {
    let files: any[] = target.files
    if (files && files.length > 0) {
      let file = files[0];
      this.fileToUpload.append('file', file);
    }  
  }

  onFileSelected(event: any) {
    const selectedFile = event.target.files[0];
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
