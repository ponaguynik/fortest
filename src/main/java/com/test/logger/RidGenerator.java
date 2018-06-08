package com.test.logger;

import org.hibernate.Session;
import org.hibernate.tuple.ValueGenerator;

import java.util.UUID;

public class RidGenerator implements ValueGenerator<String> {

    @Override
    public String generateValue(Session session, Object owner) {
        return UUID.randomUUID().toString();
    }
}
