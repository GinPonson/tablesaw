package tech.tablesaw.aggregate;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.DoubleColumnType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AggregateFunctions {

    public static DateAggregateFunction earliestDate(String column) {
        return new DateAggregateFunction("Earliest Date", column) {

            @Override
            public LocalDate summarize(DateColumn column) {
                return column.min();
            }
        };
    }

    public static DateAggregateFunction latestDate(String column) {
        return new DateAggregateFunction("Latest Date", column) {

            @Override
            public LocalDate summarize(DateColumn column) {
                return column.max();
            }
        };
    }

    public static DateTimeAggregateFunction earliestDateTime(String column) {
        return new DateTimeAggregateFunction("Earliest Date-Time", column) {

            @Override
            public LocalDateTime summarize(DateTimeColumn column) {
                return column.min();
            }
        };
    }

    public static DateTimeAggregateFunction latestDateTime(String column) {
        return new DateTimeAggregateFunction("Latest Date-Time", column) {

            @Override
            public LocalDateTime summarize(DateTimeColumn column) {
                return column.max();
            }
        };
    }

    public static BooleanCountFunction countTrue(String column) {
        return new BooleanCountFunction("Number True", column) {

            @Override
            public Integer summarize(BooleanColumn column) {
                return column.countTrue();
            }
        };
    }

    public static BooleanCountFunction countFalse(String column) {
        return new BooleanCountFunction("Number False", column) {

            @Override
            public Integer summarize(BooleanColumn column) {
                return (column).countFalse();
            }
        };
    }

    public static BooleanAggregateFunction allTrue(String column) {
        return new BooleanAggregateFunction("All True", column) {

            @Override
            public Boolean summarize(BooleanColumn column) {
                return column.all();
            }
        };
    }

    public static BooleanAggregateFunction anyTrue(String column) {
        return new BooleanAggregateFunction("Any True", column) {

            @Override
            public Boolean summarize(BooleanColumn column) {
                return column.any();
            }
        };
    }

    public static BooleanAggregateFunction noneTrue(String column) {
        return new BooleanAggregateFunction("None True", column) {

            @Override
            public Boolean summarize(BooleanColumn column) {
                return column.none();
            }
        };
    }

    public static BooleanNumericFunction proportionTrue(String column) {
        return new BooleanNumericFunction("Proportion True", column) {
            @Override
            public Double summarize(BooleanColumn column) {
                return (column).proportionTrue();
            }
        };
    }

    public static BooleanNumericFunction proportionFalse(String column) {
        return new BooleanNumericFunction("Proportion False", column) {
            @Override
            public Double summarize(BooleanColumn column) {
                return (column).proportionFalse();
            }
        };
    }

    /**
     * A function that returns the first item
     */
    public static NumericAggregateFunction first(String column) {
        return new NumericAggregateFunction("First", column) {

            @Override
            public Double summarize(NumericColumn<?> column) {
                return column.isEmpty() ? DoubleColumnType.missingValueIndicator() : column.getDouble(0);
            }
        };
    }

    /**
     * A function that returns the last item
     */
    public static NumericAggregateFunction last(String column) {
        return new NumericAggregateFunction("Last", column) {

            @Override
            public Double summarize(NumericColumn<?> column) {
                return column.isEmpty() ? DoubleColumnType.missingValueIndicator() : column.getDouble(column.size() - 1);
            }
        };
    }

    /**
     * A function that returns the difference between the last and first items
     */
    public static NumericAggregateFunction change(String column) {
        return new NumericAggregateFunction("Change", column) {

            @Override
            public Double summarize(NumericColumn<?> column) {
                return column.size() < 2 ? DoubleColumnType.missingValueIndicator() : column.getDouble(column.size() - 1) - column.getDouble(0);
            }
        };
    }

    /**
     * A function that returns the difference between the last and first items
     */
    public static NumericAggregateFunction pctChange(String column) {
        return new NumericAggregateFunction("Percent Change", column) {

            @Override
            public Double summarize(NumericColumn<?> column) {
                return column.size() < 2 ? DoubleColumnType.missingValueIndicator() : (column.getDouble(column.size() - 1) - column.getDouble(0)) / column.getDouble(0);
            }
        };
    }

    /**
     * A function that calculates the count of values in the column excluding missing values
     */
    public static CountFunction countNonMissing(String column) {
        return new CountFunction("CountNonMissing", column) {

            @Override
            public Integer summarize(Column<?> column) {
                return column.size() - column.countMissing();
            }
        };
    }

    /**
     * A function that calculates the count of values in the column excluding missing values. A synonym for countNonMissing
     */
    public static CountFunction count(String column) {
        return countNonMissing(column);
    }

    /**
     * A function that calculates the count of values in the column excluding missing values
     */
    public static CountFunction countMissing(String column) {
        return new CountFunction("Missing Values", column) {

            @Override
            public Integer summarize(Column<?> column) {
                return column.countMissing();
            }
        };
    }

    /**
     * A function that returns the number of non-missing unique values in the column param
     */
    public static CountFunction countUnique(String column) {
        return new CountFunction("Count Unique", column) {

            @Override
            public Integer summarize(Column<?> doubles) {
                return doubles.unique().removeMissing().size();
            }
        };
    }

    /**
     * A function that calculates the mean of the values in the column param
     */
    public static NumericAggregateFunction mean(String column) {
        return new NumericAggregateFunction("Mean", column) {

            @Override
            public Double summarize(NumericColumn<?> column) {
                return StatUtils.mean(removeMissing(column));
            }
        };
    }

    /**
     * A function that calculates the sum of the values in the column param
     */
    public static NumericAggregateFunction sum(String column) {
        return new NumericAggregateFunction("Sum", column) {

            @Override
            public Double summarize(NumericColumn<?> column) {
                return StatUtils.sum(removeMissing(column));
            }
        };
    }

    public static NumericAggregateFunction median(String column) {
        return new NumericAggregateFunction("Median", column) {

            @Override
            public Double summarize(NumericColumn<?> column) {
                return percentile(column, 50.0);
            }
        };
    }

    public static CountFunction countWithMissing(String column) {
        return new CountFunction("Count (incl. missing)", column) {

            @Override
            public Integer summarize(Column<?> column) {
                return column.size();
            }
        };
    }

    public static NumericAggregateFunction quartile1(String column) {
        return new NumericAggregateFunction("First Quartile", column) {

            @Override
            public Double summarize(NumericColumn<?> column) {
                return percentile(column, 25.0);
            }
        };
    }

    public static NumericAggregateFunction quartile3(String column) {
        return new NumericAggregateFunction("Third Quartile", column) {

            @Override
            public Double summarize(NumericColumn<?> column) {
                return percentile(column, 75.0);
            }
        };
    }

    public static NumericAggregateFunction percentile90(String column) {
        return new NumericAggregateFunction("90th Percentile", column) {

            @Override
            public Double summarize(NumericColumn<?> column) {
                return percentile(column, 90.0);
            }
        };
    }

    public static NumericAggregateFunction percentile95(String column) {
        return new NumericAggregateFunction("95th Percentile", column) {

            @Override
            public Double summarize(NumericColumn<?> column) {
                return percentile(column, 95.0);
            }
        };
    }

    public static NumericAggregateFunction percentile99(String column) {
        return new NumericAggregateFunction("99th Percentile", column) {

            @Override
            public Double summarize(NumericColumn<?> column) {
                return percentile(column, 99.0);
            }
        };
    }

    public static NumericAggregateFunction range(String column) {
        return new NumericAggregateFunction("Range", column) {

            @Override
            public Double summarize(NumericColumn<?> column) {
                double[] data = removeMissing(column);
                return StatUtils.max(data) - StatUtils.min(data);
            }
        };
    }

    public static NumericAggregateFunction min(String column) {
        return new NumericAggregateFunction("Min", column) {

            @Override
            public Double summarize(NumericColumn<?> column) {
                return StatUtils.min(removeMissing(column));
            }
        };
    }

    public static NumericAggregateFunction max(String column) {
        return new NumericAggregateFunction("Max", column) {

            @Override
            public Double summarize(NumericColumn<?> column) {
                return StatUtils.max(removeMissing(column));
            }
        };
    }

    public static NumericAggregateFunction product(String column) {
        return new NumericAggregateFunction("Product", column) {

            @Override
            public Double summarize(NumericColumn<?> column) {
                return StatUtils.product(removeMissing(column));
            }
        };
    }

    public static NumericAggregateFunction geometricMean(String column) {
        return new NumericAggregateFunction("Geometric Mean", column) {

            @Override
            public Double summarize(NumericColumn<?> column) {
                return StatUtils.geometricMean(removeMissing(column));
            }
        };
    }

    public static NumericAggregateFunction populationVariance(String column) {
        return new NumericAggregateFunction("Population Variance", column) {

            @Override
            public Double summarize(NumericColumn<?> column) {
                return StatUtils.populationVariance(removeMissing(column));
            }
        };
    }

    /**
     * Returns the quadratic mean, aka, the root-mean-square
     */
    public static NumericAggregateFunction quadraticMean(String column) {
        return new NumericAggregateFunction("Quadratic Mean", column) {

            @Override
            public Double summarize(NumericColumn<?> column) {
                return new DescriptiveStatistics(removeMissing(column)).getQuadraticMean();
            }
        };
    }

    public static NumericAggregateFunction kurtosis(String column) {
        return new NumericAggregateFunction("Kurtosis", column) {

            @Override
            public Double summarize(NumericColumn<?> column) {
                double[] data = removeMissing(column);
                return new Kurtosis().evaluate(data, 0, data.length);
            }
        };
    }

    public static NumericAggregateFunction skewness(String column) {
        return new NumericAggregateFunction("Skewness", column) {

            @Override
            public Double summarize(NumericColumn<?> column) {
                double[] data = removeMissing(column);
                return new Skewness().evaluate(data, 0, data.length);
            }
        };
    }

    public static NumericAggregateFunction sumOfSquares(String column) {
        return new NumericAggregateFunction("Sum of Squares", column) {

            @Override
            public Double summarize(NumericColumn<?> column) {
                return StatUtils.sumSq(removeMissing(column));
            }
        };
    }

    public static NumericAggregateFunction sumOfLogs(String column) {
        return new NumericAggregateFunction("Sum of Logs", column) {

            @Override
            public Double summarize(NumericColumn<?> column) {
                return StatUtils.sumLog(removeMissing(column));
            }
        };
    }

    public static NumericAggregateFunction variance(String column) {
        return new NumericAggregateFunction("Variance", column) {

            @Override
            public Double summarize(NumericColumn<?> column) {
                double[] values = removeMissing(column);
                return StatUtils.variance(values);
            }
        };
    }

    public static NumericAggregateFunction stdDev(String column) {
        return new NumericAggregateFunction("Std. Deviation", column) {

            @Override
            public Double summarize(NumericColumn<?> column) {
                return Math.sqrt(StatUtils.variance(removeMissing(column)));
            }
        };
    }

    public static NumericAggregateFunction standardDeviation(String column) {
        return stdDev(column);
    }

    public static Double percentile(NumericColumn<?> data, Double percentile) {
        return StatUtils.percentile(removeMissing(data), percentile);
    }

    private static double[] removeMissing(NumericColumn<?> column) {
        NumericColumn<?> numericColumn = (NumericColumn<?>) column.removeMissing();
        return numericColumn.asDoubleArray();
    }

    // TODO(lwhite): These are two column reductions. We need a class for that
    public static Double meanDifference(NumericColumn<?> column1, NumericColumn<?> column2) {
        return StatUtils.meanDifference(column1.asDoubleArray(), column2.asDoubleArray());
    }

    public static Double sumDifference(NumericColumn<?> column1, NumericColumn<?> column2) {
        return StatUtils.sumDifference(column1.asDoubleArray(), column2.asDoubleArray());
    }
}
