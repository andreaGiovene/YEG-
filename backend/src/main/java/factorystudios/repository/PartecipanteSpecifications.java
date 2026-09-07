package factorystudios.repository;

import org.springframework.data.jpa.domain.Specification;

import factorystudios.model.Partecipante;

/**
 * Factory di specifiche JPA per l'elenco dei partecipanti.
 *
 * <p>Una {@link Specification} descrive una condizione di ricerca senza
 * eseguire direttamente una query SQL. Spring Data JPA la traduce nella
 * clausola {@code WHERE} della query generata per l'entità {@link Partecipante}.
 * Questo separa la costruzione dinamica dei filtri dal controller e dal
 * repository.</p>
 *
 * <p>Il metodo {@link #filtra(String, String, String, Boolean, Boolean)}
 * restituisce una specifica che può essere passata a
 * {@link PartecipanteRepository#findAll(Specification,
 * org.springframework.data.domain.Pageable)}. Il repository, grazie a
 * {@code JpaSpecificationExecutor}, combina la specifica con la paginazione
 * e lascia a JPA il compito di generare ed eseguire la query.</p>
 *
 * <p>Ogni parametro è opzionale: {@code null}, stringa vuota o stringa
 * composta solo da spazi non aggiunge alcuna condizione. Per i booleani,
 * invece, {@code null} significa "ignora il filtro", mentre {@code false}
 * è un valore valido e cerca esplicitamente i partecipanti che non hanno
 * compiuto l'azione.</p>
 */
public final class PartecipanteSpecifications {

    /**
     * Classe di utilità senza stato: tutte le operazioni sono statiche, quindi
     * non avrebbe senso creare istanze di questa classe.
     */
    private PartecipanteSpecifications() {
    }

    /**
     * Costruisce una specifica JPA con tutti i filtri valorizzati.
     *
     * <p>La lambda restituita implementa il metodo astratto di
     * {@link Specification}. Spring la invoca durante la costruzione della
     * query e fornisce tre oggetti della Criteria API:</p>
     *
     * <ul>
     *   <li>{@code root}: rappresenta la tabella/entità {@code Partecipante}
     *       e permette di riferirsi ai suoi attributi;</li>
     *   <li>{@code query}: rappresenta la query JPA in costruzione;</li>
     *   <li>{@code cb}: {@link jakarta.persistence.criteria.CriteriaBuilder}
     *       crea le condizioni, ad esempio un confronto di uguaglianza.</li>
     * </ul>
     *
     * <p>Il predicato iniziale è la condizione sempre vera restituita da
     * {@code cb.conjunction()}. Ogni filtro presente viene aggiunto con
     * {@code cb.and(...)}, ottenendo una logica AND: un partecipante deve
     * soddisfare contemporaneamente tutte le condizioni specificate.</p>
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
            // TRUE consente di aggiungere condizioni una alla volta senza
            // gestire un caso speciale quando nessun filtro è valorizzato.
            var predicate = cb.conjunction();

            if (tipologiaStakeholder != null && !tipologiaStakeholder.isBlank()) {
                // root.get(...) collega il nome dell'attributo Java al campo
                // della query; cb.equal(...) genera un confronto esatto.
                predicate = cb.and(predicate, cb.equal(root.get("tipologiaStakeholder"), tipologiaStakeholder));
            }

            if (regione != null && !regione.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("regione"), regione));
            }

            if (canaleIngaggio != null && !canaleIngaggio.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("canaleIngaggio"), canaleIngaggio));
            }

            if (visitaStand != null) {
                // Il controllo è volutamente "!= null": false deve produrre
                // una condizione valida, non essere scambiato per assenza di filtro.
                predicate = cb.and(predicate, cb.equal(root.get("visitaStand"), visitaStand));
            }

            if (presenzaSimposio != null) {
                predicate = cb.and(predicate, cb.equal(root.get("presenzaSimposio"), presenzaSimposio));
            }

            // Il repository userà questo predicato nella clausola WHERE.
            return predicate;
        };
    }
}
