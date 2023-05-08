import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations'
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CertifModule } from './modules/certif/certif.module';
import { MaterialModule } from './infrastructure/material/material.module';
import { LoginAuthModule } from './modules/auth/auth.module';
import { LayoutModule } from './modules/layout/layout.module';
import { RequestsModule } from './modules/requests/requests.module';
import { Interceptor } from './infrastructure/interceptor/interceptor.interceptor';

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
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: Interceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
