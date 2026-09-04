package factorystudios.etl;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Script di caricamento ripetibile (punto 4.1 della traccia): legge
 * Dataset_Evento_Congresso_2025.xlsx e popola le tre tabelle.
 * Si attiva solo con il profilo "etl":
 *   mvn spring-boot:run -Dspring-boot.run.profiles=etl -Dspring-boot.run.arguments=/percorso/dataset.xlsx
 * Ripetibile: ogni esecuzione svuota e ricarica le tabelle (TRUNCATE),
 * non fa append — evita duplicati se lanciato più volte.
 */
@Component
@Profile("etl")
public class CaricatoreExcel implements CommandLineRunner {

    private static final DateTimeFormatter FORMATO_DATA_ITALIANO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final JdbcTemplate jdbcTemplate;

    public CaricatoreExcel(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length == 0) {
            throw new IllegalArgumentException(
                    "Uso: --spring.profiles.active=etl --dataset=/percorso/Dataset_Evento_Congresso_2025.xlsx");
        }
        String percorsoFile = Arrays.stream(args)
                .filter(a -> a.startsWith("--dataset="))
                .map(a -> a.substring("--dataset=".length()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Parametro --dataset=<percorso> mancante"));

        System.out.println("Caricamento da: " + percorsoFile);

        try (var workbook = WorkbookFactory.create(new FileInputStream(percorsoFile))) {
            List<DefinizioneTouchpoint> definizioni = leggiDizionario(workbook.getSheet("01_Interazioni"));
            svuotaTabelle();
            caricaDizionario(definizioni);
            int righeCaricate = caricaPartecipanti(workbook.getSheet("02_Partecipanti"), definizioni);
            System.out.println("Caricamento completato: " + righeCaricate + " partecipanti, "
                    + definizioni.size() + " touchpoint definiti.");
        }
    }

    private void svuotaTabelle() {
        jdbcTemplate.execute("TRUNCATE TABLE partecipante_touchpoint, partecipanti, touchpoint_definizioni CASCADE");
    }

    private record DefinizioneTouchpoint(
            String intestazioneFoglio02, String nomeTecnico, String tipoDato,
            String fasePercorso, String descrizione) {
    }

    private List<DefinizioneTouchpoint> leggiDizionario(Sheet sheet) {
        List<DefinizioneTouchpoint> risultato = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;
            String intestazione = leggiTesto(row.getCell(1));
            String nomeTecnico = leggiTesto(row.getCell(2));
            if (nomeTecnico == null || nomeTecnico.isBlank()) continue;
            risultato.add(new DefinizioneTouchpoint(
                    intestazione,
                    nomeTecnico,
                    leggiTesto(row.getCell(3)),
                    leggiTesto(row.getCell(4)),
                    leggiTesto(row.getCell(5))
            ));
        }
        return risultato;
    }

    private void caricaDizionario(List<DefinizioneTouchpoint> definizioni) {
        String sql = "INSERT INTO touchpoint_definizioni (nome_tecnico, intestazione, tipo_dato, fase_percorso, descrizione) "
                + "VALUES (?, ?, ?, ?, ?)";
        for (var d : definizioni) {
            jdbcTemplate.update(sql, d.nomeTecnico(), d.intestazioneFoglio02(), d.tipoDato(),
                    d.fasePercorso(), d.descrizione());
        }
    }

    private static final List<String> TOUCHPOINT_EVENTO = List.of(
            "in_database_dem", "dem_inviata", "dem_consegnata", "dem_aperta",
            "li_annuncio_reach", "li_annuncio_interazione", "li_recap_reach", "li_recap_interazione",
            "visita_stand", "accesso_sala_vip", "presenza_simposio"
    );

    private int caricaPartecipanti(Sheet sheet, List<DefinizioneTouchpoint> definizioni) {
        String sqlPartecipante = """
                INSERT INTO partecipanti (
                    id, nome_cognome, email, tipologia_stakeholder, regione, canale_ingaggio,
                    in_database_dem, dem_inviata, dem_consegnata, dem_aperta,
                    li_annuncio_reach, li_annuncio_interazione, li_recap_reach, li_recap_interazione,
                    visita_stand, giorno_visita, visualizzazioni, scroll_approfondimento,
                    accesso_sala_vip, risposte_wordcloud,
                    presenza_simposio, permanenza_min, focus_rate, quiz_completati
                ) VALUES (?,?,?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?, ?,?,?,?)
                """;
        String sqlFatto = """
                INSERT INTO partecipante_touchpoint (partecipante_id, nome_tecnico, avvenuto, valore_numerico, valore_data)
                VALUES (?, ?, ?, ?, ?)
                """;

        int righe = 0;
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;
            if (row.getCell(0) == null) continue;

            int id = (int) leggiNumero(row.getCell(0));
            String nomeCognome = leggiTesto(row.getCell(1));
            String email = leggiTesto(row.getCell(2));
            String tipologia = leggiTesto(row.getCell(3));
            String regione = leggiTesto(row.getCell(4));
            String canale = leggiTesto(row.getCell(5));

            boolean inDatabaseDem = leggiBooleano(row.getCell(6));
            boolean demInviata = leggiBooleano(row.getCell(7));
            boolean demConsegnata = leggiBooleano(row.getCell(8));
            boolean demAperta = leggiBooleano(row.getCell(9));
            boolean liAnnuncioReach = leggiBooleano(row.getCell(10));
            boolean liAnnuncioInterazione = leggiBooleano(row.getCell(11));
            boolean liRecapReach = leggiBooleano(row.getCell(12));
            boolean liRecapInterazione = leggiBooleano(row.getCell(13));

            boolean visitaStand = leggiBooleano(row.getCell(14));
            LocalDate giornoVisita = leggiDataItaliana(row.getCell(15));
            int visualizzazioni = (int) leggiNumero(row.getCell(16));
            int scroll = (int) leggiNumero(row.getCell(17));

            boolean accessoSalaVip = leggiBooleano(row.getCell(18));
            int risposteWordcloud = (int) leggiNumero(row.getCell(19));

            boolean presenzaSimposio = leggiBooleano(row.getCell(20));
            BigDecimal permanenzaMin = leggiDecimaleONullo(row.getCell(21));
            BigDecimal focusRate = leggiDecimaleONullo(row.getCell(22));
            int quizCompletati = (int) leggiNumero(row.getCell(23));

            jdbcTemplate.update(sqlPartecipante,
                    id, nomeCognome, email, tipologia, regione, canale,
                    inDatabaseDem, demInviata, demConsegnata, demAperta,
                    liAnnuncioReach, liAnnuncioInterazione, liRecapReach, liRecapInterazione,
                    visitaStand, giornoVisita == null ? null : Date.valueOf(giornoVisita),
                    visualizzazioni, scroll,
                    accessoSalaVip, risposteWordcloud,
                    presenzaSimposio, permanenzaMin, focusRate, quizCompletati);

            Map<String, Boolean> valoriBooleani = Map.ofEntries(
                    Map.entry("in_database_dem", inDatabaseDem), Map.entry("dem_inviata", demInviata),
                    Map.entry("dem_consegnata", demConsegnata), Map.entry("dem_aperta", demAperta),
                    Map.entry("li_annuncio_reach", liAnnuncioReach), Map.entry("li_annuncio_interazione", liAnnuncioInterazione),
                    Map.entry("li_recap_reach", liRecapReach), Map.entry("li_recap_interazione", liRecapInterazione),
                    Map.entry("visita_stand", visitaStand), Map.entry("accesso_sala_vip", accessoSalaVip),
                    Map.entry("presenza_simposio", presenzaSimposio)
            );
            for (String nomeTecnico : TOUCHPOINT_EVENTO) {
                boolean avvenuto = valoriBooleani.get(nomeTecnico);
                if (avvenuto) {
                    jdbcTemplate.update(sqlFatto, id, nomeTecnico, true, null,
                            "visita_stand".equals(nomeTecnico) && giornoVisita != null ? Date.valueOf(giornoVisita) : null);
                }
            }
            righe++;
        }
        return righe;
    }

    private String leggiTesto(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> null;
        };
    }

    private double leggiNumero(Cell cell) {
        if (cell == null) return 0;
        if (cell.getCellType() == CellType.STRING) {
            String v = cell.getStringCellValue().trim();
            return v.isEmpty() ? 0 : Double.parseDouble(v.replace(",", "."));
        }
        return cell.getNumericCellValue();
    }

    private boolean leggiBooleano(Cell cell) {
        if (cell == null) return false;
        return leggiNumero(cell) != 0;
    }

    private BigDecimal leggiDecimaleONullo(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) return null;
        return BigDecimal.valueOf(leggiNumero(cell));
    }

    private LocalDate leggiDataItaliana(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }
        String testo = leggiTesto(cell);
        if (testo == null || testo.isBlank()) return null;
        return LocalDate.parse(testo, FORMATO_DATA_ITALIANO);
    }
}
