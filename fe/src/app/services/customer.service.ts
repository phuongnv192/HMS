import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";

@Injectable({
  providedIn: "root",
})
export class CustomerService {
  constructor(private http: HttpClient) {}
  private baseUrl = environment.apiUrl;

  private getHeadersWithToken(): HttpHeaders {
    const token = sessionStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
  }

  getCustomers(params: HttpParams): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/users`, { params });
  }

  getListSchedule(): Observable<any> {
    const headers = this.getHeadersWithToken();
    return this.http.get<any>(`${this.baseUrl}/user/booking`, { headers });
  }

  // booking(body: any): Observable<any> {
  //   const headers = this.getHeadersWithToken();
  //   return this.http.post<any>(`${this.BOOKING}`, body, { headers });
  // }

  // getServiceAddOns(id: string): Observable<any> {
  //   const headers = this.getHeadersWithToken();
  //   return this.http.get<any>(`${this.SERVICEADDON + id}`, { headers });
  // }
}