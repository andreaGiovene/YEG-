import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Partecipante {
  id: number;
  nomeCognome: string;
  email: string;
  tipologiaStakeholder: string;
  regione: string;
  canaleIngaggio: string;
  demAperta: boolean;
  visitaStand: boolean;
  giornoVisita: string | null;
  accessoSalaVip: boolean;
  presenzaSimposio: boolean;
  permanenzaMin: number | null;
  focusRate: number | null;
  quizCompletati: number;
}

export interface PaginaPartecipanti {
  content: Partecipante[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface FiltriPartecipanti {
  tipologiaStakeholder?: string;
  regione?: string;
  canaleIngaggio?: string;
  visitaStand?: boolean;
  presenzaSimposio?: boolean;
  page?: number;
  size?: number;
}

@Injectable({ providedIn: 'root' })
export class CongressoApiService {
  private readonly baseUrl = '/api';

  constructor(private http: HttpClient) {}

  partecipanti(filtri: FiltriPartecipanti): Observable<PaginaPartecipanti> {
    let params = new HttpParams();
    Object.entries(filtri).forEach(([chiave, valore]) => {
      if (valore !== undefined && valore !== null && valore !== '') {
        params = params.set(chiave, String(valore));
      }
    });
    return this.http.get<PaginaPartecipanti>(`${this.baseUrl}/partecipanti`, { params });
  }

  funnel(canaleIngaggio?: string): Observable<Array<{ tappa: string; persone: number }>> {
    return this.http.get<Array<{ tappa: string; persone: number }>>(`${this.baseUrl}/aggregazioni/funnel`, {
      params: this.parametriOpzionali({ canaleIngaggio }),
    });
  }

  confrontoPerDimensione(dimensione: string, canaleIngaggio?: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/aggregazioni/confronto`, {
      params: this.parametriOpzionali({ dimensione, canaleIngaggio }),
    });
  }

  andamentoGiornaliero(canaleIngaggio?: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/aggregazioni/andamento-giornaliero`, {
      params: this.parametriOpzionali({ canaleIngaggio }),
    });
  }

  conversioneEmailStand(canaleIngaggio?: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/aggregazioni/conversione-email-stand`, {
      params: this.parametriOpzionali({ canaleIngaggio }),
    });
  }

  private parametriOpzionali(valori: Record<string, string | undefined>): HttpParams {
    let params = new HttpParams();
    Object.entries(valori).forEach(([chiave, valore]) => {
      if (valore) params = params.set(chiave, valore);
    });
    return params;
  }
}
