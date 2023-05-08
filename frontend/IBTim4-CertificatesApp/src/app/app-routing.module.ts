import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './modules/auth/login/login.component';
import { CertifOverviewComponent } from './modules/certif/certif-overview/certif-overview.component';
import { LandingComponent } from './modules/layout/landing/landing.component';
import { CreateRequestComponent } from './modules/requests/create-request/create-request.component';
import { ManagePendingRequestsComponent } from './modules/requests/manage-pending-requests/manage-pending-requests.component';
import { RequestOverviewComponent } from './modules/requests/request-overview/request-overview.component';
import { RegistrationComponent } from './modules/auth/registration/registration.component';
import { UnregisteredGuard } from './infrastructure/guard/unregistered.guard';
import { TokenGuard } from './infrastructure/guard/token.guard';
import { ValidateCertificateComponent } from './modules/certif/validate-certificate/validate-certificate.component';

const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path: 'registration', component: RegistrationComponent},
  {path: 'certifs-overview', component: CertifOverviewComponent, canActivate: [TokenGuard]},
  {path: 'create-req', component: CreateRequestComponent, canActivate: [TokenGuard]},
  {path: 'req-overview', component: RequestOverviewComponent, canActivate: [TokenGuard]},
  {path: 'manage-req', component: ManagePendingRequestsComponent, canActivate: [TokenGuard]},
  {path: 'validate', component: ValidateCertificateComponent, canActivate: [TokenGuard]},
  {path: '', component: LandingComponent},

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
