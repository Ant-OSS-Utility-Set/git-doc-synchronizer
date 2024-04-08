package com.alipay.sofa.doc.service;

import com.alipay.sofa.doc.model.MenuItem;
import com.alipay.sofa.doc.model.Repo;
import com.alipay.sofa.doc.utils.FileUtils;
import com.alipay.sofa.doc.utils.StringUtils;
import com.alipay.sofa.doc.model.Context;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 */
@Component
public class SummaryMdTOCParser implements TOCParser {

    public static final String TOC_MD = "SUMMARY.md";

    public MenuItem parse(Repo repo, Context context) {
        List<String> lines = null;
        String tocFile = repo.getTocFile();
        File summaryFile = new File(repo.getLocalDocPath(), StringUtils.isBlank(tocFile) ? TOC_MD : tocFile);
        try {
            lines = FileUtils.readLines(summaryFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return parseSummaryLines(lines);
    }

    protected MenuItem parseSummaryLines(List<String> lines) {
        MenuItem toc = new MenuItem();
        int ltag = -1;
        MenuItem lastParentMenuItem = toc;
        MenuItem lastMenuItem = null;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String data = line.trim();
            if (data.startsWith("*") || data.startsWith("-")) {
                lastMenuItem = line2MenuItem(i, line, ltag, lastParentMenuItem, lastMenuItem);
                ltag = lastMenuItem.getLtrim();
                lastParentMenuItem = lastMenuItem.getParentMenuItem();
            }
        }
        return toc;
    }

    /**
     * @param lineNum            行号，用于定位问题
     * @param line               * 或者 - 开头的一行内容，例如  <code>  * [Introduction](README.md)</code>
     * @param ltag               上次记录的空格数
     * @param lastParentMenuItem 如果是平级的，放到这里
     * @param lastMenuItem       如果是子级的，放到这个下面
     * @return MenuItem  目录节点
     */
    private MenuItem line2MenuItem(int lineNum, String line, int ltag, MenuItem lastParentMenuItem, MenuItem lastMenuItem) {
        String data = StringUtils.trimLeft(line);
        int ltrim = line.length() - data.length();
        MenuItem menuItem = mdLineToMenuItem(lineNum, line);
        menuItem.setLtrim(ltrim);
        if (ltrim % 2 != 0) {
            throw new IllegalArgumentException("[:" + lineNum + "] markdown 不标准，行首空格不是 2 的倍数，原始内容：[" + line + "]");
        }
        if (ltag == -1 || ltag == ltrim) { // 平行
            lastParentMenuItem.getSubMenuItems().add(menuItem);
            menuItem.setParentMenuItem(lastParentMenuItem);
        } else if (ltag > ltrim) { // 返回上一级或者上两级
            MenuItem tmp = lastMenuItem;
            for (int i = 0; i < (ltag - ltrim) / 2 + 1; i++) {
                tmp = tmp.getParentMenuItem();
            }
            if (tmp == null) {
                throw new IllegalArgumentException("[:" + lineNum + "] markdown 不标准，上下行行首空格超过指定格式，原始内容：[" + line + "]");
            }
            tmp.getSubMenuItems().add(menuItem);
            menuItem.setParentMenuItem(tmp);
        } else { // 子集
            if (ltrim - ltag > 2) {
                throw new IllegalArgumentException("[:" + lineNum + "] markdown 不标准，上下行行首空格超过指定格式，原始内容：[" + line + "]");
            }
            lastMenuItem.getSubMenuItems().add(menuItem);
            menuItem.setParentMenuItem(lastMenuItem);
        }
        return menuItem;
    }

    private MenuItem mdLineToMenuItem(int lineNum, String line) {
        String tmp = line.trim();
        int idx = tmp.indexOf("](");
        if (idx < 0) {
            throw new IllegalArgumentException("[:" + lineNum + "] 非标准链接格式，正确格式为 [xxx](xxx.md)，原始内容：[" + line + "]");
        }
        String p1 = tmp.substring(0, idx);
        String p2 = tmp.substring(idx + 2);

        MenuItem item = new MenuItem();
        idx = p1.indexOf("[");
        if (idx < 0) {
            throw new IllegalArgumentException("[:" + lineNum + "] 非标准链接格式，正确格式为 [xxx](xxx.md)，原始内容：[" + line + "]");
        }
        String title = p1.substring(idx + 1);
        item.setTitle(title);

        idx = p2.indexOf(")");
        if (idx < 0) {
            throw new IllegalArgumentException("[:" + lineNum + "] 非标准链接格式，正确格式为 [xxx](xxx.md)，原始内容：[" + line + "]");
        }
        String url = p2.substring(0, idx);
        item.setUrl(url);
        if (StringUtils.isBlank(url)) {
            item.setType(MenuItem.MenuItemType.TITLE);
        } else if (url.endsWith(".md") || url.endsWith(".markdown")) {
            item.setType(MenuItem.MenuItemType.DOC);
        } else {
            item.setType(MenuItem.MenuItemType.LINK);
        }
        return item;
    }
}
