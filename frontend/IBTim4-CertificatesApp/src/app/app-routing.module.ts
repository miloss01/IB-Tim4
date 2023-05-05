import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './modules/auth/login/login.component';
import { CertifOverviewComponent } from './modules/certif/certif-overview/certif-overview.component';
import { CreateRequestComponent } from './modules/requests/create-request/create-request.component';

const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path: 'certifs-overview', component: CertifOverviewComponent},
  {path: 'create-cert-req', component: CreateRequestComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
