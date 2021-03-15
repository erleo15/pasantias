import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable, Subject } from 'rxjs';

@Injectable()
export class MetadataService { 

  constructor(private http: HttpClient) {
  }

  private url = `${environment.apiBaseUrl}`;


  public configurarLink(link){   
    return this.http.post<any[]>(`${this.url}/configurarLink`, { linkFile: link});
 
  }


  public agregarCola(parametros){    
    return this.http.post<any[]>(`${this.url}/agregarCola`, { linkFile: parametros.link, numero:parametros.numero});

  }

  public getNumeroLineasFile(link){    
    return this.http.post<any[]>(`${this.url}/numeroLineasFile`,{ linkFile: link });
 
  }

  public getEvolution(){ 
      return this.http.get<any[]>(`${this.url}/evolution`);
  }

  public getIniciar(){ 
    return this.http.get<any[]>(`${this.url}/iniciar`);
  }

  public getParar(){ 
    return this.http.get<any[]>(`${this.url}/parar`);
  }

  public getCargar(){ 
    return this.http.get<any[]>(`${this.url}/cargar`);
  }

  public getCargarRespaldo(){ 
    return this.http.get<any[]>(`${this.url}/cargarRespaldo`);
  }

  public getGuardar(lineas){ 
    return this.http.post<any[]>(`${this.url}/guardar`,{ lineas: lineas });
  }

  public getTool(){
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