package factorystudios.repository;

import factorystudios.model.Partecipante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PartecipanteRepository
        extends JpaRepository<Partecipante, Integer>, JpaSpecificationExecutor<Partecipante> {

    // JpaSpecificationExecutor consente filtri combinabili (tipologia, regione,
    // canale...) costruiti dinamicamente nel service, con paginazione nativa.
    Page<Partecipante> findAll(Pageable pageable);
}
