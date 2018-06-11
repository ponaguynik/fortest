package com.test.logger;

import org.apache.logging.log4j.core.LogEvent;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "TEST_LOG")
public class TestLog extends Log {

    public TestLog() {
    }

    public TestLog(LogEvent wrappedEvent) {
        super(wrappedEvent);
    }
}
