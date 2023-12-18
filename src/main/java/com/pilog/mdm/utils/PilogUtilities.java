package com.pilog.mdm.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

public class PilogUtilities {
    public static int convertIntoInteger(Object value) {
        int count = 0;
        try {
            if (value != null)
                if (value instanceof BigInteger) {
                    BigInteger countObj = (BigInteger)value;
                    if (countObj != null)
                        count = countObj.intValue();
                } else if (value instanceof BigDecimal) {
                    BigDecimal countObj = (BigDecimal)value;
                    if (countObj != null)
                        count = countObj.intValue();
                } else {
                    count = ((Integer)value).intValue();
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public static String trimChar(String query, char character) {
        try {
            if (query != null && !"".equalsIgnoreCase(query)) {
                query = query.trim().replaceAll("\\s{2,}", " ");
                if (query.charAt(0) == character || query.charAt(query.length() - 1) == character) {
                    if (query.charAt(0) == character)
                        query = query.substring(1);
                    if (query.charAt(query.length() - 1) == character)
                        query = query.substring(0, query.length() - 1);
                    if (query.charAt(0) == character || query.charAt(query.length() - 1) == character)
                        query = trimChar(query, character);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return query;
    }

}
