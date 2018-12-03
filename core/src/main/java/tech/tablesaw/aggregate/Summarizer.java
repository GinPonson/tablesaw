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

package tech.tablesaw.aggregate;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.table.SelectionTableSliceGroup;
import tech.tablesaw.table.StandardTableSliceGroup;
import tech.tablesaw.table.TableSliceGroup;
import tech.tablesaw.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Summarizes the data in a table, by applying functions to a subset of its columns.
 *
 * How to use:
 *
 * 1. Create an instance providing a source table, the column or columns to summarize, and a function or functions to apply
 * 2. Applying the functions to the designated columns, possibly creating subgroup summaries using one of the by() methods
 */
public class Summarizer {

    private final Table original;
    private final Table temp;
    private final AggregateFunction<?, ?>[] reductions;

    /**
     * Returns an object capable of summarizing the given column in the given sourceTable,
     * by applying the given functions
     */
    public Summarizer(Table sourceTable, AggregateFunction<?, ?>... functions) {
        this.temp = Table.create(sourceTable.name());
        this.original = sourceTable;
        this.reductions = functions;

        // add columns to temp table
        List<String> columnNames = Arrays.stream(functions).map(AggregateFunction::aggColumn)
                .distinct().collect(Collectors.toList());
        columnNames.forEach(columnName -> temp.addColumns(sourceTable.column(columnName)));
    }

    public Table by(String... columnNames) {
        for (String columnName : columnNames) {
            temp.addColumnsIgnoreExists(original.column(columnName));
        }
        TableSliceGroup group = StandardTableSliceGroup.create(temp, columnNames);
        return summarize(group);
    }

    public Table by(CategoricalColumn<?>... columns) {
        for (Column<?> c : columns) {
            temp.addColumnsIgnoreExists(c);
        }
        TableSliceGroup group = StandardTableSliceGroup.create(temp, columns);
        return summarize(group);
    }

    public Table by(String groupNameTemplate, int step) {
        TableSliceGroup group = SelectionTableSliceGroup.create(temp, groupNameTemplate, step);
        return summarize(group);
    }

    /**
     * Returns the result of applying to the functions to all the values in the appropriate column
     * TODO add a test that uses a non numeric return type with apply
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Table apply() {
        List<Table> results = new ArrayList<>();
        ArrayListMultimap<String, AggregateFunction<?, ?>> reductionMultimap = getAggregateFunctionMultimap();

        for (String name : reductionMultimap.keys()) {
            List<AggregateFunction<?, ?>> reductions = reductionMultimap.get(name);
            Table table = TableSliceGroup.summaryTableName(temp);
            for (AggregateFunction function : reductions) {
                Column column = temp.column(name);
                Object result = function.summarize(column);

                String columnName = !Strings.isNullOrEmpty(function.getAlias()) ? function.getAlias()
                        : TableSliceGroup.aggregateColumnName(name, function.functionName());
                Column newColumn = function.returnType().create(columnName);

                /*if (result instanceof Number) {
                    Number number = (Number) result;
                    newColumn.append(number.doubleValue());
                } else {
                    newColumn.append(result);
                }*/
                newColumn.append(result);
                table.addColumns(newColumn);
            }
            results.add(table);
        }
        return combineTables(results);
    }

    /**
     * Associates the columns to be summarized with the functions that match their type. All valid combinations are used
     * @param group A table slice group
     * @return      A table containing a row of summarized data for each group in the table slice group
     */
    private Table summarize(TableSliceGroup group) {
        List<Table> results = new ArrayList<>();

        ArrayListMultimap<String, AggregateFunction<?, ?>> reductionMultimap = getAggregateFunctionMultimap();

        for (String name : reductionMultimap.keys()) {
            List<AggregateFunction<?, ?>> reductions = reductionMultimap.get(name);
            results.add(group.aggregate(name, reductions.toArray(new AggregateFunction<?, ?>[0])));
        }
        return combineTables(results);
    }

    private ArrayListMultimap<String, AggregateFunction<?, ?>> getAggregateFunctionMultimap() {
        ArrayListMultimap<String, AggregateFunction<?, ?>> reductionMultimap = ArrayListMultimap.create();

        for (AggregateFunction<?, ?> reduction : reductions) {
            ColumnType type = temp.column(reduction.aggColumn()).type();
            if (reduction.isCompatibleColumn(type)) {
                temp.replaceColumn(reduction.aggColumn(), reduction.compatibleColumn(temp.column(reduction.aggColumn())));
                reductionMultimap.put(reduction.aggColumn(), reduction);
            }
        }

        if (reductionMultimap.isEmpty()) {
            throw new IllegalArgumentException("None of the aggregate functions provided apply to the summarized column type(s).");
        }
        return reductionMultimap;
    }

    private Table combineTables(List<Table> tables) {
        if (tables.size() == 1) {
            return tables.get(0);
        }

        Table result = null;
        for (Table table : tables) {
            if (result == null) {
                result = table;
            } else {
                for (Column<?> column : table.columns()) {
                    result.addColumnsIgnoreExists(column);
                }
            }
        }
        return result;
    }
}
