import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CertifOverviewComponent } from './certif-overview/certif-overview.component';
import { MaterialModule } from 'src/app/infrastructure/material/material.module';
import { HttpClient } from '@angular/common/http';
import { CertService } from 'src/app/services/cert-service.service';



@NgModule({
  declarations: [
    CertifOverviewComponent
  ],
  imports: [
    CommonModule,
    MaterialModule
  ],
  providers: [
    CertService
  ],
  exports: [
    CertifOverviewComponent
  ]
})
export class CertifModule { }
