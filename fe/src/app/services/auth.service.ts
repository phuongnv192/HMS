import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { tap } from "rxjs/operators";
import { environment } from "src/environments/environment";

@Injectable()
export class AuthService {
  private baseUrl = environment.apiUrl + "/auth";
  private jwtToken: string;
  private REGISTER = this.baseUrl + "/register";
  private CHANGE_PASS = this.baseUrl + "/change-password";
  private LOGOUT = this.baseUrl + "/logout";

  constructor(private http: HttpClient) { }

  signin(username: string, password: string): Observable<any> {
    return this.http
      .post(`${this.baseUrl}/authenticate`, { username, password })
      .pipe(
        tap((res) => {
          if (res.token) {
            // Lưu jwt token vào sessionStorage
            sessionStorage.setItem("jwtToken", res.token);
            this.jwtToken = res.token;
          }
        })
      );
  }

  changePass(username: string, password: string): Observable<any> {
    return this.http.post<any>(this.CHANGE_PASS, { username, password });
  }

  register(req: any): Observable<any> {
    return this.http.post<any>(this.REGISTER, req);
  }

  signout() {
    // Xóa jwt token khỏi sessionStorage
    var a = this.http.post<any>(this.LOGOUT, {});
    sessionStorage.removeItem("jwtToken");
    this.jwtToken = null;
    return a;
  }

  isAuthenticated(): boolean {
    // Kiểm tra xem jwt token đã tồn tại trong sessionStorage hay chưa
    return !!sessionStorage.getItem("jwtToken");
  }

  getJwtToken(): string {
    return this.jwtToken || sessionStorage.getItem("jwtToken");
  }

  getHeaders(): HttpHeaders {
    // Thêm jwt token vào header trước khi gửi request
    return new HttpHeaders({ Authorization: `Bearer ${this.getJwtToken()}` });
  }
}