package com.shenchu.app.utils;

/**
 * Created by admin on 2017/11/17.
 */

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static boolean isEmpty(String str) {
        if (null == str || str.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean isPhoneNum(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        String regex = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
        if (phone.matches(regex)) {
            return true;
        }
        return false;
    }

    public static boolean isEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        return email.matches(regex);
    }

    public static boolean isFloat(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        String regex = "^[1-9][\\d]*\\.[\\d]{2}$";
        return value.matches(regex);
    }

    // 字符转float
    public static float string2float(String str) {
        if (str == null || str.isEmpty() || str.equals(null)) {
            return 0;
        }
        float f = Float.parseFloat(str);
        return f;
    }

    // json转map
    public static Map<String, Object> json2map(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        Gson gson = new Gson();

        Map<String, Object> map = gson.fromJson(json,
                new TypeToken<Map<String, Object>>() {
                }.getType());
        return map;
    }

    // 保留两位小数
    public static String float00(float f) {
        if (f == 0) {
            return "0.00";
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.00");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
        return decimalFormat.format(f);// format 返回的是字符串
    }

    // 四舍五入并保留两位小数
    public static String float45(float f) {
        if (f == 0) {
            return "0.00";
        }
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
        return decimalFormat.format(f);// format 返回的是字符串
    }

    // 四舍五入并保留三位小数
    public static String float450(float f) {
        if (f == 0) {
            return "0.000";
        }
        DecimalFormat decimalFormat = new DecimalFormat("#0.000");// 构造方法的字符格式这里如果小数不足3位,会以0补足.
        return decimalFormat.format(f);// format 返回的是字符串
    }

    public static int getChineseNum(String str) {
        int count = 0;
        Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
        char c[] = str.toCharArray();
        for (int i = 0; i < c.length; i++) {
            Matcher matcher = pattern.matcher(String.valueOf(c[i]));
            if (matcher.matches()) {
                count++;
            }
        }
        return count;
    }

    /**
     * 使用 Map按key进行排序
     *
     * @param map
     * @return
     */
    public static Map<String, Object> sortMapByKey(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, Object> sortMap = new TreeMap<String, Object>(
                new MapKeyComparator());

        sortMap.putAll(map);

        return sortMap;
    }

    static class MapKeyComparator implements Comparator<String> {
        @Override
        public int compare(String str1, String str2) {

            return str1.compareTo(str2);
        }
    }
}