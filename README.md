# Motivation
git-doc-synchronizer hopes to synchronize Markdown documents to Yuque, providing users with a better experience in editing and viewing documents

# Features
- Synchronize Markdown documents to Yuque
- Editing Mark Documents on Yuque
- Rate documents

# quickstarts

Before starting, you need to prepare the following environment on your computer

- java8
- maven3.6.3

Other prerequisites:

- Create a knowledge base on yuque
- Please apply for the Yuque Team Token
- Create a SUMMARY.md for relative use in the warehouse


If you are ready, you can follow the steps below to start our trip

First, start the project

After the project starts, use tools such as Postman to post requests and call synchronization interfaces

The interface address is:
http://localhost:8777/v1/rest/sync

The parameters are as follows:
```json
{
    "inputs": {
        
        "yuqueNamespace": "语雀知识库命名空间，例如知识库 “https://yuque.com/aaa/bbb/” 的命名空间就是 “aaa/bbb”" , 
        "yuqueSite": "语雀访问站点",
        "yuqueToken":"语雀访问 Token",
        "gitRepo": "仓库地址，https://github.com/owner/repo",
        "gitDocRoot": "git文档根路径，/",
        "gitCommitId": "commitId可选参数，可指定上传不上传则获取最新commitId",
        "gitDocToc": "git 目录文件，默认为 git 文档路径下的 /docs/SUMMARY.md",
        "gitBranch": "分支",
        "isGitHub" : "true"

    }
}
```

# How to contribute

If you want to make your contribution to Sofadoc, you can first fork it into your own warehouse, modify it, and then submit a PR

# How to expand
If you want to synchronize the markDown document of the github warehouse to other platforms, you can extend it according to the following steps

    1、You can fork the project to your own warehouse
    2、You need to create a directory under the plugins directory. This directory contains all your implementation logic(For details, please refer to the corresponding logic in Yuque). 
    3、You need to modify the sofa.doc.sync.direction configuration in the application.properties configuration file. The specific value is the bean you have created, such as syncService.
    4、After you complete the test and ensure perfect implementation, you can submit your PR to the central warehouse

Thank you very much for your contribution

## Contributors

Thank y'all!
