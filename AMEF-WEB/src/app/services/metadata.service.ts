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
    return this.http.post<any[]>(`${this.url}/servicios/configurarLink`, { linkFile: link});
 
  }


  public agregarCola(parametros){    
    return this.http.post<any[]>(`${this.url}/servicios/agregarCola`, { linkFile: parametros.link, numero:parametros.numero});

  }

  public getNumeroLineasFile(link){    
    return this.http.post<any[]>(`${this.url}/servicios/numeroLineasFile`,{ linkFile: link });
 
  }

  public getEvolution(){ 
      return this.http.get<any[]>(`${this.url}/servicios/evolution`);
  }

  public getIniciar(){ 
    return this.http.get<any[]>(`${this.url}/servicios/iniciar`);
  }

  public getParar(){ 
    return this.http.get<any[]>(`${this.url}/servicios/parar`);
  }

  public getCargar(){ 
    return this.http.get<any[]>(`${this.url}/servicios/cargar`);
  }

  public getCargarRespaldo(){ 
    return this.http.get<any[]>(`${this.url}/servicios/cargarRespaldo`);
  }

  public getGuardar(lineas){ 
    return this.http.post<any[]>(`${this.url}/servicios/guardar`,{ lineas: lineas });
  }

  public getTool(){
    return this.http.get<any[]>(`${this.url}/servicios/tool`);
}

  public getAvailableMonths(){
      return this.http.get<any[]>(`${this.url}/servicios/months`);
  }

  public getMonth(month){
     return this.http.get<any[]>(`${this.url}/servicios/month/${month}/props`);
  }

  public getMonthByProps(month){
    return this.http.get<any[]>(`${this.url}/servicios/month/${month}/byprop`);
  }

  public getMonthDomains(month){
    console.log(month)
    return this.http.get<any[]>(`${this.url}/servicios/month/${month}/domains`);
  }



}