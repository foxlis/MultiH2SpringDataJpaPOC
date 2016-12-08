package hello.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import hello.persistence.entity.primary.User;
import hello.persistence.entity.secondary.Customer;
import hello.persistence.repository.primary.ProductRepository;
import hello.persistence.repository.secondary.CustomerRepository;
import hello.service.VersionInfoService;

@Component
public class ApplicationArgumentsHandler implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(ApplicationArgumentsHandler.class);

  @Autowired
  private ConfigurableApplicationContext context;

  @Autowired
  ProductRepository productRepository;

  @Autowired
  CustomerRepository customerRepository;

  /**
   * {@inheritDoc}
   */
  @Override
  public void run(final String... args) throws Exception {

    testPersistence();

    printAppInfo(context);
    printDbInfo(context);

    context.registerShutdownHook();
    System.exit(0);
  }

  private void testPersistence() {
    // save a couple of customers
    productRepository.save(new User("Jack", "test@test.pl", 1));
    productRepository.save(new User("Jack", "new@new.pl", 2));

    // fetch all customers
    log.info("User found with findAll():");
    log.info("-------------------------------");
    for (User user : productRepository.findAll()) {
      log.info(user.toString());
    }

    log.info("Customer found with findAll():");
    log.info("-------------------------------");
    customerRepository.save(new Customer("Jack", "test@test.pl"));
    for (Customer user : customerRepository.findAll()) {
      log.info(user.toString());
    }
  }

  private static void printAppInfo(final ConfigurableApplicationContext ctx) {
    final String appInfo = String.format("\nPTE-MON Aggregator is configured by [%s] profile(s)", //
                                         ctx.getEnvironment().getProperty("spring.profiles.active", //
                                                                          String.class));
    log.info(appInfo);
    System.out.println(appInfo);
  }

  private static void printDbInfo(final ConfigurableApplicationContext ctx) {
    String dbInfo = "\nPrimaryDataSource " + ctx.getBean(VersionInfoService.class).resolvePrimaryDbMetadata();
    log.info(dbInfo);
    System.out.println(dbInfo);
    dbInfo = "\nSecondaryDataSource " + ctx.getBean(VersionInfoService.class).resolveSecondaryDbMetadata();
    log.info(dbInfo);
    System.out.println(dbInfo);
  }
}
