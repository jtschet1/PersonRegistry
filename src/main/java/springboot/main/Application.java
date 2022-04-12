package springboot.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//if you want to modularize (have packages for different things), you need these scans
//modularize is good
@ComponentScan({"springboot.services"})
@EntityScan("springboot.model") //entity = model that connects to DB table
@EnableJpaRepositories("springboot.repository")
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }
}
