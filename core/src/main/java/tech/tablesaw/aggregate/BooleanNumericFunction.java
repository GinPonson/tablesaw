package tech.tablesaw.aggregate;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.Column;

abstract class BooleanNumericFunction extends AggregateFunction<BooleanColumn, Double> {

    public BooleanNumericFunction(String functionName, String columnName) {
        super(functionName, columnName);
    }

    @Override
    abstract public Double summarize(BooleanColumn column);

    @Override
    public boolean isCompatibleColumn(ColumnType type) {
        return type.equals(ColumnType.BOOLEAN);
    }

    @Override
    public ColumnType returnType() {
        return ColumnType.DOUBLE;
    }

    @Override
    public BooleanColumn compatibleColumn(Column<?> column) {
        return (BooleanColumn) column;
    }
}
