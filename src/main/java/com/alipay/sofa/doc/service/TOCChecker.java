package com.alipay.sofa.doc.service;

import com.alipay.sofa.doc.model.Context;
import com.alipay.sofa.doc.model.MenuItem;
import com.alipay.sofa.doc.model.Repo;
import com.alipay.sofa.doc.plugins.yuque.YuqueSlugGenerator;
import com.alipay.sofa.doc.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ></a>
 */
@Component
public class TOCChecker {

    /**
     * 日志
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(TOCChecker.class);

    /**
     * 提前检查 Repo 和 Toc
     *
     * @param repo    语雀知识库
     * @param toc     文档
     * @param context 上下文
     */
    public void check(Repo repo, MenuItem toc, Context context) {
        List<MenuItem> subs = toc.getSubMenuItems();
        List<String> errors = new ArrayList<>();
        Set<String> slugs = new HashSet<>();
        for (MenuItem item : subs) {
            checkMenuItemWithChild(errors, repo, toc, context, item, slugs);
        }
        if (errors.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < errors.size(); i++) {
                sb.append(i + 1).append(": ").append(errors.get(i)).append("; \n");
            }
            sb.deleteCharAt(sb.length() - 1);
            LOGGER.error("Pre-check error({}): {}", errors.size(), sb);
            throw new RuntimeException("Pre-check error: " + sb);
        }
    }

    /**
     * 插入一个节点及其所有子节点
     *
     * @param repo     文档仓库
     * @param toc      目录
     * @param context  同步上下文
     * @param menuItem 要添加的目标节点
     * @param slugs    已存在的文章列表
     */
    protected void checkMenuItemWithChild(List<String> errors, Repo repo, MenuItem toc, Context context, MenuItem menuItem, Set<String> slugs) {
        Assert.notNull(repo, "repo is null");
        Assert.notNull(toc, "toc is null");
        Assert.notNull(repo.getNamespace(), "namespace is null");
        // 检查自己
        checkMenuItem(errors, repo, toc, context, menuItem, slugs);
        // 然后遍历子目录
        List<MenuItem> subs = menuItem.getSubMenuItems();
        if (!subs.isEmpty()) {
            for (MenuItem subMenuItem : subs) {
                checkMenuItemWithChild(errors, repo, toc, context, subMenuItem, slugs);
            }
        }
    }

    /**
     * 同步一个文章
     *
     * @param errors   错误列表
     * @param repo     代码库
     * @param toc      目录对象
     * @param context  上下文
     * @param menuItem 目录节点
     * @param slugs    之前的访问路径集合
     */
    protected void checkMenuItem(List<String> errors, Repo repo, MenuItem toc, Context context, MenuItem menuItem, Set<String> slugs) {
        Assert.notNull(repo, "repo is null");
        Assert.notNull(toc, "toc is null");

        // 检查标题
        String title = menuItem.getTitle();
        String url = menuItem.getUrl();
        if (StringUtils.isBlank(title)) {
            errors.add("[" + title + "](" + url + ") 标题不能为空，请检查");
            return;
        }

        // 非文档不需要检查 URL
        if (!MenuItem.MenuItemType.DOC.equals(menuItem.getType())) {
            return;
        }
        // 检查 URL 是否符合格式
        if (url.contains("../") || url.contains("./")) {
            errors.add("[" + title + "](" + url + ") URL 不能使用 ../ 或者 ./");
            return;
        }
        String slug = new YuqueSlugGenerator().url2Slug(url, context);
        if (slugs.contains(slug)) {
            errors.add("[" + title + "](" + url + ") URL 存在同访问路径，请检查是否存在多个同名文件");
            return;
        } else if (!isLegalSlug(slug)) {
            errors.add("[" + title + "](" + url + ") URL 生成的 slug [" + slug + "] 不合法（语雀访问路径至少 2 个字符，最长 190 字符，只能输入小写字母、数字、横线、下划线和点），请检查");
            return;
        } else {
            // 检查文件是否存在
            File file = new File(repo.getLocalDocPath(), url);
            if (!file.exists()) {
                errors.add("[" + title + "](" + url + ") URL 所指的文件 " + file.getAbsolutePath() + " 不存在，请检查文件名是否正确，或者文件夹名是否正确");
                return;
            }
            slugs.add(slug);
        }
        // TODO: 检查其它文件内容
    }

    /**
     * 访问路径为 2～190 个字符，只能输入小写字母、数字、横线、下划线和点
     * // 202301 语雀从 36 扩展到 190 字符
     */
    private static final String SLUG_REGEX = "^[a-z0-9\\._-]{2,190}$";

    /**
     * 访问路径至少 2 个字符，最长 190 字符，只能输入小写字母、数字、横线、下划线和点
     * modify by lhq
     * @param slug 访问路径
     * @return 是否合法
     */
    protected boolean isLegalSlug(String slug) {
        return true;
    }
}
