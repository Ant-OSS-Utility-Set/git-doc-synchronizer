package com.alipay.sofa.doc.plugins.yuque;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.doc.model.*;
import com.alipay.sofa.doc.utils.FileUtils;
import com.alipay.sofa.doc.utils.ObtainContributorsUtils;
import com.alipay.sofa.doc.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.File;
import java.text.MessageFormat;
import java.util.*;

/**
 * ></a>
 */
@Component
public class YuqueDocService {

    public static final Logger LOGGER = LoggerFactory.getLogger(YuqueDocService.class);

    @Autowired
    private ObtainContributorsUtils obtainContributorsUtils;

    /**
     * 同步整个目录对应的文档
     *
     * @param workspaceurl
     * @param gitBranch
     * @param client    YuqueClient
     * @param repo      仓库对象
     * @param toc       目录对象
     * @param context
     */
    public void syncDocs(String workspaceurl, String gitBranch,  YuqueClient client, Repo repo, MenuItem toc, Context context,String httpUrl) {
        List<MenuItem> subs = toc.getSubMenuItems();
        for (int i = 0; i < subs.size(); i++) {
            //for (int i = subs.size() - 1; i >= 0; i--) {
            // 一级目录
            MenuItem item = subs.get(i);
            syncWithChild(workspaceurl,gitBranch,client, repo, toc, context, item,httpUrl);
        }
    }

    /**
     * 同步一个文章
     *
     * @param spaceurl
     * @param gitBranch
     * @param client    YuqueClient
     * @param toc       目标对象
     * @param context
     * @param menuItem  目录节点
     */
    public void sync(String spaceurl, String gitBranch, YuqueClient client, Repo repo, MenuItem toc, Context context, MenuItem menuItem,String httpUrl) {

        Assert.notNull(client, "client is null");
        Assert.notNull(repo, "repo is null");
        Assert.notNull(toc, "toc is null");
        String namespace = repo.getNamespace();
        Assert.notNull(namespace, "namespace is null");
        String url = menuItem.getUrl();
        if (MenuItem.MenuItemType.TITLE.equals(menuItem.getType())) {
            LOGGER.info("  type is TITLE, continue.");
        } else if (MenuItem.MenuItemType.LINK.equals(menuItem.getType())) {
            LOGGER.info("  type is LINK, continue.");
            menuItem.setSlug(url);
        } else {

            YuqueSlugGenerator generator = new YuqueSlugGenerator();
            // 拼接，放回
            String slug = generator.url2Slug(url, context);
            menuItem.setSlug(slug);

            long start = System.currentTimeMillis();
            String newContent = getContent(spaceurl,gitBranch,repo, context, menuItem,httpUrl);
            Doc doc = query(client, namespace, slug);
            if (doc == null) { // 新增
                doc = new Doc();
                doc.setTitle(menuItem.getTitle());
                doc.setFormat("markdown");
                doc.setBody(newContent);
                doc.setSlug(slug);
                insert(client, namespace, doc);
            } else { // 更新
                doc.setTitle(menuItem.getTitle());
                doc.setFormat("markdown");
                doc.setBody(newContent);
                update(client, namespace, doc);
            }
            try {
                long elapsed = System.currentTimeMillis() - start;
                int syncDocSleepTime = 150;
                if (elapsed < syncDocSleepTime) {
                    Thread.sleep(syncDocSleepTime - elapsed);
                }
            } catch (InterruptedException e) {
                // NOPMD
            }
        }
    }

    /**
     * 插入一个节点及其所有子节点
     *
     * @param gitBranch
     * @param repo      文档仓库
     * @param toc       目录
     * @param context
     * @param menuItem  要添加的目标节点
     */
    public void syncWithChild(String spaceurl,String gitBranch,  YuqueClient client, Repo repo, MenuItem toc, Context context, MenuItem menuItem,String httpUrl) {
        Assert.notNull(client, "client is null");
        Assert.notNull(repo, "repo is null");
        Assert.notNull(toc, "toc is null");
        Assert.notNull(repo.getNamespace(), "namespace is null");
        // 新建一行
        LOGGER.info("sync menu item: {}, {}", menuItem.getTitle(), menuItem.getUrl());
        sync(spaceurl,gitBranch,client, repo, toc, context, menuItem,httpUrl);

        // 然后遍历子目录
        List<MenuItem> subs = menuItem.getSubMenuItems();
        if (!subs.isEmpty()) {
            for (int i = 0; i < subs.size(); i++) {
                //for (int i = subs.size() - 1; i >= 0; i--) {
                MenuItem subMenuItem = subs.get(i);
                syncWithChild(spaceurl,gitBranch, client, repo, toc, context, subMenuItem ,httpUrl);
            }
        }
    }

    /**
     * @param namespace repo 仓库
     * @param slug      唯一标识
     * @return 文档对象
     */
    public Doc query(YuqueClient client, String namespace, String slug) {
        Assert.notNull(client, "client is null");
        Assert.notNull(namespace, "namespace is null");
        Assert.notNull(slug, "slug is null");
        String url = "repos/" + namespace + "/docs/" + slug;
        String json = client.get(url);
        JSONObject res = JSONObject.parseObject(json);
        JSONObject data = res.getJSONObject("data");
        return data == null ? null : json2Doc(data);
    }

    /**
     * 新增文档
     *
     * @param namespace repo 仓库
     * @param doc       文档对象
     */
    public void insert(YuqueClient client, String namespace, Doc doc) {
        Assert.notNull(client, "client is null");
        Assert.notNull(namespace, "namespace is null");
        Assert.notNull(doc, "doc is null");
        LOGGER.info("  insert doc:{}, {}, {}", namespace,doc.getSlug(), doc.getTitle());

        String url = "repos/" + namespace + "/docs";
        Map<String, String> map = new HashMap<>();
        map.put("title", doc.getTitle());
        map.put("slug", doc.getSlug());
        map.put("format", doc.getFormat());
        map.put("body", doc.getBody());


        String json = client.post(url, null, JSON.toJSONString(map));
        JSONObject res = JSONObject.parseObject(json);
        JSONObject data = res.getJSONObject("data");
        if (data == null) {
            // 创建不成功
            LOGGER.error("Failed to add doc: " + doc.getTitle() + ", response data is : " + json);
            if (json.contains(YuqueConstants.HTTP_API_CODE_OVERLOAD)) {
                throw new RuntimeException("新增语雀文档失败，当前账号已经超过语雀 API 使用次数限制，请稍后再试");
            } else if (json.contains("路径名冲突")) {
                throw new RuntimeException("请检查知识库中是否存在同路径文档，比如回收站中，请清理后再试");
            } else {
                throw new RuntimeException("新增语雀文档失败，请检查文档和知识库是否存在或者当前同步用户有知识库操作权限");
            }
        } else {
            doc.setId(data.getInteger("id"));
        }
    }

    /**
     * 更新文档
     *
     * @param namespace repo 仓库
     * @param doc       文档对象
     */
    public void update(YuqueClient client, String namespace, Doc doc) {
        Assert.notNull(client, "client is null");
        Assert.notNull(namespace, "namespace is null");
        Assert.notNull(doc, "doc is null");
        LOGGER.info("  update doc: {}, {}", doc.getSlug(), doc.getTitle());

        // 强制更新的后面
        String url = "repos/" + namespace + "/docs/" + doc.getId() + "?_force_asl=true";
        Map<String, Object> map = new HashMap<>();
        map.put("title", doc.getTitle());
        map.put("slug", doc.getSlug());
        map.put("format", doc.getFormat());
        map.put("body", doc.getBody());
        map.put("status", 1);

        String json = client.put(url, null, JSON.toJSONString(map));
        JSONObject res = JSONObject.parseObject(json);
        JSONObject data = res.getJSONObject("data");
        if (data == null) {
            // 更新不成功
            LOGGER.error("Failed to update doc: " + doc.getTitle() + ", response data is : " + json);
            if (json.contains(YuqueConstants.HTTP_API_CODE_OVERLOAD)) {
                throw new RuntimeException("更新语雀文档失败，当前账号已经超过语雀 API 使用次数限制，请稍后再试");
            } else if (json.contains("路径名冲突")) {
                throw new RuntimeException("请检查知识库中是否存在同路径文档，比如回收站中，请清理后再试");
            } else {
                throw new RuntimeException("更新语雀文档失败，请检查文档和知识库是否存在或者当前同步用户有知识库操作权限");
            }
        }
    }

    protected Doc json2Doc(JSONObject data) {
        Doc doc = new Doc();
        doc.setId(data.getInteger("id"));
        doc.setTitle(data.getString("title"));
        doc.setFormat(data.getString("format"));
        doc.setBody(data.getString("body"));
        doc.setSlug(data.getString("slug"));
        return doc;
    }

    protected String getContent(String spaceurl, String gitBranch, Repo repo, Context context, MenuItem menuItem,String httpUrl) {
        String filePath = menuItem.getUrl();
        String title = menuItem.getTitle();
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }
        File file = new File(repo.getLocalDocPath(), filePath);
        String yuqueUrl = FileUtils.contactPath(repo.getSite(), repo.getNamespace(), menuItem.getSlug());
        Map<String, Object> contributors = obtainContributorsUtils.getContributors(httpUrl, filePath);
        Object latestName = contributors.get("latestName");
        Object latestDate = contributors.get("latestDate");
        Object uniqueNames = contributors.get("uniqueNames");
        try {
            List<String> lines = FileUtils.readLines(file);
            boolean removeTitle = false;
            StringBuilder content = new StringBuilder(512);
            for (String line : lines) {
                if (!removeTitle) {
                    if (StringUtils.isNotBlank(line)) {
                        removeTitle = true;
                        if (line.trim().startsWith("#") && line.contains(title)) {
                            continue; // 第一非空行和标题重复，删掉
                        }
                    } else {
                        continue;
                    }
                }
                content.append(line).append("\n");
            }
            if (lines.size() > 16) {
                content.append("\n");
                generateEditURL(gitBranch, repo, content, context, filePath);
                genericHeaderAndFooter(repo, yuqueUrl, content, context.getFooter());
                content.append("\t\t\t\t最后修改时间:"  + latestDate);
                content.append("\n最后修改人:"  + latestName);
                content.append("\n所有贡献者:"  + uniqueNames);
                content.append("\n");
            }
            return content.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void generateEditURL(String gitBranch,  Repo repo, StringBuilder content, Context context, String filePath) {
        if (StringUtils.isNotEmpty(repo.getGitHttpURL())) {
            String url = "https://github.com/%s/edit/%s/%s";
            String URL = String.format(url, getProject(repo.getGitHttpURL()), gitBranch,getFilePath(context, filePath));
            content.append("[✍️️ 在GitHub 上编辑此页](").append(URL).append(")        ");

        }
    }

    /**
     * @param git
     * @return /test-doc
     */
    String getProject(String git) {
        if (git.endsWith(".git")) {
            git = git.substring(0, git.length() - 4);
        }
        int idx = git.lastIndexOf("/");
        String repo = git.substring(idx + 1);
        git = git.substring(0, idx);
        idx = git.lastIndexOf("/");
        String group = git.substring(idx + 1);
        return group + "/" + repo;
    }

    /**
     * @param context  上下文，包括同步路径，例如 / 和  /doc
     * @param filePath 文件路径
     * @return 同步路径 + 文件路径
     */
    String getFilePath(Context context, String filePath) {
        String path;
        if (context != null) {
            String gitDocRoot = context.getGitDocRoot();
            if (StringUtils.isNotEmpty(gitDocRoot)) {
                if (gitDocRoot.endsWith("/")) {
                    gitDocRoot = gitDocRoot.substring(0, gitDocRoot.length() - 1);
                }
                path = gitDocRoot + "/" + (filePath.startsWith("/") ? filePath.substring(1) : filePath);
            } else {
                path = filePath;
            }
        } else {
            path = filePath;
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }

    protected void genericHeaderAndFooter( Repo repo, String yuqueUrl, StringBuilder content, String headerOrFooter) {
        if (StringUtils.isNotEmpty(headerOrFooter)) {
            content.append(MessageFormat.format(headerOrFooter, yuqueUrl, repo.getNamespace()));
        }
    }
}