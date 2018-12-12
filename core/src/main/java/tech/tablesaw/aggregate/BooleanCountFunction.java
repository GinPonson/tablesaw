package tech.tablesaw.aggregate;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.Column;

public abstract class BooleanCountFunction extends AggregateFunction<BooleanColumn, Integer> {

    public BooleanCountFunction(String functionName, String columnName) {
        super(functionName, columnName);
    }

    @Override
    abstract public Integer summarize(BooleanColumn column);

    @Override
    public boolean isCompatibleColumn(ColumnType type) {
        return type.equals(ColumnType.BOOLEAN);
    }

    @Override
    public ColumnType returnType() {
        return ColumnType.INTEGER;
    }

    @Override
    public BooleanColumn compatibleColumn(Column<?> column) {
        return (BooleanColumn) column;
    }
}
