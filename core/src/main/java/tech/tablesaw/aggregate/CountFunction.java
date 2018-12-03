package tech.tablesaw.aggregate;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.Column;

abstract class CountFunction extends AggregateFunction<Column<?>, Integer> {

    public CountFunction(String functionName, String columnName) {
        super(functionName, columnName);
    }

    @Override
    abstract public Integer summarize(Column<?> column);

    @Override
    public boolean isCompatibleColumn(ColumnType type) {
        return true;
    }

    @Override
    public ColumnType returnType() {
        return ColumnType.DOUBLE;
    }

    @Override
    public Column compatibleColumn(Column<?> column) {
        return column;
    }
}
