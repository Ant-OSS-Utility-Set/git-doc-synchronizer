package com.alipay.sofa.doc.plugins.yuque;

import com.alipay.sofa.doc.utils.StringUtils;
import com.alipay.sofa.doc.model.Context;

import java.util.Locale;

/**
 * ></a>
 */
public class YuqueSlugGenerator {

    /**
     *
     * @param url 原生地址
     * @param context 上下文
     * @return 语雀 slug
     */
    public String url2Slug(String url, Context context) {
        Context.SlugGenMode mode = context.getSlugGenMode();
        String slug = url2Slug(url, mode);
        String prefix = context.getSlugPrefix();
        if(StringUtils.isNotBlank(prefix)){
            slug = prefix.trim().toLowerCase(Locale.ROOT) + "-" + slug;
        }
        String suffix = context.getSlugSuffix();
        if(StringUtils.isNotBlank(suffix)){
            slug = slug + "-" + suffix.trim().toLowerCase(Locale.ROOT);
        }
        return slug;
    }

    String url2Slug(String url, Context.SlugGenMode mode) {
        if (mode == Context.SlugGenMode.FILENAME) {
            return url2SlugByFileName(url);
        } else if (mode == Context.SlugGenMode.DIRS_FILENAME) {
            return url2SlugByDirsAndFileName(url);
        } else {
            throw new IllegalArgumentException("Unsupported SlugGenMode: " + mode);
        }
    }

    /**
     * 跟进文件名获取文件关键字 slug
     *
     * @param url
     * @return
     */
    private String url2SlugByFileName(String url) {
        return url.replaceAll("/","_").replace(".md","").toLowerCase();
    }

    /**
     * 根据路径名获取文件关键字 slug
     *
     * @param url 文件路径，包含文件夹
     * @return 文件关键字
     */
    private String url2SlugByDirsAndFileName(String url) {
        return url.replaceAll("/","_").replace(".md","").toLowerCase();
    }
}
