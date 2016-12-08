package hello.config;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import hello.persistence.repository.secondary.CustomerRepository;

/**
 * Created by boli on 2016-12-05.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackageClasses = { CustomerRepository.class},
                       entityManagerFactoryRef = "secondaryEntityManagerFactory",
                       transactionManagerRef = "secondaryTransactionManager")
public class H2SecondaryDbConfig {

  @Bean(name="secondaryDataSource")
  public DataSource secondaryDataSource() {
    return DataSourceBuilder.create()
        .driverClassName("org.h2.Driver")
        .url("jdbc:h2:mem:secondary;MODE=Oracle;DB_CLOSE_ON_EXIT=FALSE;INIT=CREATE SCHEMA IF NOT EXISTS secondary_test")
        .username("sa")
        .password("")
        .build();
  }

  @Bean(name="secondaryEntityManagerFactory")
  public EntityManagerFactory secondaryEntityManagerFactory(EntityManagerFactoryBuilder builder) {

    HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
    jpaVendorAdapter.setShowSql(true);
    jpaVendorAdapter.setGenerateDdl(true);
    jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");
    jpaVendorAdapter.setDatabase(Database.H2);

    final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setDataSource(secondaryDataSource());
    factory.setJpaPropertyMap(hibernateProperties());
    factory.setJpaVendorAdapter(jpaVendorAdapter);
    factory.setPersistenceUnitName("secondaryDataSource");
    factory.setPackagesToScan("hello.persistence.entity.secondary");
    factory.setPersistenceProvider(new HibernatePersistenceProvider());
    factory.afterPropertiesSet();

    return factory.getObject();
  }

  @Bean(name = "secondaryEntityManager")
  public EntityManager secondaryEntityManager(@Qualifier("secondaryEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
    return entityManagerFactory.createEntityManager();
  }

  @Bean(name="secondaryTransactionManager")
  public PlatformTransactionManager secondaryTransactionManager(EntityManagerFactoryBuilder builder) {
    final JpaTransactionManager txManager = new JpaTransactionManager();
    txManager.setEntityManagerFactory(secondaryEntityManagerFactory(builder));
    txManager.setDataSource(secondaryDataSource());

    return txManager;
  }

  private Map<String, Object> hibernateProperties() {

    final Map<String, Object> hibernateProperties = new HashMap<>();

    hibernateProperties.put("hibernate.connection.autocommit", "true");
    hibernateProperties.put("hibernate.default_schema", "secondary_test");
    hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
    hibernateProperties.put("hibernate.hbm2ddl.auto", "create-drop");

    return hibernateProperties;
  }
}
