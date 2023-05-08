import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
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
    return this.http.get<CertificateRequestDTO[]>(environment.apiHost + `certificate/request-${this.authService.getId()}`)
  }

  getRequestsForManaging() : Observable<CertificateRequestDTO[]> {
    if (this.authService.getRole() === 'ADMIN') return this.http.get<CertificateRequestDTO[]>(environment.apiHost + 'certificate/request/manage')
    return this.http.get<CertificateRequestDTO[]>(environment.apiHost + `certificate/request/manage-${this.authService.getId()}`)
  }

  acceptCertificateRequest(reqId: string) : Observable<any> {
    return this.http.post<any>(environment.apiHost + `certificate/request/accept-${reqId}`, {})
  }

  declineCertificateRequest(reqId: string, reason: string) : Observable<any> {
    return this.http.post<any>(environment.apiHost + `certificate/request/deny-${reqId}`, reason)
  }

  downloadCertificate(certId: number): Observable<any> {
    return this.http.get(environment.apiHost + "certificate/download/" + certId, {responseType: 'arraybuffer'})
  }

  retractCertificate(certId: number, reason: string): Observable<any> {
    return this.http.post(environment.apiHost + "certificate/retract/" + certId, reason)
  }

  validateBySerialNumber(certId: number): Observable<any> {
    return this.http.get(environment.apiHost + "certificate/valid?serialNumber=" + certId)
  }

  validateByFileUpload(bytes: string): Observable<any> {
    // const headers = new HttpHeaders().set('Content-Type', 'application/octet-stream');
    // console.log(bytes)
    return this.http.post(environment.apiHost + "certificate/valid/upload", bytes)
  }


}
