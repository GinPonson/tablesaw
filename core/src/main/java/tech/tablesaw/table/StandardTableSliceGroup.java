/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.table;

import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

/**
 * A group of tables formed by performing splitting operations on an original table
 */
public class StandardTableSliceGroup extends TableSliceGroup {

    private StandardTableSliceGroup(Table original, CategoricalColumn<?>... columns) {
        super(original, splitColumnNames(columns));
        setSourceTable(getSourceTable());
        splitOn(getSplitColumnNames());
    }

    private static String[] splitColumnNames(CategoricalColumn<?>... columns) {
        String[] splitColumnNames = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            splitColumnNames[i] = columns[i].name();
        }
        return splitColumnNames;
    }

    /**
     * Returns a viewGroup splitting the original table on the given columns.
     * The named columns must be CategoricalColumns
     */
    public static StandardTableSliceGroup create(Table original, String... columnsNames) {
        List<CategoricalColumn<?>> columns = original.categoricalColumns(columnsNames);
        return new StandardTableSliceGroup(original, columns.toArray(new CategoricalColumn<?>[0]));
    }

    /**
     * Returns a viewGroup splitting the original table on the given columns.
     * The named columns must be CategoricalColumns
     */
    public static StandardTableSliceGroup create(Table original, CategoricalColumn<?>... columns) {
        return new StandardTableSliceGroup(original, columns);
    }

    /**
     * Splits the sourceTable table into sub-tables, grouping on the columns whose names are given in
     * splitColumnNames
     */
    private void splitOn(String... columnNames) {

        List<Column<?>> columns = getSourceTable().columns(columnNames);
        int byteSize = getByteSize(columns);

        for (int row = 0; row < getSourceTable().rowCount(); row++) {

            ByteBuffer byteBuffer = ByteBuffer.allocate(byteSize);
            StringBuilder newStringKey = new StringBuilder();

            for (int col = 0; col < columnNames.length; col++) {
                if (col > 0) {
                    newStringKey.append(SPLIT_STRING);
                }

                Column<?> c = getSourceTable().column(columnNames[col]);
                String groupKey = getSourceTable().getUnformatted(row, getSourceTable().columnIndex(c));
                newStringKey.append(groupKey);
                byteBuffer.put(c.asBytes(row));
            }

            String currentStringKey = newStringKey.toString();

            final String tableName = currentStringKey;
            Optional<TableSlice> optional = getSlices().stream()
                    .filter(slice -> slice.name().equals(tableName))
                    .findFirst();
            if (optional.isPresent()) {
                optional.get().addRow(row);
            } else {
                TableSlice view = new TableSlice(getSourceTable());
                view.setName(currentStringKey);
                view.addRow(row);
                addSlice(view);
            }
        }
    }
}
