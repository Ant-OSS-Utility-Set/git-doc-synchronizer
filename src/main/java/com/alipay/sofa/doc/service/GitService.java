package com.alipay.sofa.doc.service;

import com.alipay.sofa.doc.utils.FileUtils;
import com.alipay.sofa.doc.utils.StringUtils;
import jakarta.annotation.PostConstruct;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.File;

/**
 * ></a>
 */
@Component
public class GitService {

    public static final Logger LOGGER = LoggerFactory.getLogger(GitService.class);

    @Value("${sofa.doc.git.cacheEnable}")
    boolean cacheEnable = true;

    @Value("${sofa.doc.git.cachePath}")
    String gitCacheRepo;

    @Value("${sofa.doc.git.deployKeyFile}")
    String gitDeployKeyFile;

    /**
     * @param gitRepo  repo 地址
     * @param branch   分支
     * @param commitId 分支提交Id
     * @return 本地地址
     * @throws Exception 出现异常
     */
    public String clone( String gitRepo, String branch, String commitId,String name,String token) throws Exception {
        String gitRepoName = getGitRepoName(gitRepo); // 不带 http和.git的地址，用于生成本地文件夹，例如：xxx.bbb.ccc//test-doc
        gitRepo = getGitHttpURL(gitRepo);
        return clone(gitRepo, gitRepoName, branch, commitId,name,token);
    }

    /**
     * @param gitSshURL  远程 git 地址
     * @param repoName 仓库名称
     * @param branch   分支
     * @param commitId 分支提交Id
     * @return 本地地址
     * @throws Exception 出现异常
     */
    public String clone(String gitSshURL, String repoName, String branch, String commitId,String name,String token) throws Exception {
        File localRepoPath;
        Git git = null;
        try {
            if (cacheEnable) {
                // 可以加快速度，但是同一个仓库并发同步的情况下，缓存模式可能会报错
                localRepoPath = new File(gitCacheRepo + "/" + repoName);
                File gitDir = new File(localRepoPath, ".git");
                if (gitDir.exists()) {
                    // git pull
                    LOGGER.info(".git directory exists, try git pull: {}", gitDir.getAbsolutePath());
                    git = new Git(new FileRepository(gitDir));
                    try {
                        gitPull(git, branch);
                        LOGGER.info("git pull success! {}", gitDir.getAbsolutePath());
                    } catch (Exception e) {
                        LOGGER.warn("git pull failed, try remove directory and git clone: " + gitDir.getAbsolutePath(), e);
                        git.close();
                        FileUtils.cleanDirectory(localRepoPath);
                        git = gitClone(gitSshURL, branch, localRepoPath,name,token);
                        LOGGER.info("git clone success! {}", localRepoPath.getAbsolutePath());
                    }
                } else {
                    LOGGER.info(".git directory not exists, try git clone: {}", gitDir.getAbsolutePath());
                    FileUtils.cleanDirectory(localRepoPath);
                    git = gitClone(gitSshURL, branch, localRepoPath,name,token);
                    LOGGER.info("git clone success! {}", localRepoPath.getAbsolutePath());
                }
            } else {
                // 每次都重新下载，支持毫秒级并发
                localRepoPath = new File(gitCacheRepo + "/" + repoName + "_" + System.currentTimeMillis());
                LOGGER.info("remove old directory and try git clone: {}", localRepoPath.getAbsolutePath());
                FileUtils.cleanDirectory(localRepoPath);
                git = gitClone(gitSshURL, branch, localRepoPath,name,token);
                LOGGER.info("git clone success! {}", localRepoPath.getAbsolutePath());
            }
            // git checkout
            checkOutCommitId(git, commitId, "c_" + System.currentTimeMillis());
        } finally {
            if (git != null) {
                git.close();
            }
        }
        return localRepoPath.getAbsolutePath();
    }

    /**
     * git clone
     *
     * @param gitUrl    远程地址
     * @param branch    分支
     * @param localPath 本地存储地址
     * @return Git代码库对象
     * @throws GitAPIException 出现异常
     */
    private Git gitClone(String gitUrl, String branch, File localPath,String name,String token) throws GitAPIException {
        if(StringUtils.isBlank(name) &&StringUtils.isBlank(token)){
            LOGGER.info("开始下载：：：：：：：：：：：：：：git clone:"+gitUrl+";branch:"+branch+";localpath:"+localPath);
            return Git.cloneRepository()
                    .setURI(gitUrl) //设置远程URI
                    .setBranch(branch) //设置clone下来的分支
                    .setDirectory(localPath) //设置下载存放路径
                    .call();
        }else{
            LOGGER.info("用户名和密码："+name+";"+token);
            UsernamePasswordCredentialsProvider credentials = new UsernamePasswordCredentialsProvider(name, token);
            LOGGER.info("开始下载：：：：：：：：：：：：：：git clone:"+gitUrl+";branch:"+branch+";localpath:"+localPath);
            return Git.cloneRepository()
                    .setURI(gitUrl) //设置远程URI
                    .setBranch(branch) //设置clone下来的分支
                    .setCredentialsProvider(credentials)
                    .setDirectory(localPath) //设置下载存放路径
                    .call();
        }


    }

    /**
     * git pull
     *
     * @param git    代码库
     * @param branch 要 pull 的分支
     * @throws GitAPIException 出现异常
     */
    private void gitPull(Git git, String branch) throws GitAPIException {
        git.pull().setRemoteBranchName(branch).call();
    }

    /**
     * @param git       代码库
     * @param commitId  commit id
     * @param newBranch 新分支名称
     * @throws Exception
     */
    private void checkOutCommitId(Git git, String commitId, String newBranch) throws Exception {
        try {
            git.checkout().setCreateBranch(true).setName(newBranch).call();
        } catch (RefAlreadyExistsException e) {
            // 如果分支已存在，直接切
            git.checkout().setName(newBranch).call();
        }
    }


    /**
     * @param gitRepo
     * @return
     */
    public String getGitHttpURL(String gitRepo) {
        gitRepo = gitRepo.replace("git@", "http://");
        gitRepo = gitRepo.replace("git://", "http://");
        if (gitRepo.endsWith(".git")) {
            gitRepo = gitRepo.substring(0, gitRepo.length() - 4);
        }
        if (gitRepo.endsWith("/")) {
            gitRepo = gitRepo.substring(0, gitRepo.length() - 1);
        }
        gitRepo = gitRepo.replace(":", "/");
        gitRepo = gitRepo.replace("http///", "http://");
        gitRepo = gitRepo.replace("https///", "https://");
        return gitRepo;
    }

    /**
     * @param gitRepo
     * @return 唯一路径
     */
    String getGitRepoName(String gitRepo) {
        String gitPath = getGitHttpURL(gitRepo);
        if (gitPath.contains("://")) {
            gitPath = gitPath.substring(gitPath.indexOf("://") + 3);
        } else if (gitPath.contains("@")) {
            gitPath = gitPath.substring(gitPath.indexOf("@") + 1);
        }
        gitPath = gitPath.replace(":", "/");
        return gitPath;
    }
}
