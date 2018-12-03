package tech.tablesaw.aggregate;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.util.NumberUtils;

/**
 * A partial implementation of aggregate functions to summarize over a boolean column
 */
public abstract class BooleanAggregateFunction extends AggregateFunction<BooleanColumn, Boolean> {

    public BooleanAggregateFunction(String functionName, String columnName) {
        super(functionName, columnName);
    }

    abstract public Boolean summarize(BooleanColumn column);

    @Override
    public boolean isCompatibleColumn(ColumnType type) {
        return type == ColumnType.BOOLEAN;
    }

    @Override
    public ColumnType returnType() {
        return ColumnType.BOOLEAN;
    }

    @Override
    public BooleanColumn compatibleColumn(Column<?> column) {
        return (BooleanColumn) column;
    }
}
