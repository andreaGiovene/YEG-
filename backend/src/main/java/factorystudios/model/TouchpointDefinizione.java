package factorystudios.model;

import jakarta.persistence.*;

/**
 * Dizionario dei touchpoint, caricato dal foglio 01_Interazioni.
 * Classifica ogni touchpoint per fase del percorso: è la tabella
 * che rende possibile aggregare "per fase" senza hardcodare
 * l'elenco delle colonne nel codice applicativo.
 */
@Entity
@Table(name = "touchpoint_definizioni")
public class TouchpointDefinizione {

    @Id
    @Column(name = "nome_tecnico")
    private String nomeTecnico;

    @Column(name = "intestazione", nullable = false)
    private String intestazione;

    @Column(name = "tipo_dato", nullable = false)
    private String tipoDato;

    @Column(name = "fase_percorso", nullable = false)
    private String fasePercorso;

    @Column(name = "descrizione")
    private String descrizione;

    protected TouchpointDefinizione() {
    }

    public TouchpointDefinizione(String nomeTecnico, String intestazione, String tipoDato,
                                  String fasePercorso, String descrizione) {
        this.nomeTecnico = nomeTecnico;
        this.intestazione = intestazione;
        this.tipoDato = tipoDato;
        this.fasePercorso = fasePercorso;
        this.descrizione = descrizione;
    }

    public String getNomeTecnico() { return nomeTecnico; }
    public String getIntestazione() { return intestazione; }
    public String getTipoDato() { return tipoDato; }
    public String getFasePercorso() { return fasePercorso; }
    public String getDescrizione() { return descrizione; }
}
