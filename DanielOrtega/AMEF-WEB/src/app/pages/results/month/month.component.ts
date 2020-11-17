import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from "@angular/router";

import { MetadataService } from '../../../services';
import { nameMonth }  from '../../../services/global.service';
import { elementStyleProp } from '@angular/core/src/render3';

@Component({
  selector: 'app-month',
  templateUrl: './month.component.html',
  styleUrls: ['./month.component.scss'],
  providers: [MetadataService],
})
export class MonthComponent implements OnInit {

  private byPropForm: string = "chart";

  private month: any;
  private monthName : string;

  private props : any = null;
  private byProp : any[] = [];
  private domains : any[] = [];


  constructor(private route: ActivatedRoute, private metadataService: MetadataService, private router :Router) {
    this.route.params.subscribe( params => {this.month=params['month']; this.monthName=nameMonth(this.month)});
  }

  ngOnInit() {
    this.metadataService.getMonth(this.month).subscribe((res) => {
      if(res.length == 0) this.router.navigateByUrl('/404');
      this.props=res;
    });
    this.metadataService.getMonthByProps(this.month).subscribe((res) => {
      if(res.length == 0) this.router.navigateByUrl('/404');
      this.byProp= res;
    });
    this.metadataService.getMonthDomains(this.month).subscribe((res) => {
      if(res.length == 0) this.router.navigateByUrl('/404');
      this.domains= res;
    });
  }

  sortDomainsName(){
    console.log("LOOJKNHKLJNLOKN");
    this.domains = this.domains.sort(function (a, b) {
      if (a.domain > b.domain) {
          return -1;
      }
      else {
          return 1;
      }
    });
  }

  sortDomainsTotal(){
    this.domains = this.domains.sort(function (a, b) {
      if (a.total > b.total) {
          return -1;
      }
      else {
          return 1;
      }
    });
  }

}
