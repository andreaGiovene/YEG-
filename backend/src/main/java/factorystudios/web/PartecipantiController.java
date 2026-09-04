package factorystudios.web;

import factorystudios.dto.PartecipanteDto;
import factorystudios.repository.PartecipanteRepository;
import factorystudios.repository.PartecipanteSpecifications;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * GET /api/partecipanti — elenco paginato con filtri opzionali.
 * Tutti i parametri di filtro sono opzionali e combinabili.
 */
@RestController
@RequestMapping("/api/partecipanti")
@Validated
public class PartecipantiController {

    private final PartecipanteRepository repository;

    public PartecipantiController(PartecipanteRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Page<PartecipanteDto> elenco(
            @RequestParam(required = false) String tipologiaStakeholder,
            @RequestParam(required = false) String regione,
            @RequestParam(required = false) String canaleIngaggio,
            @RequestParam(required = false) Boolean visitaStand,
            @RequestParam(required = false) Boolean presenzaSimposio,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(200) int size
    ) {
        var spec = PartecipanteSpecifications.filtra(
                tipologiaStakeholder, regione, canaleIngaggio, visitaStand, presenzaSimposio);
        return repository.findAll(spec, PageRequest.of(page, size))
                .map(PartecipanteDto::from);
    }
}
