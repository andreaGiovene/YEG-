package factorystudios.web;

import factorystudios.repository.AggregazioniRepository;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoint di aggregazione: restituiscono dati già pronti per il grafico
 * (raggruppati e calcolati nel database), non righe grezze da elaborare
 * nel browser — come richiesto dal punto 4.2 della traccia.
 *
 * Tutti accettano l'opzionale ?canaleIngaggio=, il filtro che agisce su
 * tutta la pagina (punto 4.3): quando valorizzato, ogni vista mostra solo
 * i dati relativi a quel canale.
 */
@RestController
@RequestMapping("/api/aggregazioni")
public class AggregazioniController {

    private final AggregazioniRepository repository;

    public AggregazioniController(AggregazioniRepository repository) {
        this.repository = repository;
    }

    /** Funnel del percorso: dalle persone raggiunte alla presenza al simposio. */
    @GetMapping("/funnel")
    public List<Map<String, Object>> funnel(@RequestParam(required = false) String canaleIngaggio) {
        return repository.funnel(canaleIngaggio);
    }

    /**
     * Confronto per dimensione anagrafica (tipologia_stakeholder | regione | canale_ingaggio).
     * Incrocia la dimensione scelta con i comportamenti (email aperte, stand, simposio).
     */
    @GetMapping("/confronto")
    public List<Map<String, Object>> confronto(
            @RequestParam String dimensione,
            @RequestParam(required = false) String canaleIngaggio) {
        return repository.confrontoPerDimensione(dimensione, canaleIngaggio);
    }

    /** Andamento per giornata del congresso (visite stand, accessi sala VIP). */
    @GetMapping("/andamento-giornaliero")
    public List<Map<String, Object>> andamentoGiornaliero(@RequestParam(required = false) String canaleIngaggio) {
        return repository.andamentoPerGiorno(canaleIngaggio);
    }

    /** Incrocio: chi apre l'email si presenta poi davvero allo stand? */
    @GetMapping("/conversione-email-stand")
    public List<Map<String, Object>> conversioneEmailStand(@RequestParam(required = false) String canaleIngaggio) {
        return repository.conversioneEmailVsStand(canaleIngaggio);
    }
}
