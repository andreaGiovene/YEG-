import { Component } from '@angular/core';
import { DashboardComponent } from './features/dashboard/dashboard.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [DashboardComponent],
  template: `
    <header class="card" style="margin: 16px; text-align: center;">
      <h1 style="margin: 4px 0;">Dashboard Evento Congresso</h1>
      <p style="margin: 0; color: #6b7280;">Prova tecnica — Factory Studios</p>
    </header>
    <app-dashboard></app-dashboard>
  `,
})
export class AppComponent {}
