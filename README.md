# Motivation
Sofadoc hopes to synchronize Markdown documents to Yuque, providing users with a better experience in editing and viewing documents

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

# How to use this tool in your project
    1、If your project is adding a workflow for the first time, you can
        a、Go to the repository where templates need to be added and click Actions->New workflow
        b、After entering New workflow, click set up a workflow yourself to set the work template
        c、The file name is sync-to-yuque.yml
        d、Copy the following content to the file, and modify the parameters to the real save

    2、If other workflows already exist in the project
        a、Added sync-to-yuque.yml to the.github->workflows directory
        b、Copy the following content to the file, and modify the parameters to the real save

## Template case
```yaml
name: Sync with Yuque
on:
  push:
    branches:
      - main
  pull_request:
    types:
      - closed
    branches:
      - main
jobs:
  sync:
    runs-on:  ubuntu-latest
    if: github.event_name == 'push' || (github.event_name == 'pull_request' && github.event.action == 'closed' && github.event.pull_request.base.ref == 'main')
    steps:
      - uses: Duan-0916/gitdoc-sync-docker@main
        with:
          yuqueNamespace: "Replace it with the actual yuqueNamespace,Such as:XXXXXX/XXXXXX"
          yuqueSite: "Replace with the actual yuqueSite，Such as:https://test.yuque.com"
          yuqueToken: ${{ secrets.YUQUE_TOKEN }} # Replace with the actual yuquetoken
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
