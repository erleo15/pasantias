import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable, Subject } from 'rxjs';

@Injectable()
export class MetadataService {
  //public filterValue$ = new Subject();

  constructor(private http: HttpClient) {
  }

  private url = `${environment.apiBaseUrl}`;

  public getEvolution(){
      return this.http.get<any[]>(`${this.url}/evolution`);
  }

  public getAvailableMonths(){
      return this.http.get<any[]>(`${this.url}/months`);
  }

  public getMonth(month){
     return this.http.get<any[]>(`${this.url}/month/${month}/props`);
  }

  public getMonthByProps(month){
    return this.http.get<any[]>(`${this.url}/month/${month}/byprop`);
  }

  public getMonthDomains(month){
    return this.http.get<any[]>(`${this.url}/month/${month}/domains`);
  }

}