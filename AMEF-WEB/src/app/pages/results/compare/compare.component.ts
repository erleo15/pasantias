import { Component, OnInit, HostBinding } from '@angular/core';
import { MetadataService } from '../../../services';
import { nameMonth }  from '../../../services/global.service';

@Component({
  selector: 'app-compare',
  templateUrl: './compare.component.html',
  styleUrls: ['./compare.component.scss']
})
export class CompareComponent implements OnInit {
  @HostBinding('class.compare') private readonly compare = true;

  private totalData : any[] = null;
  private percentageData : any[] = null;

  constructor(private metadataService: MetadataService) { }

  ngOnInit() {
    this.metadataService.getEvolution().subscribe((res) => {
      console.log("Estamos emn ngOnInit Evolution");
      
      this.percentageData = res.map(function (date) { return { label: nameMonth(date.date) , value: (100*(date.found/date.searched)), tooltip: date.found + " out of " + date.searched}})
      this.totalData = res.map(function (date) { return { label: nameMonth(date.date) , value: date.found, tooltip: date.found}})
    });
    //console.log(this.percentageData.toString());
  }

}
