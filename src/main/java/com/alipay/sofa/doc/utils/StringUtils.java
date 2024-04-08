/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.doc.utils;

import java.io.UnsupportedEncodingException;


/**
 * 字符串操作工具类
 *
 * ></a>
 */
public class StringUtils {

    /**
     * The empty String {@code ""}.
     *
     * @since 5.0.0
     */
    public static final String   EMPTY              = "";

    /**
     * The context path separator String {@code "/"}.
     */
    public static final String   CONTEXT_SEP        = "/";

    /**
     * The string {@code "*"}.
     *
     * @since 5.3.1
     */
    public static final String   ALL                = "*";

    /**
     * The string {@code "default"}.
     *
     * @since 5.3.1
     */
    public static final String   DEFAULT            = "default";

    /**
     * The string {@code "true"}.
     *
     * @since 5.4.0
     */
    public static final String   TRUE               = "true";

    /**
     * The string {@code "false"}.
     *
     * @since 5.4.0
     */
    public static final String   FALSE              = "false";

    /**
     * The string {@code "null"}.
     *
     * @since 5.4.0
     */
    public static final String   NULL               = "null";

    /**
     * 空数组
     */
    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    // Empty checks
    //-----------------------------------------------------------------------

    /**
     * <p>Checks if a CharSequence is empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * <p>NOTE: This method changed in Lang version 2.0.
     * It no longer trims the CharSequence.
     * That functionality is available in isBlank().</p>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is empty or null
     * @since 3.0 Changed signature from isEmpty(String) to isEmpty(CharSequence)
     */
    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * <p>Checks if a CharSequence is not empty ("") and not null.</p>
     *
     * <pre>
     * StringUtils.isNotEmpty(null)      = false
     * StringUtils.isNotEmpty("")        = false
     * StringUtils.isNotEmpty(" ")       = true
     * StringUtils.isNotEmpty("bob")     = true
     * StringUtils.isNotEmpty("  bob  ") = true
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is not empty and not null
     * @since 3.0 Changed signature from isNotEmpty(String) to isNotEmpty(CharSequence)
     */
    public static boolean isNotEmpty(CharSequence cs) {
        return !StringUtils.isEmpty(cs);
    }

    /**
     * <p>Checks if a CharSequence is whitespace, empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace
     * @since 3.0 Changed signature from isBlank(String) to isBlank(CharSequence)
     */
    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if a CharSequence is not empty (""), not null and not whitespace only.</p>
     *
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is
     * not empty and not null and not whitespace
     * @since 3.0 Changed signature from isNotBlank(String) to isNotBlank(CharSequence)
     */
    public static boolean isNotBlank(CharSequence cs) {
        return !StringUtils.isBlank(cs);
    }

    /**
     * <pre>
     * StringUtils.trim(null)          = null
     * StringUtils.trim("")            = ""
     * StringUtils.trim("     ")       = ""
     * StringUtils.trim("abc")         = "abc"
     * StringUtils.trim("    abc    ") = "abc"
     * </pre>
     *
     * @param str the String to be trimmed, may be null
     * @return the trimmed string, {@code null} if null String input
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    public static String trimLeft(String str) {
        if (str == null || str.equals("")) {
            return str;
        } else {
            return str.replaceAll("^[　 ]+", "");
        }
    }


    /**
     * <pre>
     * StringUtils.trimToEmpty(null)          = ""
     * StringUtils.trimToEmpty("")            = ""
     * StringUtils.trimToEmpty("     ")       = ""
     * StringUtils.trimToEmpty("abc")         = "abc"
     * StringUtils.trimToEmpty("    abc    ") = "abc"
     * </pre>
     *
     * @param str the String to be trimmed, may be null
     * @return the trimmed String, or an empty String if {@code null} input
     * @since 2.0
     */
    public static String trimToEmpty(String str) {
        return str == null ? EMPTY : str.trim();
    }

    /**
     * Converts a <code>byte[]</code> to a String using the specified character encoding.
     *
     * @param bytes       the byte array to read from
     * @param charsetName the encoding to use, if null then use the platform default
     * @return a new String
     * @throws UnsupportedEncodingException If the named charset is not supported
     * @throws NullPointerException         if the input is null
     * @since 3.1
     */
    public static String toString(byte[] bytes, String charsetName) throws UnsupportedEncodingException {
        return charsetName == null ? new String(bytes) : new String(bytes, charsetName);
    }

    // Defaults
    //-----------------------------------------------------------------------


    /**
     * 对象转string
     *
     * @param o          对象
     * @param defaultVal 默认值
     * @return 不为null执行toString方法
     */
    public static String toString(Object o, String defaultVal) {
        return o == null ? defaultVal : o.toString();
    }

    /**
     * 对象转string
     *
     * @param o 对象
     * @return 不为null执行toString方法
     */
    public static String toString(Object o) {
        return toString(o, null);
    }


    /**
     * 字符串是否相同
     *
     * @param s1 字符串1
     * @param s2 字符串2
     * @return 是否相同
     */
    public static boolean equals(CharSequence s1, CharSequence s2) {
        return s1 == null ? s2 == null : s1.equals(s2);
    }

    /**
     * 按分隔符分隔的数组，包含空值<br>
     * 例如 "1,2,,3," 返回 [1,2,,3,] 5个值
     *
     * @param src       原始值
     * @param separator 分隔符
     * @return 字符串数组
     */
    public static String[] split(String src, String separator) {
        if (isEmpty(separator)) {
            return new String[] { src };
        }
        if (isEmpty(src)) {
            return StringUtils.EMPTY_STRING_ARRAY;
        }
        return src.split(separator, -1);
    }


    /**
     * 连接字符串数组
     *
     * @param strings   字符串数组
     * @param separator 分隔符
     * @return 按分隔符分隔的字符串
     */
    public static String join(String[] strings, String separator) {
        if (strings == null || strings.length == 0) {
            return EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        for (String string : strings) {
            if (isNotBlank(string)) {
                sb.append(string).append(separator);
            }
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - separator.length()) : StringUtils.EMPTY;
    }


}
