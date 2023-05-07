import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './modules/auth/login/login.component';
import { CertifOverviewComponent } from './modules/certif/certif-overview/certif-overview.component';
import { LandingComponent } from './modules/layout/landing/landing.component';
import { CreateRequestComponent } from './modules/requests/create-request/create-request.component';
import { ManagePendingRequestsComponent } from './modules/requests/manage-pending-requests/manage-pending-requests.component';
import { RequestOverviewComponent } from './modules/requests/request-overview/request-overview.component';

const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path: 'certifs-overview', component: CertifOverviewComponent},
  {path: 'create-req', component: CreateRequestComponent},
  {path: 'req-overview', component: RequestOverviewComponent},
  {path: 'manage-req', component: ManagePendingRequestsComponent},
  {path: '', component: LandingComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
