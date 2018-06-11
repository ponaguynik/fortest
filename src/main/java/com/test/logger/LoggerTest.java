package com.test.logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.async.AsyncLoggerConfig;
import org.apache.logging.log4j.core.config.*;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.message.StructuredDataMessage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.persistence.EntityManagerFactory;

public class LoggerTest {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(Config.class);
        ctx.refresh();
        EntityManagerFactory entityManagerFactory= ctx.getBean(EntityManagerFactory.class);
        reconfigure(entityManagerFactory);
        StructuredDataMessage msg = new StructuredDataMessage("", "Some info", "info");
        msg.put("componentName", LoggerTest.class.getSimpleName());
        msg.put("action", "DO_IT");
        LOGGER.debug(msg);
        LOGGER.error("Holy molly something went wrong!");
        System.exit(0);
    }

    public static void reconfigure(EntityManagerFactory entityManagerFactory) {
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder
                .setStatusLevel(Level.WARN)
                .add(consoleAppenderBuilder(builder))
                .add(builder.newRootLogger(Level.WARN)
                        .add(builder.newAppenderRef(ConsoleAppender.PLUGIN_NAME)))
                .add(builder.newAsyncLogger("com.test.logger", ));
        Configuration configuration = builder.build();
        configuration.getAppender(ConsoleAppender.PLUGIN_NAME).start();
        Appender jpaAppender = MyJpaAppender.createAppender(MyJpaAppender.PLUGIN_NAME, null, null, null, "com.test.logger.TestLog", entityManagerFactory);
        jpaAppender.start();
        configuration.addAppender(jpaAppender);
        LoggerConfig loggerConfig = AsyncLoggerConfig .createLogger(true, Level.DEBUG, "com.test.logger", null, new AppenderRef[]{AppenderRef.createAppenderRef(MyJpaAppender.PLUGIN_NAME, null, null)}, null, configuration, null);
        loggerConfig.addAppender(jpaAppender , null, null);
        configuration.addLogger("com.test.logger", loggerConfig);
        LoggerContext.getContext(false).updateLoggers(configuration);
    }

    private static AppenderComponentBuilder consoleAppenderBuilder(ConfigurationBuilder<BuiltConfiguration> builder) {
        return builder.newAppender(ConsoleAppender.PLUGIN_NAME, ConsoleAppender.PLUGIN_NAME)
                .addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT)
                .add(builder.newLayout("PatternLayout")
                        .addAttribute("pattern", "%d [%t] %-5level: %msg%n%throwable"));
    }
    private static AppenderComponentBuilder jpaAppenderBuilder(ConfigurationBuilder<BuiltConfiguration> builder,
                                                               EntityManagerFactory entityManagerFactory) {
        return builder.newAppender(MyJpaAppender.PLUGIN_NAME, MyJpaAppender.PLUGIN_NAME)
                .addAttribute("entityManagerFactory", entityManagerFactory)
                .addAttribute("entityClassName", "com.test.logger.TestLog");
    }

}
