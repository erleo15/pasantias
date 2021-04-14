import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatRadioModule } from '@angular/material/radio';
import { FormsModule } from '@angular/forms';

import { ThemeModule } from 'theme';

import { PieChartComponent } from './pie-chart';

import { MonthComponent } from './month.component';

@NgModule({
  imports: [
    CommonModule,
    ThemeModule,
    MatRadioModule,
    FormsModule
  ],
  declarations: [
    MonthComponent,
    PieChartComponent,
  ],
})
export class MonthModule {}