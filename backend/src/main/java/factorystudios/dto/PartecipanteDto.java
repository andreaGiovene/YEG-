package factorystudios.dto;

import factorystudios.model.Partecipante;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Rappresentazione di un partecipante restituita dalle API REST.
 *
 * <p>Il DTO espone solo i dati necessari al frontend e mantiene separato il
 * contratto HTTP dall'entità JPA. I valori relativi a permanenza, focus e
 * giorno della visita possono essere {@code null} quando il partecipante non
 * ha completato il relativo touchpoint.</p>
 *
 * @param id identificativo univoco del partecipante
 * @param nomeCognome nome e cognome del partecipante
 * @param email indirizzo email del partecipante
 * @param tipologiaStakeholder tipologia anagrafica dello stakeholder
 * @param regione regione di appartenenza
 * @param canaleIngaggio canale attraverso il quale il partecipante è stato ingaggiato
 * @param demAperta indica se il partecipante ha aperto la DEM
 * @param visitaStand indica se il partecipante ha visitato lo stand
 * @param giornoVisita giorno della visita allo stand, se disponibile
 * @param accessoSalaVip indica se il partecipante ha avuto accesso alla sala VIP
 * @param presenzaSimposio indica se il partecipante era presente al simposio
 * @param permanenzaMin durata della permanenza al simposio in minuti, se disponibile
 * @param focusRate percentuale di attenzione rilevata, se disponibile
 * @param quizCompletati numero di quiz completati dal partecipante
 */
public record PartecipanteDto(
        Integer id,
        String nomeCognome,
        String email,
        String tipologiaStakeholder,
        String regione,
        String canaleIngaggio,
        Boolean demAperta,
        Boolean visitaStand,
        LocalDate giornoVisita,
        Boolean accessoSalaVip,
        Boolean presenzaSimposio,
        BigDecimal permanenzaMin,
        BigDecimal focusRate,
        Integer quizCompletati
) {
    /**
     * Crea il DTO a partire dall'entità persistita.
     *
     * @param p entità JPA del partecipante da convertire
     * @return DTO pronto per essere serializzato nella risposta REST
     */
    public static PartecipanteDto from(Partecipante p) {
        return new PartecipanteDto(
                p.getId(), p.getNomeCognome(), p.getEmail(), p.getTipologiaStakeholder(),
                p.getRegione(), p.getCanaleIngaggio(), p.getDemAperta(), p.getVisitaStand(),
                p.getGiornoVisita(), p.getAccessoSalaVip(), p.getPresenzaSimposio(),
                p.getPermanenzaMin(), p.getFocusRate(), p.getQuizCompletati()
        );
    }
}
