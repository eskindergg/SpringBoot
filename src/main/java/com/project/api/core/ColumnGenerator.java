package com.project.api.core;

import org.hibernate.dialect.Dialect;
import org.hibernate.generator.EventType;
import org.hibernate.generator.OnExecutionGenerator;

import java.util.EnumSet;

public class ColumnGenerator implements OnExecutionGenerator {

    @Override
    public boolean referenceColumnsInSql(Dialect dialect) {
        return true;
    }

    @Override
    public boolean writePropertyValue() {
        return true;
    }

    @Override
    public String[] getReferencedColumnValues(Dialect dialect) {
        return new String[]{dialect.currentTimestamp()};
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
        return EnumSet.of(EventType.INSERT, EventType.UPDATE);
    }

}
