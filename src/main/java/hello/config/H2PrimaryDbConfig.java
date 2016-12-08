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
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import hello.persistence.repository.primary.ProductRepository;
import hello.persistence.repository.primary.UserRepository;

/**
 * Created by boli on 2016-11-30.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackageClasses = { ProductRepository.class , UserRepository.class },
                       entityManagerFactoryRef = "entityManager",
                       transactionManagerRef = "transactionManager")
public class H2PrimaryDbConfig {

  @Primary
  @Bean(name="dataSource")
  public DataSource dataSource() {
    return DataSourceBuilder.create()
        .driverClassName("org.h2.Driver")
        .url("jdbc:h2:mem:primary;MODE=Oracle;DB_CLOSE_ON_EXIT=FALSE;INIT=CREATE SCHEMA IF NOT EXISTS primary_test")
        .username("sa")
        .password("")
        .build();
  }

  @Primary
  @Bean(name = "entityManager")
  public EntityManagerFactory entityManagerFactory(EntityManagerFactoryBuilder builder) {

    HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
    jpaVendorAdapter.setShowSql(true);
    jpaVendorAdapter.setGenerateDdl(true);
    jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");
    jpaVendorAdapter.setDatabase(Database.H2);

    final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setDataSource(dataSource());
    factory.setJpaPropertyMap(hibernateProperties());
    factory.setJpaVendorAdapter(jpaVendorAdapter);
    factory.setPersistenceUnitName("dataSource");
    factory.setPackagesToScan("hello.persistence.entity.primary");
    factory.setPersistenceProvider(new HibernatePersistenceProvider());
    factory.afterPropertiesSet();

    return factory.getObject();
  }

  @Primary
  @Bean(name = "entityManager")
  public EntityManager entityManager(@Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
    return entityManagerFactory.createEntityManager();
  }

  @Primary
  @Bean(name = "transactionManager")
  public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder) {
    final JpaTransactionManager txManager = new JpaTransactionManager();
    txManager.setEntityManagerFactory(entityManagerFactory(builder));
    txManager.setDataSource(dataSource());
    txManager.afterPropertiesSet();

    return txManager;
  }

  private Map<String, Object> hibernateProperties() {

    final Map<String, Object> hibernateProperties = new HashMap<>();

    hibernateProperties.put("hibernate.connection.autocommit", "true");
    hibernateProperties.put("hibernate.default_schema", "primary_test");
    hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
    hibernateProperties.put("hibernate.hbm2ddl.auto", "create-drop");

    return hibernateProperties;
  }
}
