package hello.service.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import hello.service.VersionInfoService;

@Service
public class VersionInfoServiceImpl implements VersionInfoService {

  @PersistenceContext(unitName = "dataSource")
  private EntityManager entityManager;

  @PersistenceContext(unitName = "secondaryDataSource")
  private EntityManager secondaryEntityManager;

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public String resolvePrimaryDbMetadata() {

    final StringBuffer infoStrBuf = new StringBuffer();

    final Session session = (Session) entityManager.getDelegate();
    session.doWork(connection -> infoStrBuf.append(connection.getMetaData().getDatabaseProductName() + " ") //
        .append(connection.getMetaData().getDatabaseProductVersion() + " ") //
        .append(connection.getMetaData().getDriverName() + " ") //
        .append(connection.getMetaData().getDriverVersion() + "\n") //
        .append("URL: " + connection.getMetaData().getURL() + " ") //
        .append("Username: " + connection.getMetaData().getUserName() + "\n"));

    return infoStrBuf.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public String resolveSecondaryDbMetadata() {

    final StringBuffer infoStrBuf = new StringBuffer();

    final Session session = (Session) secondaryEntityManager.getDelegate();
    session.doWork(connection -> infoStrBuf.append(connection.getMetaData().getDatabaseProductName() + " ") //
        .append(connection.getMetaData().getDatabaseProductVersion() + " ") //
        .append(connection.getMetaData().getDriverName() + " ") //
        .append(connection.getMetaData().getDriverVersion() + "\n") //
        .append("URL: " + connection.getMetaData().getURL() + " ") //
        .append("Username: " + connection.getMetaData().getUserName() + "\n"));

    return infoStrBuf.toString();
  }
}
