package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import hello.persistence.entity.primary.User;
import hello.persistence.repository.primary.ProductRepository;

@SpringBootApplication
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}

	@Autowired
	private ProductRepository productRepository;

	@Bean
	public CommandLineRunner demo() {
		return (args) -> {
			// save a couple of customers
			productRepository.save(new User("Jack", "test@test.pl", 1));
			productRepository.save(new User("Jack", "new@new.pl", 2));

			// fetch all customers
			log.info("User found with findAll():");
			log.info("-------------------------------");
			for (User user : productRepository.findAll()) {
				log.info(user.toString());
			}
		};
	}

}
