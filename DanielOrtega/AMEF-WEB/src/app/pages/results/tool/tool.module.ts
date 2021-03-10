import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { ThemeModule } from 'theme';

import { ToolComponent } from './tool.component';
import { FormsModule } from '@angular/forms';

import { DiscreteBarChartComponent } from './discrete-bar-chart';

@NgModule({
  imports: [
    CommonModule,
    ThemeModule,
    FormsModule
  ],
  declarations: [
    ToolComponent,
    DiscreteBarChartComponent,

  ],
})
export class ToolModule {}