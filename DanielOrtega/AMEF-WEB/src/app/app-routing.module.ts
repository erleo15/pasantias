import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { LayoutsModule } from './layouts';
import { CommonLayoutComponent } from './layouts/common-layout';
import { DashboardComponent } from './pages/dashboard';
import { CompareComponent } from './pages/results/compare';
import { MonthComponent } from './pages/results/month';
import { ErrorComponent } from './pages/pages/error';

@NgModule({
  imports: [
    RouterModule.forRoot(
      [
        { path: '', redirectTo: 'app', pathMatch: 'full' },
        {
          path: 'app', component: CommonLayoutComponent, children: [
            { path: '', redirectTo: 'results', pathMatch: 'full' },
            { path: 'results', children: [
                { path: '', redirectTo: 'evolution', pathMatch: 'full' },
                { path: 'evolution', component: CompareComponent, pathMatch: 'full' },
                { path: 'month/:month', component: MonthComponent, pathMatch: 'full' },
              ],
            }
          ]
        },
        { path: '**', redirectTo:'404' },
        { path: '404', component: ErrorComponent, pathMatch: 'full' },
      ],
      { useHash: false },
    ),
    LayoutsModule,
  ],
  exports: [RouterModule],
})
export class AppRoutingModule {}
