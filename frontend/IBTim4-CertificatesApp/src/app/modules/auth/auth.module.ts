import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoginComponent } from './login/login.component';
import { AppRoutingModule } from 'src/app/app-routing.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from 'src/app/infrastructure/material/material.module';
import { RegistrationComponent } from './registration/registration.component';
import { ChangePasswordDialogComponent } from './change-password-dialog/change-password-dialog.component';
import { RefreshPasswordComponent } from './refresh-password/refresh-password.component';
import { OauthComponent } from './oauth/oauth.component';


@NgModule({
  declarations: [
    LoginComponent,
    RegistrationComponent,
    ChangePasswordDialogComponent,
    RefreshPasswordComponent,
    OauthComponent
  ],
  imports: [
    CommonModule,
    AppRoutingModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  exports: [
    LoginComponent,
    RegistrationComponent
  ]
})
export class LoginAuthModule { }
