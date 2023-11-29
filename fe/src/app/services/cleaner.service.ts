import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";

@Injectable({
  providedIn: "root",
})
export class CleanerService {
  constructor(private http: HttpClient) {}
  private baseUrl = environment.apiUrl;

  getEmployees(params: HttpParams): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/cleaners`, { params });
  }

  getEmployeeById(id: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/user/${id}`);
  }

  getCleanerHistoryDetail(id: string): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/cleaner/history/detail`, { id });
  }

}