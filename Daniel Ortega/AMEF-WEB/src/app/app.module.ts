import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { DashboardModule } from './pages/dashboard';
import { CompareModule } from './pages/results/compare/compare.module';
import { MetadataService } from './services/metadata.service';
import { MonthModule } from './pages/results/month/month.module';
import { ErrorComponent } from './pages/pages/error';

@NgModule({
  declarations: [AppComponent, ErrorComponent],
  imports: [
    BrowserModule,
    AppRoutingModule,
    DashboardModule,
    CompareModule,
    HttpClientModule,
    MonthModule
  ],
  providers: [
    MetadataService
  ],
  bootstrap: [AppComponent],
})
export class AppModule { }
