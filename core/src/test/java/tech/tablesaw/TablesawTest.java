package tech.tablesaw;

import org.junit.Test;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static tech.tablesaw.aggregate.AggregateFunctions.sum;

/**
 * @author panyongjian
 * @since 2018/11/16.
 */
public class TablesawTest {


    @Test
    public void testSummarize() {
        String[] animals = {"cat", "bear", "giraffe", "cat"};
        double[] cuteness = {90.1, 84.3, 99.7, 2};

        Table cuteAnimals = Table.create("Cute Animals")
                .addColumns(
                        StringColumn.create("types", animals),
                        DoubleColumn.create("rating", cuteness));
        System.out.println(cuteAnimals.summarize(sum("rating").as("rating")).by("types"));
    }

    @Test
    public void testDate() {
        DateTimeColumn column = DateTimeColumn.create("a");
        column.appendCell("2014-07-09 13:03:44.7");
        DateTimeFormatter dtTimef1 = new DateTimeFormatterBuilder().parseLenient().appendPattern("yyyy-MM-dd HH:mm:ss.S").toFormatter();
        LocalDateTime.parse("2016-12-25 12:50:33.000", dtTimef1);
    }

}
