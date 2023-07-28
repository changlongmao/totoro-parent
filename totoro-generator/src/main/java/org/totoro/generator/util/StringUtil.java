package org.totoro.generator.util;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理工具类
 * @author changlf 2023-05-16
 */
public final class StringUtil {

    /**
     * 手机号正则表达式
     */
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[3-9][0-9]{9}$");

    private static final Pattern CHINESE_ID = Pattern.compile("^\\d{17}[\\d|X|x]$");

    private static final Pattern NAME_PATTERN = Pattern.compile("[\\u4E00-\\u9FA5|·]{2,15}");

    private static final Pattern SQL_PATTERN = Pattern.compile("(select|update|delete|insert|drop|alter|order|desc" +
            "|truncate|substr|ascii|execute|table|declare|database|sitename|xp_cmdshell|grant|column" +
            "column_name|information_schema|table_schema)");

    /**
     * 年龄1-999的正则表达式
     */
    private static final Pattern AGE_PATTERN = Pattern.compile("^[1-9][0-9]{0,2}$");

    public static boolean isNotMobile(String mobile) {
        return !StringUtil.isNotEmpty(mobile) || !MOBILE_PATTERN.matcher(mobile).matches();
    }

    /**
     * 判断是否为空
     *
     * @param str 字符串
     * @return true表示为空，false表示不为空
     */
    public static boolean isEmpty(String str) {
        return null == str || "".equals(str.trim());
    }

    /**
     * 判断是否不为空值
     *
     * @param str 字符串
     * @return true表示不为空值，false表示为空值
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isNullOrEmpty(Object obj) {
        if (obj == null) {
            return true;
        }

        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).toString().trim().length() == 0;
        }

        if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        }

        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).isEmpty();
        }

        if (obj instanceof Number) {
            return "0".equals( obj.toString());
        }

        if (obj instanceof Object[]) {
            Object[] object = (Object[]) obj;
            if (object.length == 0) {
                return true;
            }
            boolean empty = true;
            for (Object anObject : object) {
                if (!isNullOrEmpty(anObject)) {
                    empty = false;
                    break;
                }
            }
            return empty;
        }
        return false;
    }

    /**
     * 判断对象属性是否全部为空，是返回true
     * @param object
     * @return
     */
    public static boolean checkObjAllFieldsIsNull(Object object) {
        if (null == object) {
            return true;
        }
        try {
            for (Field f : object.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                boolean nullOrEmpty = isNullOrEmpty(f.get(object));
                if (!nullOrEmpty) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static boolean isName(String name) {
        return isNotEmpty(name) && NAME_PATTERN.matcher(name).matches();
    }

    public static boolean isAge(String age) {
        return StringUtil.isNotEmpty(age) && AGE_PATTERN.matcher(age).matches();
    }

    /**
     * 脱敏身份证号
     * @param idCard 身份证号
     */
    public static String idCardHidden(String idCard) {
        if (idCard != null && idCard.length() == 18) {
            return idCard.substring(0, 6) + "********" + idCard.substring(14);
        }
        return idCard;
    }

    /**
     * 身份证验证
     *
     * @param certNo 号码内容
     * @return 是否有效 null和"" 都是false
     */
    public static boolean isIDCard(String certNo) {
        if (certNo == null || certNo.length() != 18 || !CHINESE_ID.matcher(certNo).matches()){
            return false;
        }

        final int birthDay = Integer.parseInt(certNo.substring(6, 14));

        //校验年份
        final int iyear = birthDay / 10000;
        if (iyear <= 1900 || iyear >= LocalDate.now().getYear()){
            return false;
        }

        //校验天数
        final int iday = birthDay % 100;
        if (iday <= 0 || iday >= 32){
            return false;
        }

        //校验月份
        final int imonth = birthDay % 10000 / 100;
        if (imonth <= 0 || imonth >= 13) {
            return false;
        }

        //校验位数
        int power = 0;
        final char[] cs = certNo.toUpperCase().toCharArray();
        int[] powerArray = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        int[] parityBit = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        int indexRemark = cs.length -1;
        for (int i = 0; i < indexRemark; i++) {
            power += (cs[i] - '0') * powerArray[i];
        }

        return cs[indexRemark] == parityBit[power % 11];
    }

    /**
     * 校验字符串
     * @param str 可能包含sql的参数
     * @author ChangLF 2023/7/7 15:57
     * @return boolean true 代表包含sql关键字
     **/
    public static boolean sqlValidate(String str) {
        if (isEmpty(str)) {
            return false;
        }
        Matcher matcher = SQL_PATTERN.matcher(str.toLowerCase());
        // 使用正则表达式进行匹配
        return matcher.find();
    }

    /**
     * 校验银行卡卡号(比较算出的校验位和卡号里的校验位)
     * @param cardId
     */
    public static boolean isBankCard(String cardId) {
        return cardId != null && cardId.trim().length() > 0
                && cardId.matches("\\d+");
    }

    /**
     * 判断uri是否在排除的url中
     * @param excludedUris 需要排除的uri
	 * @param uri 原始uri
     * @author ChangLF 2023/6/6 15:40
     * @return boolean
     **/
    public static boolean isExcludedUri(String excludedUris, String uri) {
        if (isEmpty(excludedUris)) {
            return false;
        }
        for (String excludedUri : excludedUris.split(",")) {
            int index = excludedUri.indexOf("*");
            if ((index != -1 && uri.startsWith(excludedUri.substring(0, index))) || uri.equals(excludedUri)) {
                return true;
            }
        }
        return false;
    }

}