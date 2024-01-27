import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable()
export class ApiService {
  // private subject = new Subject<any>();

  private baseUrl = environment.apiUrl;
  // private GENERATE_HEAD_PHONE = this.baseUrl + "api/v1/common";
  // private GENERATE_NOTI = this.baseUrl + "api/v1/common/loan-notification";
  // private UPLOAD_FILE = this.baseUrl + "api/v1/files/upload-file";
  // // private IMPORT_PROFILE = this.baseUrl + "api/v1/files/import-file";
  // private SEND_OTP = this.baseUrl + "api/v1/otp/send-otp";
  // private VERIFY_OTP = this.baseUrl + "api/v1/otp/verify-otp";
  private USER_INFOR = this.baseUrl + "user/infor";
  private LOGIN = this.baseUrl + "user/signin";
  private REGISTER = this.baseUrl + "/user/signup";
  // // THIẾU 1 số api liên quan đến quyền / phân quyền và admin do chưa design
  // private CREATE_LOAN = this.baseUrl + "user/create-loan";
  // private CHECK_LOAN = this.baseUrl + "user/is-exist-loan";
  // private UPDATE_INFOR = this.baseUrl + "user/update-base-information";
  // private CHECK_PHOTO = this.baseUrl + "user/update-kyc-image";

  constructor(private http: HttpClient) { }

  private getHeadersWithToken(): HttpHeaders {
    const token = localStorage.getItem('jwt');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
  }
  getUsers(): Observable<any> {
    const headers = this.getHeadersWithToken();
    return this.http.get<any>(this.USER_INFOR, { headers });
  }

  register(account_name: string, first:string, last:string, gender:string, tel: string, password: string): Observable<any> {
    return this.http.post<any>(this.REGISTER, { account_name, first, last, gender, tel, password });
  }


  public get(endpoint: string): Observable<any> {
    const headers = this.getHeadersWithToken();
    return this.http.get(`${this.baseUrl}/${endpoint}`, { headers });
  }

  public post(endpoint: string, body: any): Observable<any> {
    const headers = this.getHeadersWithToken();
    return this.http.post(`${this.baseUrl}/${endpoint}`, body, { headers });
  }


  public put(endpoint: string, body: any): Observable<any> {
    const headers = this.getHeadersWithToken();
    return this.http.put(`${this.baseUrl}/${endpoint}`, body, { headers });
  }

  public delete(endpoint: string): Observable<any> {
    const headers = this.getHeadersWithToken();
    return this.http.delete(`${this.baseUrl}/${endpoint}`, { headers });
  }

  public deleteUser(endpoint: string): Observable<any> {
    const headers = this.getHeadersWithToken();
    return this.http.delete(`${this.baseUrl}/${endpoint}`, { headers });
  }



  // sendData(message: string) {
  //   this.subject.next(message);
  // }

  // clearData() {
  //   this.subject.next();
  // }

  // getData(): Observable<any> {
  //   return this.subject.asObservable();
  // }

}