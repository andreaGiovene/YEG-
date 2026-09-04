import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';
import { finalize } from 'rxjs';
import { CongressoApiService } from '../../core/services/congresso-api.service';

type Dimensione = 'tipologia_stakeholder' | 'regione' | 'canale_ingaggio';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, BaseChartDirective],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent implements OnInit {
  canaleSelezionato = '';
  readonly canaliDisponibili = ['Database DEM', 'LinkedIn', 'On-site (stand)', 'On-site (simposio)'];

  dimensioneSelezionata: Dimensione = 'tipologia_stakeholder';
  readonly dimensioniDisponibili: { valore: Dimensione; etichetta: string }[] = [
    { valore: 'tipologia_stakeholder', etichetta: 'Tipologia stakeholder' },
    { valore: 'regione', etichetta: 'Regione' },
    { valore: 'canale_ingaggio', etichetta: 'Canale di ingaggio' },
  ];

  caricamentoFunnel = false;
  erroreFunnel: string | null = null;
  datiFunnel: ChartConfiguration<'bar'>['data'] | null = null;

  caricamentoConfronto = false;
  erroreConfronto: string | null = null;
  datiConfronto: ChartConfiguration<'bar'>['data'] | null = null;

  caricamentoAndamento = false;
  erroreAndamento: string | null = null;
  datiAndamento: ChartConfiguration<'line'>['data'] | null = null;

  readonly opzioniGrafico: ChartConfiguration['options'] = {
    responsive: true,
    plugins: { legend: { display: true } },
  };

  constructor(private api: CongressoApiService) {}

  ngOnInit(): void {
    this.ricaricaTutto();
  }

  onFiltroCambiato(): void {
    this.ricaricaTutto();
  }

  onDimensioneCambiata(): void {
    this.caricaConfronto();
  }

  private ricaricaTutto(): void {
    this.caricaFunnel();
    this.caricaConfronto();
    this.caricaAndamento();
  }

  private get filtro(): string | undefined {
    return this.canaleSelezionato || undefined;
  }

  private caricaFunnel(): void {
    this.caricamentoFunnel = true;
    this.erroreFunnel = null;
    this.api
      .funnel(this.filtro)
      .pipe(finalize(() => (this.caricamentoFunnel = false)))
      .subscribe({
        next: (righe) => {
          this.datiFunnel = righe.length
            ? {
                labels: righe.map((r) => r['tappa']),
                datasets: [{ label: 'Persone', data: righe.map((r) => r['persone']), backgroundColor: '#4f46e5' }],
              }
            : null;
        },
        error: () => (this.erroreFunnel = 'Impossibile caricare il funnel. Riprova più tardi.'),
      });
  }

  private caricaConfronto(): void {
    this.caricamentoConfronto = true;
    this.erroreConfronto = null;
    this.api
      .confrontoPerDimensione(this.dimensioneSelezionata, this.filtro)
      .pipe(finalize(() => (this.caricamentoConfronto = false)))
      .subscribe({
        next: (righe) => {
          this.datiConfronto = righe.length
            ? {
                labels: righe.map((r) => r['valore']),
                datasets: [
                  { label: 'Totale persone', data: righe.map((r) => r['totale_persone']), backgroundColor: '#9ca3af' },
                  { label: 'Email aperte', data: righe.map((r) => r['email_aperte']), backgroundColor: '#60a5fa' },
                  { label: 'Visite stand', data: righe.map((r) => r['visite_stand']), backgroundColor: '#34d399' },
                  { label: 'Presenze simposio', data: righe.map((r) => r['presenze_simposio']), backgroundColor: '#f59e0b' },
                ],
              }
            : null;
        },
        error: () => (this.erroreConfronto = 'Impossibile caricare il confronto. Riprova più tardi.'),
      });
  }

  private caricaAndamento(): void {
    this.caricamentoAndamento = true;
    this.erroreAndamento = null;
    this.api
      .andamentoGiornaliero(this.filtro)
      .pipe(finalize(() => (this.caricamentoAndamento = false)))
      .subscribe({
        next: (righe) => {
          this.datiAndamento = righe.length
            ? {
                labels: righe.map((r) => r['giorno_visita']),
                datasets: [
                  { label: 'Visite stand', data: righe.map((r) => r['visite_stand']), borderColor: '#4f46e5', backgroundColor: '#4f46e5' },
                  { label: 'Accessi sala VIP', data: righe.map((r) => r['accessi_sala_vip']), borderColor: '#f59e0b', backgroundColor: '#f59e0b' },
                ],
              }
            : null;
        },
        error: () => (this.erroreAndamento = 'Impossibile caricare l\'andamento giornaliero. Riprova più tardi.'),
      });
  }
}
