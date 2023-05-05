import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations'
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MaterialModule } from './infrastructure/material/material.module';
import { CertifModule } from './modules/certif/certif.module';
import { LayoutModule } from './modules/layout/layout.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LoginAuthModule } from './modules/auth/auth.module';
import { RequestsModule } from './modules/requests/requests.module';

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    NoopAnimationsModule,
    MaterialModule,
    CertifModule,
    HttpClientModule,
    LayoutModule,
    LoginAuthModule,
    RequestsModule
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
