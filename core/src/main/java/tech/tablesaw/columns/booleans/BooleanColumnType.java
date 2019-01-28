package tech.tablesaw.columns.booleans;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.AbstractParser;
import tech.tablesaw.io.csv.CsvReadOptions;

public class BooleanColumnType extends AbstractColumnType {

    public static final BooleanParser DEFAULT_PARSER = new BooleanParser(ColumnType.BOOLEAN);

    public static final byte MISSING_VALUE = (Byte) missingValueIndicator();

    public static final byte BYTE_TRUE = 1;
    public static final byte BYTE_FALSE = 0;

    private static final byte BYTE_SIZE = 1;

    private static BooleanColumnType INSTANCE;

    private BooleanColumnType(int byteSize, String name, String printerFriendlyName) {
        super(byteSize, name, printerFriendlyName);
    }

    public static BooleanColumnType instance() {
        if (INSTANCE == null) {
            INSTANCE = new BooleanColumnType(BYTE_SIZE, "BOOLEAN", "Boolean");
        }
        return INSTANCE;
    }


    @Override
    public BooleanColumn create(String name) {
        return BooleanColumn.create(name);
    }

    @Override
    public BooleanParser customParser(CsvReadOptions readOptions) {
        return new BooleanParser(this, readOptions);
    }

    @Override
    public AbstractParser<?> defaultParser() {
        return new BooleanParser(this);
    }

    public static byte missingValueIndicator() {
        return Byte.MIN_VALUE;
    }
}
