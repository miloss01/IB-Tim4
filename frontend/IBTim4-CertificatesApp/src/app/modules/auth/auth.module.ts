import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoginComponent } from './login/login.component';
import { AppRoutingModule } from 'src/app/app-routing.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from 'src/app/infrastructure/material/material.module';
import { RegistrationComponent } from './registration/registration.component';
import { ChangePasswordDialogComponent } from './change-password-dialog/change-password-dialog.component';
import { RecaptchaFormsModule, RecaptchaModule } from 'ng-recaptcha';


@NgModule({
  declarations: [
    LoginComponent,
    RegistrationComponent,
    ChangePasswordDialogComponent
  ],
  imports: [
    CommonModule,
    AppRoutingModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
    RecaptchaModule,
    RecaptchaFormsModule,
  ],
  exports: [
    LoginComponent,
    RegistrationComponent
  ]
})
export class LoginAuthModule { }
