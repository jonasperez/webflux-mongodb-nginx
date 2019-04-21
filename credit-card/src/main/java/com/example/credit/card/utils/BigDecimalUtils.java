package com.example.credit.card.utils;

import java.math.BigDecimal;

public class BigDecimalUtils {

    public static BigDecimal build(double value){
        BigDecimal bigDecimal = new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        return bigDecimal;
    }
}
