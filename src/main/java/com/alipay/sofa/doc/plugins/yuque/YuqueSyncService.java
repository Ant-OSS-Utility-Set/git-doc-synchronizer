package com.alipay.sofa.doc.plugins.yuque;

import com.alipay.sofa.doc.model.*;
import com.alipay.sofa.doc.service.*;
import com.alipay.sofa.doc.utils.FileUtils;
import com.alipay.sofa.doc.utils.ObtainContributorsUtils;
import com.alipay.sofa.doc.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Locale;
import java.util.Map;

/**
 * ></a>
 */
@Component("yuque")
public class YuqueSyncService implements ISyncService {

    public static final Logger LOGGER = LoggerFactory.getLogger(YuqueSyncService.class);

    @Autowired
    private SummaryMdTOCParser summaryMdTocParser;
    @Autowired
    private TOCChecker tocChecker;
    @Autowired
    private YuqueDocService yuqueDocService;
    @Autowired
    private YuqueTocService yuqueTocService;

    @Autowired
    private GitService gitService;

    @Value("${sofa.doc.syncTocMode}")
    private String defaultSyncTocMode;

    @Value("${sofa.doc.slugGenMode}")
    private String defaultSlugGenMode;

    @Override
    public SyncResult doSync(String gitBranch,DocRequest docReq) {
        SyncResult result;
        try {
            // 获取请求参数
            Map<String, String> inputs = docReq.getInputs();
            String yuqueNamespace = inputs.get("yuqueNamespace");
            Assert.hasLength(yuqueNamespace, "yuqueNamespace 不能为空，请在「yml」里配置要同步的语雀知识库");
            String gitRepo = downloadAndParseRepo( docReq);
            String repoUrl =inputs.get("gitRepo");
            String startTag = "github.com/";
            String endTag = ".git";
            int startIndex = repoUrl.indexOf(startTag) + startTag.length();
            int endIndex = repoUrl.indexOf(endTag);

            String httpUrl = repoUrl.substring(startIndex, endIndex);
            String gitDocRoot = inputs.get("gitDocRoot");
            Assert.hasLength(gitRepo, "gitRepo 不能为空");
            Assert.hasLength(gitDocRoot, "gitDocRoot 不能为空");
            // 先找是否有自定义 token，没有的话再找是否有自定义 user，否则走默认 user
            String yuqueToken = inputs.get("yuqueToken");
            Assert.hasLength(yuqueToken, "yuqueToken未配置，请申请语雀团队 Token 并联系管理员进行配置");
            // 解析请求中的其他参数
            Context.SyncMode syncTocMode;
            String syncTocStr = inputs.get("syncTocStr");
            if (StringUtils.isBlank(syncTocStr)) {
                syncTocStr = defaultSyncTocMode;
            }
            syncTocMode = Context.SyncMode.valueOf(syncTocStr.toUpperCase(Locale.ROOT));
            Context.SlugGenMode slugGenMode;
            String slugGenModeStr = inputs.get("slugGenModeStr");
            if (StringUtils.isBlank(slugGenModeStr)) {
                slugGenModeStr = defaultSlugGenMode;
            }
            slugGenMode = Context.SlugGenMode.valueOf(slugGenModeStr.toUpperCase(Locale.ROOT));
            String header = inputs.get("header");
            String footer = inputs.get("footer");
            String yuqueSite = inputs.get("yuqueSite");
            if (StringUtils.isBlank(yuqueSite)) {
                throw new RuntimeException("yuqueSite字段为空");
            } else if (!yuqueSite.endsWith("/")) {
                yuqueSite = yuqueSite + "/";
            }
            String baseUrl = yuqueSite + "api/v2";
            YuqueClient client = new YuqueClient(baseUrl, yuqueToken);

            // 创建 Repo 对象
            Repo repo = new Repo()
                    .setSite(yuqueSite)
                    .setNamespace(yuqueNamespace)
                    .setLocalDocPath(FileUtils.contactPath(gitRepo, gitDocRoot)) // 下载代码到本地的地址
                    .setGitHttpURL(httpUrl) // 不带.git的地址，用于拼接字符串
                    .setTocType("markdown")
                    .setTocFile(inputs.get("gitDocToc"));
            // 创建 Context 对象
            Context context = new Context().setSyncMode(syncTocMode).setSlugGenMode(slugGenMode)
                    .setSlugPrefix(inputs.get("slugPrefix")).setSlugSuffix(inputs.get("slugSuffix"))
                    .setHeader(header).setFooter(footer).setGitDocRoot(gitDocRoot);

            // 1. 解析本地目录
            MenuItem toc = summaryMdTocParser.parse(repo, context);
            // 2. 检查 toc 内容是否正确
            tocChecker.check(repo, toc, context);
            // 3. 根据目录进行文章同步
            yuqueDocService.syncDocs(inputs.get("githubCodeSpaceUrl"),gitBranch,client, repo, toc, context,httpUrl);
            // 4. 同步目录
            yuqueTocService.syncToc(client, repo, toc, context);
            String url = FileUtils.contactPath(yuqueSite, yuqueNamespace);
            result = new SyncResult(true, "同步成功！ 请访问 <a href=\""
                    + url + "\" target=\"_blank\" >" + url + "</a> 查看最新文档！");
        } catch (Exception e) {
            result = new SyncResult(false, "同步异常！ 简单原因为：" + e.getMessage() + "，更多请查看后台日志");
            LOGGER.error("同步异常：" + e.getMessage(), e);
        }
        return result;
    }

    /**
     * 下载代码并解析
     * @param request
     * @return
     */
    private String downloadAndParseRepo(DocRequest request){
        String localRepoPath;
        try {
            Map<String, String> inputs = request.getInputs();
            localRepoPath = gitService.clone(inputs.get("gitRepo"),inputs.get("gitBranch"),inputs.get("gitCommitId"),inputs.get("gitUser"),inputs.get("gitToken"));
        }catch (Exception e){
            throw new RuntimeException("Failed to download repo: " + e.getMessage(), e);
        }
        return localRepoPath;
    };

}
