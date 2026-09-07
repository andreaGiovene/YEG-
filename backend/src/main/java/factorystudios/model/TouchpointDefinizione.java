package factorystudios.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Definizione anagrafica di un touchpoint del percorso del partecipante.
 *
 * <p>Questa classe rappresenta il <em>dizionario</em> dei touchpoint, non
 * l'evento di una singola persona. Per esempio, una riga può definire il
 * touchpoint {@code dem_aperta}; il fatto che il partecipante con un certo
 * identificativo abbia davvero aperto la DEM viene invece registrato in
 * {@link PartecipanteTouchpoint}.</p>
 *
 * <p>La separazione tra definizione e fatto evita di ripetere descrizione,
 * tipo e fase del percorso su ogni evento. Inoltre consente di aggiungere o
 * classificare touchpoint tramite dati del foglio {@code 01_Interazioni},
 * senza hardcodare nel codice l'elenco delle fasi. Le query possono così
 * aggregare gli eventi per {@code fasePercorso} usando il legame con questa
 * tabella.</p>
 *
 * <p>L'entity è popolata dall'ETL e persistita nella tabella
 * {@code touchpoint_definizioni}. La colonna {@code nomeTecnico} è la chiave
 * stabile condivisa con {@code partecipante_touchpoint.nome_tecnico}.</p>
 */
@Entity
@Table(name = "touchpoint_definizioni")
public class TouchpointDefinizione {

    /**
     * Nome tecnico stabile usato dal codice e dalle foreign key.
     * È preferito all'intestazione visualizzata perché quest'ultima può
     * cambiare o contenere differenze di formattazione nel file Excel.
     */
    @Id
    @Column(name = "nome_tecnico")
    private String nomeTecnico;

    /**
     * Intestazione leggibile proveniente dal foglio Excel, conservata per
     * documentare la corrispondenza con la colonna originale.
     */
    @Column(name = "intestazione", nullable = false)
    private String intestazione;

    /**
     * Tipo logico del dato, ad esempio booleano, conteggio, data o tasso.
     * Serve a descrivere come interpretare il valore del touchpoint.
     */
    @Column(name = "tipo_dato", nullable = false)
    private String tipoDato;

    /**
     * Fase del percorso a cui appartiene il touchpoint, usata per filtri e
     * aggregazioni di analisi come il funnel per fase.
     */
    @Column(name = "fase_percorso", nullable = false)
    private String fasePercorso;

    /**
     * Descrizione opzionale del significato funzionale del touchpoint.
     */
    @Column(name = "descrizione")
    private String descrizione;

    /**
     * Costruttore richiesto da JPA per creare l'entity durante la lettura
     * dal database. È protetto per evitare l'uso diretto nell'applicazione;
     * il costruttore pubblico parametrizzato è quello destinato alla creazione
     * esplicita durante l'ETL o nei test.
     */
    protected TouchpointDefinizione() {
    }

    /**
     * Crea una definizione completa di touchpoint.
     *
     * @param nomeTecnico chiave stabile usata nei riferimenti ai fatti
     * @param intestazione intestazione originale del foglio Excel
     * @param tipoDato tipo logico del touchpoint
     * @param fasePercorso fase del percorso a cui appartiene
     * @param descrizione spiegazione opzionale del touchpoint
     */
    public TouchpointDefinizione(String nomeTecnico, String intestazione, String tipoDato,
                                  String fasePercorso, String descrizione) {
        this.nomeTecnico = nomeTecnico;
        this.intestazione = intestazione;
        this.tipoDato = tipoDato;
        this.fasePercorso = fasePercorso;
        this.descrizione = descrizione;
    }

    /** @return chiave tecnica usata per identificare il touchpoint */
    public String getNomeTecnico() { return nomeTecnico; }

    /** @return intestazione originale e leggibile del touchpoint */
    public String getIntestazione() { return intestazione; }

    /** @return tipo logico del dato memorizzato */
    public String getTipoDato() { return tipoDato; }

    /** @return fase del percorso associata al touchpoint */
    public String getFasePercorso() { return fasePercorso; }

    /** @return descrizione funzionale, se presente */
    public String getDescrizione() { return descrizione; }
}
