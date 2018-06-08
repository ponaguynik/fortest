package com.test.logger;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.appender.db.AbstractDatabaseManager;
import org.apache.logging.log4j.core.appender.db.jpa.JpaAppender;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.io.Serializable;
import java.lang.reflect.Constructor;

// TODO: Periodically compare with original JpaDatabaseManaged for fixes
public class MyJpaDatabaseManager extends AbstractDatabaseManager {
    private static final MyJpaDatabaseManagerFactory FACTORY = new MyJpaDatabaseManagerFactory();

    private final String entityClassName;
    private final Constructor<? extends Log> entityConstructor;
    private final String persistenceUnitName;

    private EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;
    private EntityTransaction transaction;

    private MyJpaDatabaseManager(final String name, final int bufferSize,
                               final Class<? extends Log> entityClass,
                               final Constructor<? extends Log> entityConstructor,
                               final String persistenceUnitName) {
        super(name, bufferSize);
        this.entityClassName = entityClass.getName();
        this.entityConstructor = entityConstructor;
        this.persistenceUnitName = persistenceUnitName;
    }

    @Override
    protected void startupInternal() {
        this.entityManagerFactory = Persistence.createEntityManagerFactory(this.persistenceUnitName);
    }

    @Override
    protected boolean shutdownInternal() {
        boolean closed = true;
        if (this.entityManager != null || this.transaction != null) {
            closed = this.commitAndClose();
        }
        if (this.entityManagerFactory != null && this.entityManagerFactory.isOpen()) {
            this.entityManagerFactory.close();
        }
        return closed;
    }

    @Override
    protected void connectAndStart() {
        try {
            this.entityManager = this.entityManagerFactory.createEntityManager();
            this.transaction = this.entityManager.getTransaction();
            this.transaction.begin();
        } catch (final Exception e) {
            throw new AppenderLoggingException(
                    "Cannot write logging event or flush buffer; manager cannot create EntityManager or transaction.", e
            );
        }
    }

    @Deprecated
    @Override
    protected void writeInternal(final LogEvent event) {
        writeInternal(event, null);
    }

    @Override
    protected void writeInternal(final LogEvent event, final Serializable serializable) {
        if (!this.isRunning() || this.entityManagerFactory == null || this.entityManager == null
                || this.transaction == null) {
            throw new AppenderLoggingException(
                    "Cannot write logging event; JPA manager not connected to the database.");
        }

        Log entity;
        try {
            entity = this.entityConstructor.newInstance(event);
        } catch (final Exception e) {
            throw new AppenderLoggingException("Failed to instantiate entity class [" + this.entityClassName + "].", e);
        }

        try {
            this.entityManager.persist(entity);
        } catch (final Exception e) {
            if (this.transaction != null && this.transaction.isActive()) {
                this.transaction.rollback();
                this.transaction = null;
            }
            throw new AppenderLoggingException("Failed to insert record for log event in JPA manager: " +
                    e.getMessage(), e);
        }
    }

    @Override
    protected boolean commitAndClose() {
        boolean closed = true;
        try {
            if (this.transaction != null && this.transaction.isActive()) {
                this.transaction.commit();
            }
        } catch (final Exception e) {
            if (this.transaction != null && this.transaction.isActive()) {
                this.transaction.rollback();
            }
            throw new AppenderLoggingException("Failed to insert record for log event in JPA manager: " +
                    e.getMessage(), e);
        } finally {
            this.transaction = null;
            try {
                if (this.entityManager != null && this.entityManager.isOpen()) {
                    this.entityManager.close();
                }
            } catch (final Exception e) {
                logWarn("Failed to close entity manager while logging event or flushing buffer", e);
                closed = false;
            } finally {
                this.entityManager = null;
            }
        }
        return closed;
    }

    /**
     * Creates a JPA manager for use within the {@link JpaAppender}, or returns a suitable one if it already exists.
     *
     * @param name The name of the manager, which should include connection details, entity class name, etc.
     * @param bufferSize The size of the log event buffer.
     * @param entityClass The fully-qualified class name of the {@link Log} concrete
     *                    implementation.
     * @param entityConstructor The one-arg {@link LogEvent} constructor for the concrete entity class.
     * @param persistenceUnitName The name of the JPA persistence unit that should be used for persisting log events.
     * @return a new or existing JPA manager as applicable.
     */
    public static MyJpaDatabaseManager getJPADatabaseManager(final String name, final int bufferSize,
                                                           final Class<? extends Log>
                                                                   entityClass,
                                                           final Constructor<? extends Log>
                                                                   entityConstructor,
                                                           final String persistenceUnitName) {

        return AbstractDatabaseManager.getManager(
                name, new FactoryData(bufferSize, entityClass, entityConstructor, persistenceUnitName), FACTORY
        );
    }

    /**
     * Encapsulates data that {@link MyJpaDatabaseManagerFactory} uses to create managers.
     */
    private static final class FactoryData extends AbstractDatabaseManager.AbstractFactoryData {
        private final Class<? extends Log> entityClass;
        private final Constructor<? extends Log> entityConstructor;
        private final String persistenceUnitName;

        protected FactoryData(final int bufferSize, final Class<? extends Log> entityClass,
                              final Constructor<? extends Log> entityConstructor,
                              final String persistenceUnitName) {
            super(bufferSize, null);

            this.entityClass = entityClass;
            this.entityConstructor = entityConstructor;
            this.persistenceUnitName = persistenceUnitName;
        }
    }

    /**
     * Creates managers.
     */
    private static final class MyJpaDatabaseManagerFactory implements ManagerFactory<MyJpaDatabaseManager, FactoryData> {

        @Override
        public MyJpaDatabaseManager createManager(final String name, final FactoryData data) {
            return new MyJpaDatabaseManager(
                    name, data.getBufferSize(), data.entityClass, data.entityConstructor, data.persistenceUnitName
            );
        }
    }
}
