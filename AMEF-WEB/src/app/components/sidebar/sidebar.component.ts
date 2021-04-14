import { Component, Input } from '@angular/core';
import { MetadataService } from '../../services';
import { nameMonth }  from '../../services/global.service';

import { SidebarComponent as BaseSidebarComponent } from 'theme/components/sidebar';

@Component({
  selector: 'app-sidebar',
  styleUrls: ['../../../theme/components/sidebar/sidebar.component.scss'],
  templateUrl: '../../../theme/components/sidebar/sidebar.component.html',
})
export class SidebarComponent extends BaseSidebarComponent {

  private availableMonthsMenu = [{name: 'Loading ...', link: "a"}];
  public menu;
  constructor(private metaService: MetadataService) {
    super();
  }

  public ngOnInit() {
    this.menu = this.menuTemplate();
    this.metaService.getAvailableMonths()
      .subscribe((result) => {
        this.availableMonthsMenu = result.map((month) => {
          return {name: nameMonth(month), link: '/app/results/month/'+month}
        })
        this.menu = this.menuTemplate();
      })
  }

  public title = 'amef';
  private menuTemplate() {return [
    { name: 'Compilar', link: '/app/results/tool', icon: 'settings' },
    { name: 'Evolution', link: '/app/results/evolution', icon: 'trending_up' },
    { name: 'Monthly results', children: this.availableMonthsMenu, icon: 'pie_chart'},
    { name: 'Realizado por Hernan Leon Proyecto_Edutech', link: '/'},
  ];
  }
}
