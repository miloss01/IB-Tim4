import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CreateRequestComponent } from './create-request/create-request.component';
import { MaterialModule } from 'src/app/infrastructure/material/material.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RequestOverviewComponent } from './request-overview/request-overview.component';
import { ManagePendingRequestsComponent } from './manage-pending-requests/manage-pending-requests.component';
import { RecaptchaModule, RecaptchaFormsModule } from 'ng-recaptcha';




@NgModule({
  declarations: [
    CreateRequestComponent,
    RequestOverviewComponent,
    ManagePendingRequestsComponent
  ],
  imports: [
    CommonModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
    RecaptchaModule,
    RecaptchaFormsModule,
  ]
})
export class RequestsModule { }
