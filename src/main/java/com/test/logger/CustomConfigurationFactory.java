/*
package com.test.logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.db.AbstractDatabaseAppender;
import org.apache.logging.log4j.core.appender.db.jpa.JpaDatabaseManager;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;

import java.net.URI;

@Plugin(name = "CustomConfigurationFactory", category = ConfigurationFactory.CATEGORY)
@Order(50)
public class CustomConfigurationFactory extends ConfigurationFactory {

    private static Configuration createConfiguration(final String name, ConfigurationBuilder<BuiltConfiguration> builder) {
        builder.setConfigurationName(name);
        builder.setStatusLevel(Level.ERROR);
        builder.add(builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.NEUTRAL).
                addAttribute("level", Level.DEBUG));

        builder.add(consoleAppenderBuilder(builder));
        builder.add(jpaAppenderBuilder(builder));

        builder.add(builder.newRootLogger(Level.DEBUG)
                .add(builder.newAppenderRef("Db")));
        return builder.build();
    }

    @Override
    public Configuration getConfiguration(final LoggerContext loggerContext, final ConfigurationSource source) {
        return getConfiguration(loggerContext, source.toString(), null);
    }

    private static AppenderComponentBuilder jpaAppenderBuilder(ConfigurationBuilder<BuiltConfiguration> builder) {
        return builder.newAppender("Db", "JPA")
                .addAttribute("persistenceUnitName", "loggingPersistenceUnit")
                .addAttribute("entityClassName", "com.test.logger.Log");
    }

    private static AppenderComponentBuilder consoleAppenderBuilder(ConfigurationBuilder<BuiltConfiguration> builder) {
        return builder.newAppender("Stdout", "CONSOLE")
                .addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT)
                .add(builder.newLayout("PatternLayout")
                        .addAttribute("pattern", "%d [%t] %-5level: %msg%n%throwable"))
                .add(builder.newFilter("MarkerFilter", Filter.Result.DENY, Filter.Result.NEUTRAL)
                        .addAttribute("marker", "FLOW"));
    }

    @Override
    public Configuration getConfiguration(final LoggerContext loggerContext, final String name, final URI configLocation) {
        ConfigurationBuilder<BuiltConfiguration> builder = newConfigurationBuilder();
        return createConfiguration(name, builder);
    }

    @Override
    protected String[] getSupportedTypes() {
        return new String[] {"*"};
    }

    private static class MyAppender extends AbstractDatabaseAppender<JpaDatabaseManager> {

        public MyAppender(String name, Filter filter, boolean ignoreExceptions, JpaDatabaseManager manager) {
            super(name, filter, ignoreExceptions, manager);
        }


    }
}
*/
