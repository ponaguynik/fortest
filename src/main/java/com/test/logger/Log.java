package com.test.logger;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.db.jpa.converter.InstantAttributeConverter;
import org.apache.logging.log4j.core.time.Instant;
import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.apache.logging.log4j.message.StructuredDataMessage;
import org.apache.logging.log4j.util.IndexedReadOnlyStringMap;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.lang.reflect.Field;

@Immutable
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@MappedSuperclass
public abstract class Log extends AbstractLog {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "RID", updatable = false)
    @GeneratorType(type = RidGenerator.class, when = GenerationTime.INSERT)
    private String rid;
    @Column(name = "LOG_DATE")
    @Convert(converter = InstantAttributeConverter.class)
    private Instant logDate;
    @Column(name = "COMPONENT_NAME")
    private String componentName;
    @Column(name = "MESSAGE", columnDefinition = "TEXT")
    private String message;
    @Column(name = "ACTION")
    private String action;
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", length = 20)
    private LogType type;
    @Column(name = "CID")
    private long cid;
    @Column(name = "USER_ID")
    private String userId;
    @Column(name = "LOGIN")
    private String login;
    @Column(name = "TARGET_ID")
    private String targetId;
    @Column(name = "TARGET_TYPE")
    private String targetType;

    public Log() {
    }

    public Log(LogEvent wrappedEvent) {
        if (wrappedEvent.getMessage() instanceof StructuredDataMessage) {
            StructuredDataMessage msg = (StructuredDataMessage) wrappedEvent.getMessage();
            buildByStructuredDataMessage(msg);
        }
        this.logDate = wrappedEvent.getInstant();
        this.message = wrappedEvent.getMessage().getFormat();
    }

    private void buildByStructuredDataMessage(StructuredDataMessage msg) {
        IndexedReadOnlyStringMap map = msg.getIndexedReadOnlyStringMap();
        for (Field field : this.getClass().getDeclaredFields()) {
            Object value;
            if ((value = map.getValue(field.getName())) != null) {
                ReflectionUtil.setFieldValue(field, this, value);
            }
        }
    }

    public enum LogType {
        SYSTEM, CRITICAL_EXCEPTION, EXCEPTION, USER_ACTIVITY, INFO, DEBUG
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonGetter("logId")
    public String getRid() {
        return rid;
    }

    @JsonSetter("logId")
    public void setRid(String rid) {
        this.rid = rid;
    }

    public Instant getLogDate() {
        return logDate;
    }

    void setLogDate(Instant logDate) {
        this.logDate = logDate;
    }

    public String getComponentName() {
        return componentName;
    }

    void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    void setMessage(String message) {
        this.message = message;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LogType getType() {
        return type;
    }

    public void setType(LogType type) {
        this.type = type;
    }

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
}
