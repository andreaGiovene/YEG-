package factorystudios.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Query di aggregazione eseguite nel database (non nel browser, come
 * richiesto dal punto 4.2). Usa JdbcTemplate invece di JPQL perché la
 * "dimensione" di confronto (tipologia/regione/canale) è scelta a
 * runtime dal chiamante: JPQL non permette di parametrizzare il nome
 * di una colonna in GROUP BY, un allowlist + SQL nativo sì.
 *
 * Tutti i metodi accettano un {@code canaleIngaggio} opzionale (null =
 * nessun filtro): è il filtro che agisce su tutta la pagina richiesto
 * dal punto 4.3, applicato in modo coerente a funnel, confronto e
 * andamento giornaliero.
 */
@Repository
public class AggregazioniRepository {

    // Allowlist delle colonne su cui è lecito raggruppare: evita SQL injection
    // dato che il nome colonna non può essere passato come bind parameter.
    private static final Map<String, String> DIMENSIONI_CONSENTITE = Map.of(
            "tipologia_stakeholder", "tipologia_stakeholder",
            "regione", "regione",
            "canale_ingaggio", "canale_ingaggio"
    );

    private final JdbcTemplate jdbcTemplate;

    public AggregazioniRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public static Set<String> dimensioniConsentite() {
        return DIMENSIONI_CONSENTITE.keySet();
    }

    /**
     * Funnel del percorso: quante persone hanno raggiunto ogni tappa,
     * dalle raggiunte dalla comunicazione fino alle azioni più impegnative.
     * L'ordine delle tappe è quello del percorso, non alfabetico.
     */
    public List<Map<String, Object>> funnel(String canaleIngaggio) {
        String clausolaFiltro = canaleIngaggio != null ? "WHERE canale_ingaggio = ?" : "";

        String sql = """
                SELECT 'Raggiunti (DEM o LinkedIn)' AS tappa,
                       COUNT(*) FILTER (WHERE dem_consegnata OR li_annuncio_reach) AS persone, 1 AS ordine
                FROM partecipanti %1$s
                UNION ALL
                SELECT 'Email aperta', COUNT(*) FILTER (WHERE dem_aperta), 2 FROM partecipanti %1$s
                UNION ALL
                SELECT 'Visita stand', COUNT(*) FILTER (WHERE visita_stand), 3 FROM partecipanti %1$s
                UNION ALL
                SELECT 'Accesso sala VIP', COUNT(*) FILTER (WHERE accesso_sala_vip), 4 FROM partecipanti %1$s
                UNION ALL
                SELECT 'Presenza simposio', COUNT(*) FILTER (WHERE presenza_simposio), 5 FROM partecipanti %1$s
                ORDER BY ordine
                """.formatted(clausolaFiltro);

        // la clausola compare 5 volte (una per ogni ramo dell'UNION ALL): il bind
        // parameter va ripetuto altrettante volte, nello stesso ordine.
        List<Object> parametriRipetuti = new ArrayList<>();
        for (int i = 0; i < 5; i++) parametriRipetuti.addAll(parametriFiltro(canaleIngaggio));

        return jdbcTemplate.queryForList(sql, parametriRipetuti.toArray());
    }

    /**
     * Confronto per dimensione anagrafica: per ogni valore della dimensione
     * scelta, quante persone raggiunte, quante allo stand, quante al simposio.
     * Incrocia due dimensioni diverse (anagrafica x comportamento), non un
     * semplice conteggio di una sola colonna.
     */
    public List<Map<String, Object>> confrontoPerDimensione(String dimensione, String canaleIngaggio) {
        String colonna = colonnaSicura(dimensione);
        String clausolaFiltro = canaleIngaggio != null ? "WHERE canale_ingaggio = ?" : "";
        String sql = """
                SELECT %s AS valore,
                       COUNT(*) AS totale_persone,
                       COUNT(*) FILTER (WHERE dem_aperta) AS email_aperte,
                       COUNT(*) FILTER (WHERE visita_stand) AS visite_stand,
                       COUNT(*) FILTER (WHERE presenza_simposio) AS presenze_simposio
                FROM partecipanti %s
                GROUP BY %s
                ORDER BY totale_persone DESC
                """.formatted(colonna, clausolaFiltro, colonna);
        return jdbcTemplate.queryForList(sql, parametriFiltro(canaleIngaggio).toArray());
    }

    /**
     * Andamento per giornata del congresso: visite allo stand e accessi sala
     * VIP raggruppati per giorno, per persone che hanno effettivamente
     * partecipato on-site (chi non è mai stato in loco non compare).
     */
    public List<Map<String, Object>> andamentoPerGiorno(String canaleIngaggio) {
        String clausolaFiltro = canaleIngaggio != null ? "AND canale_ingaggio = ?" : "";
        String sql = """
                SELECT giorno_visita,
                       COUNT(*) AS visite_stand,
                       COUNT(*) FILTER (WHERE accesso_sala_vip) AS accessi_sala_vip
                FROM partecipanti
                WHERE giorno_visita IS NOT NULL %s
                GROUP BY giorno_visita
                ORDER BY giorno_visita
                """.formatted(clausolaFiltro);
        return jdbcTemplate.queryForList(sql, parametriFiltro(canaleIngaggio).toArray());
    }

    /**
     * Incrocio richiesto esplicitamente dalla traccia: chi apre l'email
     * (raggiunto dalla comunicazione pre-evento) si presenta poi davvero
     * allo stand? Confronta il tasso di conversione a stand fra chi ha
     * aperto la DEM e chi no.
     */
    public List<Map<String, Object>> conversioneEmailVsStand(String canaleIngaggio) {
        String clausolaFiltro = canaleIngaggio != null ? "WHERE canale_ingaggio = ?" : "";
        String sql = """
                SELECT dem_aperta,
                       COUNT(*) AS totale,
                       COUNT(*) FILTER (WHERE visita_stand) AS visitatori_stand,
                       ROUND(
                           100.0 * COUNT(*) FILTER (WHERE visita_stand) / NULLIF(COUNT(*), 0), 1
                       ) AS tasso_conversione_pct
                FROM partecipanti %s
                GROUP BY dem_aperta
                ORDER BY dem_aperta DESC
                """.formatted(clausolaFiltro);
        return jdbcTemplate.queryForList(sql, parametriFiltro(canaleIngaggio).toArray());
    }

    private List<Object> parametriFiltro(String canaleIngaggio) {
        List<Object> parametri = new ArrayList<>();
        if (canaleIngaggio != null) parametri.add(canaleIngaggio);
        return parametri;
    }

    private String colonnaSicura(String dimensione) {
        String colonna = DIMENSIONI_CONSENTITE.get(dimensione);
        if (colonna == null) {
            throw new IllegalArgumentException(
                    "Dimensione non valida: " + dimensione + ". Valori ammessi: " + DIMENSIONI_CONSENTITE.keySet());
        }
        return colonna;
    }
}
