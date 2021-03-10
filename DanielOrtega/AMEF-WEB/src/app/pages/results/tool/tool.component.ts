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
  lineMax = null;
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
      console.log(a+"aaaaaaaaaaaaaaaaaaaaaa")
   // setTimeout(() => {
      var l =0
       this.getNumeroLineasFile(this.linkFile).subscribe((res)=>{
          l = res['lineas'];
          console.log(l+"ahora ahroa ")
          this.mensaje = "Finalizó ejecucion.\nEl numero de lineas es: "+l;
       })
     
      console.log(l+"bjhsdsdv")
      
    //}, 6000); 

  }

  public getNumeroLineasFile(valor){
    console.log(valor+" numeroLineas");
     
    return  this.metadataService.getNumeroLineasFile(valor)
  }

  public agregarCola(){
    
   if(!confirm()){
       return; 
   }
   this.mensajeCola = "Agregando a la cola de trabajo";
   this.metadataService.agregarCola({link:this.linkFile,numero:this.lineMax}).subscribe((res) => 
    {
    
    });

  }



  
}
