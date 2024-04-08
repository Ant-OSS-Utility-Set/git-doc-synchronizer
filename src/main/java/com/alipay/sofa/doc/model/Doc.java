package com.alipay.sofa.doc.model;

public class Doc {

    private Integer id;
    private String slug;
    private String title;
    private Integer bookId;
    // lake or markdown
    private String format;
    // body_lark or body
    private String body;
    private String description;

    public Integer getId() {
        return id;
    }

    public Doc setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getSlug() {
        return slug;
    }

    public Doc setSlug(String slug) {
        this.slug = slug;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Doc setTitle(String title) {
        this.title = title;
        return this;
    }

    public Integer getBookId() {
        return bookId;
    }

    public Doc setBookId(Integer bookId) {
        this.bookId = bookId;
        return this;
    }

    public String getFormat() {
        return format;
    }

    public Doc setFormat(String format) {
        this.format = format;
        return this;
    }

    public String getBody() {
        return body;
    }

    public Doc setBody(String body) {
        this.body = body;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Doc setDescription(String description) {
        this.description = description;
        return this;
    }
}
