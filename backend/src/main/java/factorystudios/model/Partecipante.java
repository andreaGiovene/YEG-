package factorystudios.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Anagrafica di un partecipante, con i touchpoint 1:1 (non ripetibili)
 * denormalizzati sulla stessa riga per rendere semplici ed efficienti
 * le query più frequenti della dashboard (funnel, andamento per giorno).
 * La vista "long" degli eventi ripetibili vive in {@link PartecipanteTouchpoint}.
 */
@Entity
@Table(name = "partecipanti")
public class Partecipante {

    @Id
    private Integer id;

    @Column(name = "nome_cognome", nullable = false)
    private String nomeCognome;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "tipologia_stakeholder", nullable = false)
    private String tipologiaStakeholder;

    @Column(name = "regione", nullable = false)
    private String regione;

    @Column(name = "canale_ingaggio", nullable = false)
    private String canaleIngaggio;

    @Column(name = "in_database_dem", nullable = false)
    private Boolean inDatabaseDem;

    @Column(name = "dem_inviata", nullable = false)
    private Boolean demInviata;

    @Column(name = "dem_consegnata", nullable = false)
    private Boolean demConsegnata;

    @Column(name = "dem_aperta", nullable = false)
    private Boolean demAperta;

    @Column(name = "li_annuncio_reach", nullable = false)
    private Boolean liAnnuncioReach;

    @Column(name = "li_annuncio_interazione", nullable = false)
    private Boolean liAnnuncioInterazione;

    @Column(name = "li_recap_reach", nullable = false)
    private Boolean liRecapReach;

    @Column(name = "li_recap_interazione", nullable = false)
    private Boolean liRecapInterazione;

    @Column(name = "visita_stand", nullable = false)
    private Boolean visitaStand;

    @Column(name = "giorno_visita")
    private LocalDate giornoVisita;

    @Column(name = "visualizzazioni", nullable = false)
    private Integer visualizzazioni;

    @Column(name = "scroll_approfondimento", nullable = false)
    private Integer scrollApprofondimento;

    @Column(name = "accesso_sala_vip", nullable = false)
    private Boolean accessoSalaVip;

    @Column(name = "risposte_wordcloud", nullable = false)
    private Integer risposteWordcloud;

    @Column(name = "presenza_simposio", nullable = false)
    private Boolean presenzaSimposio;

    @Column(name = "permanenza_min")
    private BigDecimal permanenzaMin;

    @Column(name = "focus_rate")
    private BigDecimal focusRate;

    @Column(name = "quiz_completati", nullable = false)
    private Integer quizCompletati;

    protected Partecipante() {
        // richiesto da JPA
    }

    public Partecipante(Integer id, String nomeCognome, String email, String tipologiaStakeholder,
                         String regione, String canaleIngaggio, Boolean inDatabaseDem, Boolean demInviata,
                         Boolean demConsegnata, Boolean demAperta, Boolean liAnnuncioReach,
                         Boolean liAnnuncioInterazione, Boolean liRecapReach, Boolean liRecapInterazione,
                         Boolean visitaStand, LocalDate giornoVisita, Integer visualizzazioni,
                         Integer scrollApprofondimento, Boolean accessoSalaVip, Integer risposteWordcloud,
                         Boolean presenzaSimposio, BigDecimal permanenzaMin, BigDecimal focusRate,
                         Integer quizCompletati) {
        this.id = id;
        this.nomeCognome = nomeCognome;
        this.email = email;
        this.tipologiaStakeholder = tipologiaStakeholder;
        this.regione = regione;
        this.canaleIngaggio = canaleIngaggio;
        this.inDatabaseDem = inDatabaseDem;
        this.demInviata = demInviata;
        this.demConsegnata = demConsegnata;
        this.demAperta = demAperta;
        this.liAnnuncioReach = liAnnuncioReach;
        this.liAnnuncioInterazione = liAnnuncioInterazione;
        this.liRecapReach = liRecapReach;
        this.liRecapInterazione = liRecapInterazione;
        this.visitaStand = visitaStand;
        this.giornoVisita = giornoVisita;
        this.visualizzazioni = visualizzazioni;
        this.scrollApprofondimento = scrollApprofondimento;
        this.accessoSalaVip = accessoSalaVip;
        this.risposteWordcloud = risposteWordcloud;
        this.presenzaSimposio = presenzaSimposio;
        this.permanenzaMin = permanenzaMin;
        this.focusRate = focusRate;
        this.quizCompletati = quizCompletati;
    }

    // --- getters (nessun setter: le righe sono immutabili dopo il caricamento ETL) ---

    public Integer getId() { return id; }
    public String getNomeCognome() { return nomeCognome; }
    public String getEmail() { return email; }
    public String getTipologiaStakeholder() { return tipologiaStakeholder; }
    public String getRegione() { return regione; }
    public String getCanaleIngaggio() { return canaleIngaggio; }
    public Boolean getInDatabaseDem() { return inDatabaseDem; }
    public Boolean getDemInviata() { return demInviata; }
    public Boolean getDemConsegnata() { return demConsegnata; }
    public Boolean getDemAperta() { return demAperta; }
    public Boolean getLiAnnuncioReach() { return liAnnuncioReach; }
    public Boolean getLiAnnuncioInterazione() { return liAnnuncioInterazione; }
    public Boolean getLiRecapReach() { return liRecapReach; }
    public Boolean getLiRecapInterazione() { return liRecapInterazione; }
    public Boolean getVisitaStand() { return visitaStand; }
    public LocalDate getGiornoVisita() { return giornoVisita; }
    public Integer getVisualizzazioni() { return visualizzazioni; }
    public Integer getScrollApprofondimento() { return scrollApprofondimento; }
    public Boolean getAccessoSalaVip() { return accessoSalaVip; }
    public Integer getRisposteWordcloud() { return risposteWordcloud; }
    public Boolean getPresenzaSimposio() { return presenzaSimposio; }
    public BigDecimal getPermanenzaMin() { return permanenzaMin; }
    public BigDecimal getFocusRate() { return focusRate; }
    public Integer getQuizCompletati() { return quizCompletati; }
}
