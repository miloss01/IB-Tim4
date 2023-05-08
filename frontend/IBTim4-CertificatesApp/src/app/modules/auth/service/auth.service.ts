import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { JwtHelperService } from '@auth0/angular-jwt';
import { PasswordChangeDTO, TwilioDTO, UserExpandedDTO } from 'src/app/models/models';

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
      "id": this.getId(),
      "phone": this.getPhone()
    });
  }
  

  login(auth: any): Observable<any> {
    return this.http.post<string>(environment.apiHost + "user/login", auth);
  }

  register(person: UserExpandedDTO): Observable<any> {
    return this.http.post<string>(environment.apiHost + "user", person);
  }

  sendPhoneCode(phone:string): Observable<any> {
    //http://localhost:8081/api/user/generateOTP/+381603531317
    return this.http.get<string>(environment.apiHost + "user/generateOTP/" + phone);
  }

  sendEmailCode(email:string): Observable<any> {
    //http://localhost:8081/api/user/generateOTP/+381603531317
    return this.http.get<string>(environment.apiHost + "user/generateEmailOTP/" + email);
  }

  verifyCode(twilo: TwilioDTO): Observable<any> {
    const options: any = {
      responseType: 'text',
    };
    return this.http.post<string>(environment.apiHost + "user/verifyOTP/", twilo, options );
  }
  getPhone() {
    if (this.isLoggedIn()) {
      console.log("logovan")
      const accessToken: any = localStorage.getItem('user');
      const helper = new JwtHelperService();
      const role = helper.decodeToken(accessToken).phone;
      return role;
    }
    return null;
  }
  getRole(): any {
    if (this.isLoggedIn()) {
      console.log("logovan")
      const accessToken: any = localStorage.getItem('user');
      const helper = new JwtHelperService();
      const role = helper.decodeToken(accessToken).role;
      console.log(role)
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
    return null;
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
      "id": this.getId(),
      "phone": this.getPhone()
    });
  }

  logout (): void {
    localStorage.removeItem("user")
    this.user$.next({})
  }

  changePassword(twilo: PasswordChangeDTO): Observable<any> {
    const options: any = {
      responseType: 'text',
    };
    return this.http.post<string>(environment.apiHost + "user/changePassword", twilo, options );
  }



}
