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

import org.apache.commons.math3.stat.StatUtils;
import org.junit.Before;
import org.junit.Test;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.table.SelectionTableSliceGroup;
import tech.tablesaw.table.StandardTableSliceGroup;
import tech.tablesaw.table.TableSliceGroup;

import static org.junit.Assert.*;
import static tech.tablesaw.aggregate.AggregateFunctions.*;

public class AggregateFunctionsTest {

    private Table table;

    @Before
    public void setUp() throws Exception {
        table = Table.read().csv(CsvReadOptions.builder("../data/bush.csv"));
    }

    @Test
    public void testGroupMean() {
        StringColumn byColumn = table.stringColumn("who");
        TableSliceGroup group = StandardTableSliceGroup.create(table, byColumn);
        Table result = group.aggregate(mean("approval"), stdDev("approval"));
        System.out.println(result);
        assertEquals(3, result.columnCount());
        assertEquals("who", result.column(0).name());
        assertEquals(6, result.rowCount());
        assertEquals("65.671875", result.getUnformatted(0, 1));
        assertEquals("10.648876067826901", result.getUnformatted(0, 2));
    }

    @Test
    public void testDateMin() {
        StringColumn byColumn = table.dateColumn("date").yearQuarter();
        Table result = table.summarize(mean("approval"), earliestDate("date")).by(byColumn);

        System.out.println(result);
        //Table result = table.summarize(sum("approval" ), mean("approval")).apply();
        assertEquals(3, result.columnCount());
        assertEquals(13, result.rowCount());
    }

    @Test
    public void testBooleanAggregateFunctions() {
        boolean[] values = {true, false};
        BooleanColumn bc = BooleanColumn.create("test", values);
        assertTrue(anyTrue("test").summarize(bc));
        assertFalse(noneTrue("test").summarize(bc));
        assertFalse(allTrue("test").summarize(bc));
    }

    @Test
    public void testGroupMean2() {
        Table result = table.summarize(mean("approval"), stdDev("approval")).apply();
        System.out.println(result);
        assertEquals(2, result.columnCount());
    }

    @Test
    public void testApplyWithNonNumericResults() {
        Table result = table.summarize( earliestDate("date"), latestDate("date")).apply();
        assertEquals(2, result.columnCount());
    }

    @Test
    public void testGroupMean3() {
        Summarizer function = table.summarize(mean("approval"), stdDev("approval"));
        Table result = function.by("Group", 10);
        assertEquals(32, result.rowCount());
    }

    @Test
    public void testGroupMean4() {
        table.addColumns(table.numberColumn("approval").cube());
        table.column(3).setName("cubed");
        Table result = table.summarize(mean("approval"), stdDev("cubed")).apply();
        assertEquals(4, result.columnCount());
    }

    @Test
    public void testGroupMeanByStep() {
        TableSliceGroup group = SelectionTableSliceGroup.create(table, "Step", 5);
        Table result = group.aggregate(mean("approval"), stdDev("approval"));
        assertEquals(3, result.columnCount());
        assertEquals("53.6", result.getUnformatted(0, 1));
        assertEquals("2.5099800796022267", result.getUnformatted(0, 2));
    }

    @Test
    public void test2ColumnGroupMean() {
        StringColumn byColumn1 = table.stringColumn("who");
        DateColumn byColumn2 = table.dateColumn("date");
        Table result = table.summarize(mean("approval"), sum("approval")).by(byColumn1, byColumn2);
        assertEquals(4, result.columnCount());
        assertEquals("who", result.column(0).name());
        assertEquals(323, result.rowCount());
        assertEquals("46.0", result.getUnformatted(0, 2));
    }

    @Test
    public void testComplexSummarizing() {
        table.addColumns(table.numberColumn("approval").cube());
        table.column(3).setName("cubed");
        StringColumn byColumn1 = table.stringColumn("who");
        StringColumn byColumn2 = table.dateColumn("date").yearMonth();
        Table result = table.summarize(mean("approval"), sum("cubed")).by(byColumn1, byColumn2);
        System.out.println(result);
        assertEquals(6, result.columnCount());
        assertEquals("who", result.column(0).name());
        assertEquals("date year & month", result.column(1).name());
    }

    @Test
    public void testMultipleColumnTypes() {

        boolean[] args = {true, false, true, false};
        BooleanColumn booleanColumn = BooleanColumn.create("b", args);

        double[] numbers = {1, 2, 3, 4};
        DoubleColumn numberColumn = DoubleColumn.create("n", numbers);

        String[] strings = {"M", "F", "M", "F"};
        StringColumn stringColumn = StringColumn.create("s", strings);

        Table table = Table.create("test", booleanColumn, numberColumn);
        table.summarize(countTrue("b"), standardDeviation("n")).by(stringColumn);
    }

    @Test
    public void testMultipleColumnTypesWithApply() {

        boolean[] args = {true, false, true, false};
        BooleanColumn booleanColumn = BooleanColumn.create("b", args);

        double[] numbers = {1, 2, 3, 4};
        DoubleColumn numberColumn = DoubleColumn.create("n", numbers);

        String[] strings = {"M", "F", "M", "F"};
        StringColumn stringColumn = StringColumn.create("s", strings);

        Table table = Table.create("test", booleanColumn, numberColumn, stringColumn);
        Table summarized = table.summarize(countTrue("b"), standardDeviation("n")).apply();
        assertEquals(1.2909944487358056, summarized.doubleColumn(1).get(0), 0.00001);
    }

    @Test
    public void testBooleanFunctions() {
        BooleanColumn c = BooleanColumn.create("test");
        c.append(true);
        c.appendCell("");
        c.append(false);
        assertEquals(1, countTrue("test").summarize(c), 0.0001);
        assertEquals(1, countFalse("test").summarize(c), 0.0001);
        assertEquals(0.5, proportionFalse("test").summarize(c), 0.0001);
        assertEquals(0.5, proportionTrue("test").summarize(c), 0.0001);
        assertEquals(1, countMissing("test").summarize(c), 0.0001);
        assertEquals(3, countWithMissing("test").summarize(c), 0.0001);
        assertEquals(2, countUnique("test").summarize(c), 0.0001);
    }


    @Test
    public void testPercentileFunctions() {
        double[] values = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        DoubleColumn c = DoubleColumn.create("test", values);
        c.appendCell("");

        assertEquals(1, countMissing("test").summarize(c), 0.0001);
        assertEquals(11, countWithMissing("test").summarize(c), 0.0001);

        assertEquals(StatUtils.percentile(values, 90), percentile90("test").summarize(c), 0.0001);
        assertEquals(StatUtils.percentile(values, 95), percentile95("test").summarize(c), 0.0001);
        assertEquals(StatUtils.percentile(values, 99), percentile99("test").summarize(c), 0.0001);

        assertEquals(10, countUnique("test").summarize(c), 0.0001);
    }
}