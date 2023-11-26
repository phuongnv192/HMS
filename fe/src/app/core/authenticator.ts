import {
    HttpEvent,
    HttpHandler,
    HttpInterceptor,
    HttpRequest,
  } from "@angular/common/http";
  import { Injectable } from "@angular/core";
  import { Observable } from "rxjs";
  import { AuthService } from "../services/auth.service";
  
  @Injectable()
  export class AuthInterceptor implements HttpInterceptor {
    constructor(private authService: AuthService) {}
  
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
      return next.handle(req);
    }
  }