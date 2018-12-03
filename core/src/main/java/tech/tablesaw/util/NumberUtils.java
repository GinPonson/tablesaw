package tech.tablesaw.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * @author panyongjian
 * @since 2017/9/8.
 */
public class NumberUtils {

    public static final String PERCENTAGE = "%";
    public static final String DOLLAR = "$";
    public static final String YUAN = "￥";

    public static Number div(Object num1, Object num2) {
        if (num1 == null || num2 == null) {
            return null;
        }
        BigDecimal b1 = toBigDecimal(num1);
        BigDecimal b2 = toBigDecimal(num2);
        if (b2.doubleValue() == 0) {
            return 0;
        }
        return b1.divide(b2, 6, BigDecimal.ROUND_HALF_UP);
    }

    public static Number add(Object... nums) {
        BigDecimal total = new BigDecimal(0);
        for (Object number : nums) {
            if (number == null) {
                return 0;
            }
            BigDecimal b1 = toBigDecimal(number);
            total = total.add(b1);
        }
        return total;
    }

    public static Number sub(Object num1, Object num2) {
        if (num1 == null || num2 == null) {
            return null;
        }
        BigDecimal b1 = toBigDecimal(num1);
        BigDecimal b2 = toBigDecimal(num2);
        return b1.subtract(b2);
    }

    public static Number mul(Object num1, Object num2) {
        if (num1 == null || num2 == null) {
            return null;
        }
        BigDecimal b1 = toBigDecimal(num1);
        BigDecimal b2 = toBigDecimal(num2);
        return b1.multiply(b2);
    }

    public static boolean isNumber(Object num) {
        try {
            NumberFormat.getPercentInstance().parse(num.toString().trim());
            return true;
        } catch (ParseException e) {
            try {
                new BigDecimal(num.toString().trim());
                return true;
            } catch (NumberFormatException ex) {
                return false;
            }
        }
    }

    public static boolean isBigInteger(Object num) {
        try {
            new BigInteger(num.toString().trim());
            return !isShortInteger(num);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isShortInteger(Object num) {
        try {
            Long.valueOf(num.toString().trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPercentage(Object num) {
        try {
            NumberFormat.getPercentInstance().parse(num.toString().trim());
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static BigDecimal toBigDecimal(Object num) {
        try {
            Number v = NumberFormat.getPercentInstance().parse(num.toString().trim());
            return new BigDecimal(v.doubleValue());
        } catch (ParseException e) {
            return new BigDecimal(num.toString().trim());
        }
    }

    public static BigDecimal round(Number num, int scale) {
        return new BigDecimal(num.toString().trim()).setScale(scale, BigDecimal.ROUND_HALF_UP);
    }

    public static Object formatNumber(Object num, String format) {
        if (!isNumber(num)) {
            return num;
        }
        switch (format) {
            case PERCENTAGE:
                return round(mul(num, 100), 2).stripTrailingZeros().toPlainString() + PERCENTAGE;
            case DOLLAR:
                return DOLLAR + num.toString().trim();
            case YUAN:
                return YUAN + num.toString().trim();
            default:
                return num;
        }
    }
}
