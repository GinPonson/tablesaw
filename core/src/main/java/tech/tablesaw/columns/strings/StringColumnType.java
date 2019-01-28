package tech.tablesaw.columns.strings;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.AbstractParser;
import tech.tablesaw.io.csv.CsvReadOptions;

public class StringColumnType extends AbstractColumnType {

    public static final int BYTE_SIZE = 4;
    public static final StringParser DEFAULT_PARSER = new StringParser(ColumnType.STRING);

    private static StringColumnType INSTANCE;

    private StringColumnType(int byteSize, String name, String printerFriendlyName) {
        super(byteSize, name, printerFriendlyName);
    }

    public static StringColumnType instance() {
        if (INSTANCE == null) {
            INSTANCE = new StringColumnType(BYTE_SIZE,
                    "STRING",
                    "String");
        }
        return INSTANCE;
    }

    @Override
    public StringColumn create(String name) {
        return StringColumn.create(name);
    }

    @Override
    public StringParser customParser(CsvReadOptions options) {
        return new StringParser(this, options);
    }

    @Override
    public AbstractParser<?> defaultParser() {
        return new StringParser(this);
    }

    public static String missingValueIndicator() {
        return "";
    }

}
