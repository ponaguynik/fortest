package com.test.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StructuredDataMessage;

public class LoggerTest {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {
        StructuredDataMessage msg = new StructuredDataMessage("", "Some info", "info");
        msg.put("componentName", LoggerTest.class.getSimpleName());
        msg.put("action", "DO_IT");
        LOGGER.debug(msg);
        LOGGER.error("Holy moly something went wrong!");
    }
}
