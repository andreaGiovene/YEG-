package factorystudios.repository;

import factorystudios.model.Partecipante;
import org.springframework.data.jpa.domain.Specification;

/**
 * Costruisce filtri combinabili per l'endpoint di elenco partecipanti
 * (GET /api/partecipanti). Ogni filtro è opzionale: se il parametro
 * è null la clausola viene ignorata.
 */
public final class PartecipanteSpecifications {

    private PartecipanteSpecifications() {
    }

    public static Specification<Partecipante> filtra(String tipologiaStakeholder, String regione,
                                                       String canaleIngaggio, Boolean visitaStand,
                                                       Boolean presenzaSimposio) {
        return (root, query, cb) -> {
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
