package sandrakorpi.csnfribeloppapi;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CsNfribeloppApiApplication {

    public static void main(String[] args) {
        //laddar variablerna som inte är synliga i application.properties.
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });
        SpringApplication.run(CsNfribeloppApiApplication.class, args);
    }

}
