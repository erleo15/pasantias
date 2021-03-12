import { Component, OnInit, HostBinding } from '@angular/core';
import { MetadataService } from '../../../services';
import { nameMonth }  from '../../../services/global.service';
import { MatDialog, MatDialogModule } from '@angular/material/dialog'; 

@Component({
  selector: 'app-compare',
  templateUrl: './tool.component.html',
  styleUrls: ['./tool.component.scss']
})
export class ToolComponent implements OnInit {
  @HostBinding('class.tool') private readonly tool = true;

  private totalData : any[] = null;
  private percentageData : any[] = null;
  numeroLinea: number = 0;
  numero: number = 0;
  linkFile: string = "";
  lineMax: string = "";
  mensaje = "El numero de lineas es: 0" 
  mensajeCola= "";

  constructor(private metadataService: MetadataService) { }

  ngOnInit() {
    console.log("Estamos emn ngOnInit tool1");
    this.metadataService.getTool().subscribe((res) => {
      console.log("Estamos emn ngOnInit tool");
      
      this.percentageData = res.map(function (date) { return { label: nameMonth(date.date) , value: (100*(date.found/date.searched)), tooltip: date.found + " out of " + date.searched}})
      this.totalData = res.map(function (date) { return { label: nameMonth(date.date) , value: date.found, tooltip: date.found}})
    }); 
  }
  
  async  configurarLink(){
    
    
    this.mensaje = "Ejecutando...\nEl numero de lineas es: 0";
    console.log(this.linkFile+" concatenando");
      var  a = await this.metadataService.configurarLink(this.linkFile).toPromise() 
      var l =0
       this.getNumeroLineasFile(this.linkFile).subscribe((res)=>{
          l = res['lineas'];
          console.log(l+"ahora ahroa ")
          this.mensaje = "Finalizó ejecucion.\nEl numero de lineas es: "+l;
          this.numeroLinea = l
          this.lineMax = "1-"+l
       })
     
      console.log(l+"bjhsdsdv")

  }

  public getNumeroLineasFile(valor){
    console.log(valor+" numeroLineas");
     
    return  this.metadataService.getNumeroLineasFile(valor)
  }

  public agregarCola(){ 
    this.comprobar(this.lineMax)
    if(this.comprobar(this.lineMax)){
        alert("Ingrese el numero de lineas correctamente"); 
        return;
    } 

   this.mensajeCola = "Agregando a la cola de trabajo";
    this.metadataService.agregarCola({link:this.linkFile,numero:this.lineMax}).subscribe((res) => 
    {
      if(res['mensaje'] == "OK"){
        this.mensajeCola = "Se agregó satisfactoriamente"
      }else{
        this.mensajeCola = "Hubo un fallo en el servidor."
      }
      
    });

  }

    private  comprobar(parametro:string){
      var numeros = parametro.split('-') 
      var bandera: Boolean =parseInt(numeros[0])<1 || parseInt(numeros[1])<1 || parseInt(numeros[0])>parseInt(this.lineMax) || parseInt(numeros[1])<1  ;

     return  (numeros.length<1 || numeros.length>2 || (numeros.length>=2 && numeros[1].length==0)|| this.lineMax.length==0 || this.lineMax.indexOf('-')==-1) 
     ||  (parseInt(numeros[0]) > parseInt(numeros[1])) 
     || bandera;
     
    }



  
}
