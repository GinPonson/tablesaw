package tech.tablesaw.aggregate;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.util.NumberUtils;

/**
 * A partial implementation of aggregate functions to summarize over a numeric column
 */
public abstract class NumericAggregateFunction extends AggregateFunction<NumericColumn<?>, Double> {

    public NumericAggregateFunction(String name, String column) {
        super(name, column);
    }

    @Override
    public boolean isCompatibleColumn(ColumnType type) {
        return type.equals(ColumnType.DOUBLE)
                || type.equals(ColumnType.FLOAT)
                || type.equals(ColumnType.INTEGER)
                || type.equals(ColumnType.SHORT)
                || type.equals(ColumnType.LONG)
                || type.equals(ColumnType.STRING);
    }

    @Override
    public ColumnType returnType() {
        return ColumnType.DOUBLE;
    }

    @Override
    public NumericColumn<?> compatibleColumn(Column<?> column) {
        if (column.type().equals(ColumnType.STRING)) {
            DoubleColumn newColumn = DoubleColumn.create(column.name(), column.size());
            for (int i = 0; i < column.size(); i++) {
                newColumn.set(i, NumberUtils.toBigDecimal(column.get(i)).doubleValue());
            }
            return newColumn;
        }
        return (NumericColumn<?>) column;
    }
}
