package com.test.logger;

import com.google.common.base.Preconditions;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.db.AbstractDatabaseAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.util.LoaderUtil;

import javax.persistence.EntityManagerFactory;
import java.lang.reflect.Constructor;

@Plugin(name = "MyJpa", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class MyJpaAppender extends AbstractDatabaseAppender<MyJpaDatabaseManager> {
    public static final String PLUGIN_NAME = "MyJpa";
    private final String description;

    private MyJpaAppender(final String name, final Filter filter, final boolean ignoreExceptions,
                          final MyJpaDatabaseManager manager) {
        super(name, filter, ignoreExceptions, manager);
        this.description = this.getName() + "{ manager=" + this.getManager() + " }";
    }

    @Override
    public String toString() {
        return this.description;
    }

    /**
     * Factory method for creating a JPA appender within the plugin manager.
     *
     * @param name                The name of the appender.
     * @param ignore              If {@code "true"} (default) exceptions encountered when appending events are logged; otherwise
     *                            they are propagated to the caller.
     * @param filter              The filter, if any, to use.
     * @param bufferSize          If an integer greater than 0, this causes the appender to buffer log events and flush whenever
     *                            the buffer reaches this size.
     * @param entityClassName     The fully qualified name of the concrete {@link Log}
     *                            implementation that has JPA annotations mapping it to a database table.
     * @return a new JPA appender.
     */
    // TODO: Init EntityManagerFactory
    @PluginFactory
    public static MyJpaAppender createAppender(
            @PluginAttribute("name") final String name,
            @PluginAttribute("ignoreExceptions") final String ignore,
            @PluginElement("Filter") final Filter filter,
            @PluginAttribute("bufferSize") final String bufferSize,
            @PluginAttribute("entityClassName") final String entityClassName,
            @PluginAttribute("entityManagerFactory") final EntityManagerFactory entityManagerFactory) {
        Preconditions.checkNotNull(entityManagerFactory, "EntityManagerFactory must not be null");

        final int bufferSizeInt = AbstractAppender.parseInt(bufferSize, 0);
        final boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);

        try {
            final Class<? extends AbstractLog> entityClass =
                    LoaderUtil.loadClass(entityClassName).asSubclass(AbstractLog.class);

            try {
                entityClass.getConstructor();
            } catch (final NoSuchMethodException e) {
                LOGGER.error("Entity class [{}] does not have a no-arg constructor. The JPA provider will reject it.",
                        entityClassName);
                return null;
            }

            final Constructor<? extends AbstractLog> entityConstructor =
                    entityClass.getConstructor(LogEvent.class);

            final String managerName = "jpaManager{ description=" + name + ", bufferSize=" + bufferSizeInt
                    + ", EntityManagerFactory=" + entityManagerFactory + ", entityClass=" + entityClass.getName() + '}';

            final MyJpaDatabaseManager manager = MyJpaDatabaseManager.getJPADatabaseManager(
                    managerName, bufferSizeInt, entityClass, entityConstructor, (EntityManagerFactory) entityManagerFactory
            );
            if (manager == null) {
                return null;
            }

            return new MyJpaAppender(name, filter, ignoreExceptions, manager);
        } catch (final ClassNotFoundException e) {
            LOGGER.error("Could not load entity class [{}].", entityClassName, e);
            return null;
        } catch (final NoSuchMethodException e) {
            LOGGER.error("Entity class [{}] does not have a constructor with a single argument of type LogEvent.",
                    entityClassName);
            return null;
        } catch (final ClassCastException e) {
            LOGGER.error("Entity class [{}] does not extend AbstractLogEventWrapperEntity.", entityClassName);
            return null;
        }
    }
}
