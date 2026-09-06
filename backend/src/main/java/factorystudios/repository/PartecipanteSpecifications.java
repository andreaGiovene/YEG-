package factorystudios.repository;

import org.springframework.data.jpa.domain.Specification;

import factorystudios.model.Partecipante;

/**
 * Costruisce filtri combinabili per l'endpoint di elenco partecipanti
 * (GET /api/partecipanti). Ogni filtro è opzionale: se il parametro
 * è {@code null}, vuoto o composto solo da spazi la relativa clausola viene
 * ignorata. Le specifiche prodotte possono essere passate direttamente a
 * {@link PartecipanteRepository#findAll(org.springframework.data.jpa.domain.Specification,
 * org.springframework.data.domain.Pageable)}.
 */
public final class PartecipanteSpecifications {

    /**
     * Classe di utilità: non deve essere istanziata.
     */
    private PartecipanteSpecifications() {
    }

    /**
     * Costruisce una specifica JPA con tutti i filtri valorizzati.
     *
     * <p>I filtri testuali vengono confrontati con uguaglianza esatta dopo
     * aver verificato che non siano vuoti; i filtri booleani vengono applicati
     * anche quando il valore è {@code false}. Perciò {@code null} significa
     * "nessun filtro", mentre {@code false} è un valore effettivo di ricerca.</p>
     *
     * @param tipologiaStakeholder tipologia dello stakeholder da cercare
     * @param regione regione del partecipante da cercare
     * @param canaleIngaggio canale di ingaggio da cercare
     * @param visitaStand indica se filtrare i partecipanti che hanno visitato lo stand
     * @param presenzaSimposio indica se filtrare i partecipanti presenti al simposio
     * @return specifica JPA con le condizioni richieste
     */
    public static Specification<Partecipante> filtra(String tipologiaStakeholder, String regione,
                                                       String canaleIngaggio, Boolean visitaStand,
                                                       Boolean presenzaSimposio) {
        return (root, query, cb) -> {
            // Il predicato TRUE consente di aggiungere dinamicamente le condizioni
            // senza gestire casi speciali quando nessun filtro è valorizzato.
            var predicate = cb.conjunction();
            if (tipologiaStakeholder != null && !tipologiaStakeholder.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("tipologiaStakeholder"), tipologiaStakeholder));
            }
            if (regione != null && !regione.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("regione"), regione));
            }
            if (canaleIngaggio != null && !canaleIngaggio.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("canaleIngaggio"), canaleIngaggio));
            }
            if (visitaStand != null) {
                predicate = cb.and(predicate, cb.equal(root.get("visitaStand"), visitaStand));
            }
            if (presenzaSimposio != null) {
                predicate = cb.and(predicate, cb.equal(root.get("presenzaSimposio"), presenzaSimposio));
            }
            return predicate;
        };
    }
}
