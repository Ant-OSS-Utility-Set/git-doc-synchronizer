package com.alipay.sofa.doc.model;

/**
 * 语雀知识库
 */
public class Repo {
    private int id;
    // Book
    private String type;
    private String slug;
    private String name;
    private String description;
    /**
     * 站点：https://
     */
    private String site;
    /**
     * 命名空间  /test
     */
    private String namespace;
    /**
     * 文档本地根路径
     */
    private transient String localDocPath;
    /**
     * git 远程访问地址
     */
    private String gitHttpURL;
    /**
     * toc 类型：markdown/json/yml
     */
    private String tocType;
    /**
     * toc 文件地址
     */
    private String tocFile;
    private String token;

    public int getId() {
        return id;
    }

    public Repo setId(int id) {
        this.id = id;
        return this;
    }

    public String getType() {
        return type;
    }

    public Repo setType(String type) {
        this.type = type;
        return this;
    }

    public String getSlug() {
        return slug;
    }

    public Repo setSlug(String slug) {
        this.slug = slug;
        return this;
    }

    public String getName() {
        return name;
    }

    public Repo setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Repo setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public Repo setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getLocalDocPath() {
        return localDocPath;
    }

    public Repo setLocalDocPath(String localDocPath) {
        this.localDocPath = localDocPath;
        return this;
    }

    public String getGitHttpURL() {
        return gitHttpURL;
    }

    public Repo setGitHttpURL(String gitHttpURL) {
        this.gitHttpURL = gitHttpURL;
        return this;
    }

    public String getTocType() {
        return tocType;
    }

    public Repo setTocType(String tocType) {
        this.tocType = tocType;
        return this;
    }

    public String getToken() {
        return token;
    }

    public Repo setToken(String token) {
        this.token = token;
        return this;
    }

    public Repo setSite(String site) {
        this.site = site;
        return this;
    }

    public String getSite() {
        return site;
    }

    public String getTocFile() {
        return tocFile;
    }

    public Repo setTocFile(String tocFile) {
        this.tocFile = tocFile;
        return this;
    }
}
