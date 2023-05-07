import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class LoginAuthService {

  user$ = new BehaviorSubject({});
  userState$ = this.user$.asObservable();

  constructor(private http: HttpClient) {
    this.user$.next({
      "email": this.getEmail(),
      "role": this.getRole(),
      "id": this.getId()
    });
  }

  login(auth: any): Observable<any> {
    return this.http.post<string>(environment.apiHost + "user/login", auth);
  }

  getRole(): any {
    if (this.isLoggedIn()) {
      console.log("logovan")
      const accessToken: any = localStorage.getItem('user');
      const helper = new JwtHelperService();
      const role = helper.decodeToken(accessToken).role;
      return role;
    }
    return null;
  }

  getEmail(): any {
    if (this.isLoggedIn()) {
      console.log("logovan")
      const accessToken: any = localStorage.getItem('user');
      const helper = new JwtHelperService();
      const email = helper.decodeToken(accessToken).sub;
      return email;
    }
    return null;
  }

  getId(): any {
    if (this.isLoggedIn()) {
      console.log("logovan")
      const accessToken: any = localStorage.getItem('user');
      const helper = new JwtHelperService();
      const id = helper.decodeToken(accessToken).id;
      return id;
    }
    return 1;
  }

  isLoggedIn(): boolean {
    if (localStorage.getItem('user') != null) {
      return true;
    }
    return false;
  }

  setUser(): void {
    this.user$.next({
      "email": this.getEmail(),
      "id": this.getId()
    });
  }

  logout (): void {
    localStorage.removeItem("user")
    this.user$.next({})
  }
}
