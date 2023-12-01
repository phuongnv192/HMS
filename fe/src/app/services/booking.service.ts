import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";

@Injectable({
  providedIn: "root",
})
export class BookingService {
  constructor(private http: HttpClient) { }
  private baseUrl = environment.apiUrl;

  // getEmployees(params: HttpParams): Observable<any> {
  //   return this.http.get<any>(`${this.baseUrl}/cleaners`, { params });
  // }

  // getEmployeeById(id: string): Observable<any> {
  //   return this.http.get<any>(`${this.baseUrl}/user/${id}`);
  // }

  // getCleanerHistoryDetail(id: string): Observable<any> {
  //   return this.http.post<any>(`${this.baseUrl}/cleaner/history/detail`, { id });
  // }

  getBookingDetail(id: any): Observable<any> {
    if (id == 1) {
      return this.http.get<any>('assets/data/booking-detail.json');
    } else if (id == 2) {
      return this.http.get<any>('assets/data/booking-detail2.json');
    } else if (id == 3) {
      return this.http.get<any>('assets/data/booking-detail3.json');
    } else if (id == 4) {
      return this.http.get<any>('assets/data/booking-detail4.json');
    } else if (id == 5) {
      return this.http.get<any>('assets/data/booking-detail5.json');
    } else if (id == 6) {
      return this.http.get<any>('assets/data/booking-detail6.json');
    } else if (id == 7) {
      return this.http.get<any>('assets/data/booking-detail7.json');
    } else if (id == 8) {
      return this.http.get<any>('assets/data/booking-detail8.json');
    }
  }

  getListBooking(): Observable<any> {
    return this.http.get<any>('assets/data/list-booking.json');
  }

}