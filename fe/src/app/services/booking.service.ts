import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";

@Injectable({
  providedIn: "root",
})
export class BookingService {
  constructor(private http: HttpClient) { }
  private baseUrl = environment.apiUrl;
  private SERVICETYPE = this.baseUrl + "/service-types";
  private CLEANERAVAIBLAE = this.baseUrl + "/cleaner/available";
  private CLEANERPICKDETAIL = this.baseUrl + "/cleaner/history/detail?cleanerId=";
  private SERVICEADDON = this.baseUrl + "/service-add-ons?addOnId=";
  // getEmployees(params: HttpParams): Observable<any> {
  //   return this.http.get<any>(`${this.baseUrl}/cleaners`, { params });
  // }

  private getHeadersWithToken(): HttpHeaders {
    const token = sessionStorage.getItem('token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
  }

  getServiceType(): Observable<any> {
    const headers = this.getHeadersWithToken();
    return this.http.get<any>(`${this.SERVICETYPE}`, { headers });
  }

  getServiceAddOns(id: string): Observable<any> {
    const headers = this.getHeadersWithToken();
    return this.http.get<any>(`${this.SERVICEADDON + id}`, { headers });
  }

  getCleanerAvaiable(date: any, typeId: any, packageId: any): Observable<any> {
    const headers = this.getHeadersWithToken();
    return this.http.get<any>(`${this.CLEANERAVAIBLAE + '?workDate=' + date + '&serviceTypeId=' + typeId + '&servicePackageId=' + packageId}`, { headers });
  }

  getCleanerHistory(id: any): Observable<any> {
    const headers = this.getHeadersWithToken();
    return this.http.get<any>(`${this.CLEANERPICKDETAIL + id}`, { headers });
  }


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