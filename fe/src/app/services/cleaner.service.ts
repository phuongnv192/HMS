import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";

@Injectable({
  providedIn: "root",
})
export class CleanerService {
  constructor(private http: HttpClient) {}
  private baseUrl = environment.apiUrl;

  private getHeadersWithToken(): HttpHeaders {
    const token = sessionStorage.getItem("token");
    return new HttpHeaders({
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    });
  }

  getEmployees(params: HttpParams): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/cleaners`, { params });
  }

  getEmployeeById(id: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/user/${id}`);
  }

  getCleanerHistoryDetail(id: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/cleaner/history/detail/`, {
      params: { userId: id },
    });
  }

  getListSchedule(id: string, params: HttpParams): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/cleaner/schedules?userId=${id}`, { params });
  }

  getListBooking(page: any, size: any, bookingName?: string, bookingPhone?: string, status?:string): Observable<any> {
    const headers = this.getHeadersWithToken();
    let params = { page: page, size: size };
  
    // Thêm các tham số tùy chọn nếu chúng tồn tại
    if (bookingName) {
      params['bookingName'] = bookingName;
    }
  
    if (bookingPhone) {
      params['bookingPhone'] = bookingPhone;
    }

    if(status){
      params['status'] = status;
    }
  
    return this.http.get<any>(`${this.baseUrl}/booking`, { headers, params });
  }


  changeStatus(body: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/cleaner/schedule/status`,  body);
  }

  reject(body: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/cleaner/reject/booking`,  body);
  }
}