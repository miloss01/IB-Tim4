import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { LoginAuthService } from 'src/app/modules/auth/service/auth.service';
import { Router } from '@angular/router';

@Injectable()
export class TokenExpirationInterceptor implements HttpInterceptor {

  constructor(private readonly authService: LoginAuthService,
    private router: Router) {}
  intercept(
  req: HttpRequest<any>,
  next: HttpHandler
  ): Observable<HttpEvent<any>> {

  return next.handle(req)
            .pipe(
              catchError((error: HttpErrorResponse) => {
                console.log("nananannananana1")
                if (error instanceof HttpErrorResponse && error.status === 401) {
                    console.log("nananannananana")
                    return this.handle401Error(req, next);
                }
                return throwError(() => new Error(error.message));
              })
            )

}
private handle401Error(request: HttpRequest<any>, next: HttpHandler) {

        this.authService.logout()
        this.router.navigate(["/login"])
        alert("We need you to login again");
        return throwError(() => new Error("error.message"));
      
}
}
