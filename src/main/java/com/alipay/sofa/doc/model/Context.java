package com.alipay.sofa.doc.model;

/**
 * ></a>
 */
public class Context {

    private SyncMode syncMode = SyncMode.MERGE;

    private SlugGenMode slugGenMode = SlugGenMode.FILENAME;

    private String slugPrefix;

    private String slugSuffix;

    private String header;

    private String footer;

    private String gitDocRoot;

    public SyncMode getSyncMode() {
        return syncMode;
    }

    public Context setSyncMode(SyncMode syncMode) {
        this.syncMode = syncMode;
        return this;
    }

    public SlugGenMode getSlugGenMode() {
        return slugGenMode;
    }

    public Context setSlugGenMode(SlugGenMode slugGenMode) {
        this.slugGenMode = slugGenMode;
        return this;
    }

    public String getHeader() {
        return header;
    }

    public Context setHeader(String header) {
        this.header = header;
        return this;
    }

    public String getFooter() {
        return footer;
    }

    public Context setFooter(String footer) {
        this.footer = footer;
        return this;
    }

    public String getGitDocRoot() {
        return gitDocRoot;
    }

    public Context setGitDocRoot(String gitDocRoot) {
        this.gitDocRoot = gitDocRoot;
        return this;
    }

    public String getSlugPrefix() {
        return slugPrefix;
    }

    public Context setSlugPrefix(String slugPrefix) {
        this.slugPrefix = slugPrefix;
        return this;
    }

    public String getSlugSuffix() {
        return slugSuffix;
    }

    public Context setSlugSuffix(String slugSuffix) {
        this.slugSuffix = slugSuffix;
        return this;
    }

    public enum SyncMode {
        /**
         * 不同步，只根据目录文件来同步文档，需要手动到语雀知识库里维护目录
         */
        IGNORE,
        /**
         * 全覆盖模式，推荐！适合全部文档托管到git库的场景，先清空原有目录再同步新目录
         */
        OVERRIDE,
        /**
         * 合并模式，适合部分文档托管到git库，部分直接语雀维护的场景，自动按照一级目录进行合并，如一级目录名变化可能存在垃圾数据
         */
        MERGE
    }

    /**
     * 语雀 slug 规则：访问路径至少 2 个字符，只能输入小写字母、数字、横线、下划线和点
     */
    public enum SlugGenMode {
        /**
         * 取文件名，转小写
         */
        FILENAME,
        /**
         * 文件路径和文件名用 - 拼接，并从后截取
         */
        DIRS_FILENAME
    }
}
