import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpResponse,
} from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { AuthService } from "../services/auth.service";
import { tap } from "rxjs/operators";
import { Router } from "@angular/router";
import { ToastrService } from "ngx-toastr";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService
  ) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    const authToken = this.authService.getToken();
    // Clone the request and add the token to the headers
    if (authToken) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${this.authService.getToken()}`,
        },
      });
    }
    return next.handle(req).pipe(
      tap({
        next: (event: HttpEvent<any>) => {
          if (event instanceof HttpResponse) {
            // Check the status code in the response
            if (event.status === 403) {
              // Redirect to the login screen
              sessionStorage.removeItem("token");
              this.router.navigate(["/login"]);
            }
          }
        },
        error: (error) => {
          console.log('authenticator: ', error);
          this.toastr.error('Lỗi hệ thống', 'Có lỗi xảy ra');
          sessionStorage.removeItem("token");
          this.router.navigate(["/login"]);
        },
      })
    );
  }
}