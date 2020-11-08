import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { ThemeModule } from 'theme';

import { CompareComponent } from './compare.component';

import { DiscreteBarChartComponent } from './discrete-bar-chart';

@NgModule({
  imports: [
    CommonModule,
    ThemeModule,
  ],
  declarations: [
    CompareComponent,
    DiscreteBarChartComponent,
  ],
})
export class CompareModule {}