package factorystudios.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Vista "long" (una riga per touchpoint avvenuto) generata dall'ETL a
 * partire dalla tabella wide {@link Partecipante}, incrociata con
 * {@link TouchpointDefinizione}. È la tabella su cui girano il funnel
 * per fase e gli incroci multi-dimensione richiesti dalla traccia.
 */
@Entity
@Table(name = "partecipante_touchpoint")
public class PartecipanteTouchpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partecipante_id", nullable = false)
    private Partecipante partecipante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nome_tecnico", nullable = false)
    private TouchpointDefinizione touchpoint;

    @Column(name = "avvenuto", nullable = false)
    private Boolean avvenuto;

    @Column(name = "valore_numerico")
    private BigDecimal valoreNumerico;

    @Column(name = "valore_data")
    private LocalDate valoreData;

    protected PartecipanteTouchpoint() {
    }

    public PartecipanteTouchpoint(Partecipante partecipante, TouchpointDefinizione touchpoint,
                                   Boolean avvenuto, BigDecimal valoreNumerico, LocalDate valoreData) {
        this.partecipante = partecipante;
        this.touchpoint = touchpoint;
        this.avvenuto = avvenuto;
        this.valoreNumerico = valoreNumerico;
        this.valoreData = valoreData;
    }

    public Long getId() { return id; }
    public Partecipante getPartecipante() { return partecipante; }
    public TouchpointDefinizione getTouchpoint() { return touchpoint; }
    public Boolean getAvvenuto() { return avvenuto; }
    public BigDecimal getValoreNumerico() { return valoreNumerico; }
    public LocalDate getValoreData() { return valoreData; }
}
