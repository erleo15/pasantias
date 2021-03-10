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


  public configurarLink(link){ 
    
    //console.log("metodo configurarLink service.ts"+link+" otro otro "+this.url);    
    return this.http.post<any[]>(`${this.url}/configurarLink`, { linkFile: link});
 
  }


  public agregarCola(parametros){ 
    
    console.log("metodo agregarCola service.ts");    
    return this.http.post<any[]>(`${this.url}/agregarCola`, { linkFile: parametros.link, numero:parametros.numero});
 
  }

  public getNumeroLineasFile(link){ 
    console.log("metodo getNumeroLineas service.ts");    
    return this.http.post<any[]>(`${this.url}/numeroLineasFile`,{ linkFile: link });
 
  }

  public getEvolution(){
    //console.log('llega al metodo gettol');
    //return this.http.get<any[]>(`${this.url}/tool`);
      return this.http.get<any[]>(`${this.url}/evolution`);
  }

  public getTool(){
    console.log('llega al metodo gettol');
    return this.http.get<any[]>(`${this.url}/tool`);
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