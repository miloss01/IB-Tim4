import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CertifDTO, CertifRequestDTO } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class CertService {

  constructor(private readonly http: HttpClient) { }

  getAll() : Observable<CertifDTO[]> {
    return this.http.get<CertifDTO[]>(environment.apiHost + 'certificate/all')
  }

  sendRequest(req: CertifRequestDTO) : Observable<any> {
    return this.http.post<void>(environment.apiHost + 'certificate/request', req)
  }

}
