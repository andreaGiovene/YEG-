package factorystudios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CongressoDashboardApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CongressoDashboardApplication.class, args);
        boolean profiloEtl = java.util.Arrays.stream(args)
                .anyMatch(arg -> arg.equals("--spring.profiles.active=etl"));
        if (profiloEtl) {
            int codiceUscita = SpringApplication.exit(context);
            System.exit(codiceUscita);
        }
    }
}
