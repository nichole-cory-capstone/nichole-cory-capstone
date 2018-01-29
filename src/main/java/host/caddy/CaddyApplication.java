package host.caddy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class CaddyApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {

		SpringApplication.run(CaddyApplication.class, args);
	}

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CaddyApplication.class);
    }

}
