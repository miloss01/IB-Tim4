import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CertifDTO, CertificateRequestDTO, CertifRequestDTO } from '../models/models';
import { LoginAuthService } from '../modules/auth/service/auth.service';

@Injectable({
  providedIn: 'root'
})
export class CertService {

  constructor(
    private readonly http: HttpClient,
    private authService: LoginAuthService) { }

  getAll() : Observable<CertifDTO[]> {
    return this.http.get<CertifDTO[]>(environment.apiHost + 'certificate/all')
  }

  sendRequest(req: CertifRequestDTO) : Observable<any> {
    return this.http.post<void>(environment.apiHost + 'certificate/request', req)
  }

  getRequestsForOverview() : Observable<CertificateRequestDTO[]> {
    if (this.authService.getRole() === 'ADMIN') return this.http.get<CertificateRequestDTO[]>(environment.apiHost + 'certificate/request')
    return this.http.get<CertificateRequestDTO[]>(environment.apiHost + `certificate/request/${this.authService.getId()}`)
  }

  getRequestsForManaging() : Observable<CertificateRequestDTO[]> {
    if (this.authService.getRole() === 'ADMIN') return this.http.get<CertificateRequestDTO[]>(environment.apiHost + 'certificate/request/manage')
    return this.http.get<CertificateRequestDTO[]>(environment.apiHost + `certificate/request/manage/${this.authService.getId()}`)
  }

  acceptCertificateRequest(reqId: string) : Observable<any> {
    return this.http.post<any>(environment.apiHost + `certificate/request/accept/${reqId}`, {})
  }

  declineCertificateRequest(reqId: string, reason: string) : Observable<any> {
    return this.http.post<any>(environment.apiHost + `certificate/request/deny/${reqId}`, reason)
  }


}
