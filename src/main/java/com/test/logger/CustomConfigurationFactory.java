package com.test.logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
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
        return builder
                .setConfigurationName(name)
                .setStatusLevel(Level.WARN)
                .add(consoleAppenderBuilder(builder))
                .add(builder.newRootLogger(Level.WARN)
                        .add(builder.newAppenderRef(ConsoleAppender.PLUGIN_NAME)))
                .build();
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
        return builder.newAppender(ConsoleAppender.PLUGIN_NAME, ConsoleAppender.PLUGIN_NAME)
                .addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT)
                .add(builder.newLayout("PatternLayout")
                        .addAttribute("pattern", "%d [%t] %-5level: %msg%n%throwable"));
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
}
