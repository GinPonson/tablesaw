package tech.tablesaw.aggregate;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.Column;

/**
 * A partial implementation of aggregate functions to summarize over a numeric column
 */
public abstract class AggregateFunction<INCOL extends Column<?>, OUT> {

    private String functionName;

    private String columnName;

    private String alias;

    public AggregateFunction(String functionName, String columnName) {
        this.functionName = functionName;
        this.columnName = columnName;
    }

    public String functionName() {
        return functionName;
    }

    public String aggColumn() {
        return columnName;
    }

    public AggregateFunction as(String alias) {
        this.alias = alias;
        return this;
    }

    public String getAlias() {
        return alias;
    }

    public abstract OUT summarize(INCOL column);

    public String toString() {
        return functionName();
    }

    public abstract boolean isCompatibleColumn(ColumnType type);

    public abstract ColumnType returnType();

    public abstract INCOL compatibleColumn(Column<?> column);
}
