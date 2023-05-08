import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CertifOverviewComponent } from './certif-overview/certif-overview.component';
import { MaterialModule } from 'src/app/infrastructure/material/material.module';
import { HttpClient } from '@angular/common/http';
import { CertService } from 'src/app/services/cert-service.service';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ValidateCertificateComponent } from './validate-certificate/validate-certificate.component';



@NgModule({
  declarations: [
    CertifOverviewComponent,
    ValidateCertificateComponent
  ],
  imports: [
    CommonModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    CertService
  ],
  exports: [
    CertifOverviewComponent
  ]
})
export class CertifModule { }
