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

import java.io.*;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.List;


/**
 * 文件操作工具类<br>
 *
 * ></a>
 */
public class FileUtils {


    /**
     * 读取类相对路径内容
     *
     * @param file 文件
     * @return 文件内容（按行）
     * @throws IOException 读取IO异常
     */
    public static List<String> readLines(File file) throws IOException {
        List<String> lines = new ArrayList<String>();
        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(isr)) {
            String lineText = null;
            while ((lineText = bufferedReader.readLine()) != null) {
                lines.add(lineText);
            }
            return lines;
        }
    }


    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 文件夹
     * @return 是否删除完成
     */
    public static boolean cleanDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String aChildren : children) {
                    boolean success = cleanDirectory(new File(dir, aChildren));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }

    /**
     * 清楚路径前后的分隔符
     *
     * @param path
     * @return
     */
    public static String trimPath(String path) {
        return trimPath(path, true, true);
    }

    private static String trimPath(String path, boolean left, boolean right) {
        char[] val = path.toCharArray();
        char sbeTrim = '/';
        int idx = 0;
        int len = path.length();
        if (left) {
            while ((idx < len) && (val[idx] == sbeTrim)) {
                idx++;
            }
        }
        if (right) {
            while ((idx < len) && (val[len - 1] == sbeTrim)) {
                len--;
            }
        }
        return ((idx > 0) || (len < path.length())) ? path.substring(idx, len) : path;
    }

    public static String contactPath(String... paths) {
        if (paths.length == 1) {
            return paths[0];
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(trimPath(paths[0], false, true)).append("/");
            for (int i = 1; i < paths.length; i++) {
                sb.append(trimPath(paths[i])).append("/");
            }
            return sb.substring(0, sb.length() - 1);
        }
    }


}