import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";

@Injectable({
  providedIn: "root",
})
export class CustomerService {
  constructor(private http: HttpClient) {}
  private baseUrl = environment.apiUrl;

  getCustomers(params: HttpParams): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/users`, { params });
  }
}