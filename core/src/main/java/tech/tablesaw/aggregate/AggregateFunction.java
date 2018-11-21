package tech.tablesaw.aggregate;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.Column;

/**
 * A partial implementation of aggregate functions to summarize over a numeric column
 */
public abstract class AggregateFunction<INCOL extends Column<?>, OUT> {

    private final String functionName;

    private String column;

    public AggregateFunction(String functionName) {
        this.functionName = functionName;
    }

    public AggregateFunction(String functionName, String column) {
        this.functionName = functionName;
        this.column = column;
    }

    public String functionName() {
        return functionName;
    }

    public String aggColumn() {
        return column;
    }

    public abstract OUT summarize(INCOL column);

    public String toString() {
        return functionName();
    }

    public abstract boolean isCompatibleColumn(ColumnType type);

    public abstract ColumnType returnType();
}
