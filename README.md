# 背景
git-doc-synchronizer 希望将Markdown文档同步到语雀等文档托管平台，为用户提供更好的编辑和查看文档的体验

# 特性
- 同步Markdown文档到语雀
- 在语雀上编辑文档
- 更好的阅读体验

# 快速开始

在开始之前，您需要在计算机上准备以下环境
- java8
- maven3.6.3

其他条件

- 在语雀上创建一个知识库
- 申请语雀团队token
- 创建一个用于存储文档的路径


如果你准备好了，你可以按照下面的步骤开始

首先，启动项目

项目启动后，使用Postman等工具发布请求并调用同步接口

接口地址为:http://localhost:8777/v1/rest/sync

参数说明如下:
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

# 如何在你的项目中使用这个工具
    1、如果您的项目是第一次添加工作流，您可以这样做
        a、进入需要添加模板的存储库，单击Actions->New workflow
        b、进入“新建工作流”后，单击“自己设置工作流”，设置工作模板
        c、文件名为“sync-to-yuqueue.yml”
        d、将以下内容拷贝到文件中，并将参数修改为real save

    2、如果项目中已经存在其他工作流
        a、sync-to-yuque补充道.github->workflows 
        b、将以下内容复制到文件中，并将参数修改为实际保存

## 模版样例
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
          yuqueNamespace: "将其替换为实际的yuqueNamespace,例如:XXXXXX/XXXXXX"
          yuqueSite: "Replace with the actual yuqueSite，Such as:https://test.yuque.com"
          yuqueToken: ${{ secrets.YUQUE_TOKEN }} # Replace with the actual yuquetoken
```

# 如何贡献

如果您想为Ssynchronizer做出贡献，您可以首先将其分叉到您自己的仓库中，修改它，然后提交PR

# 如何扩展
如果要将github仓库的markDown文档同步到其他平台，可以按照以下步骤对其进行扩展

    1、你可以把项目fork到你自己的仓库
    2、您需要在plugins目录下创建一个目录。此目录包含所有实现逻辑(详细信息请参考Yuque中相应的逻辑) 
    3、修改应用中的sofa.doc.sync.direction配置。属性配置文件。具体值是您创建的bean，例如syncService
    4、在您完成测试并确保完美执行后，您可以将您的PR提交到中央仓库

**以飞行书文档为例**

飞书文档文档同步文档API是： https://{domain}/open-apis/drive/v1/medias/upload_all     ##domain 需替换为真实域名

**所需的参数**

    Content-Type: multipart/form-data
    Authorization: Bearer u-{xxxxx} //替换为真实的Authorization
    ile_name="demo.txt"  //需要上传的文件名称
    parent_type="ccm_import_open"
    size="5"
    extra={"obj_type":"docx","file_extension":"txt"} 设置上传文档的格式，docx、txt、Markdown等
    file=@"demo.txt"  //本地文件路径

**上传文件类型设置**

    type和file_extension需要结合来看，其中：  
    当type取值为docx时，file_extension支持取值为：  
    docx（Microsoft Word Document）  
    doc（Microsoft Word 97-2004 Document）  
    txt（文本文件）  
    md（Markdown）  
    mark（Markdown）  
    markdown（Markdown）  
    html (HTML)  

还可以使用postman进行调试，如下所示

```
curl --location --request POST 'https://{domain}/open-apis/drive/v1/medias/upload_all' \ // domain 需替换为真实域名
--header 'Content-Type: multipart/form-data' \
--header 'Authorization: Bearer u-{xxxxx}' \ // 需替换为真实 Authorization
--form 'file_name="demo.txt"' \ // 本地文件名
--form 'parent_type="ccm_import_open"' \ // 使用固定值：ccm_import_open
--form 'size="5"' \ // 本地文件大小（以字节为单位）
--form 'extra={"obj_type":"docx","file_extension":"txt"}' \ // 将本地 txt 格式文件导入为新版文档
--form 'file=@"demo.txt"' // 本地文件路径
```

您可以参考yuque的代码并遵循实际的开放api，以同步实现到其他文档平台的扩展

## 贡献者

Thank y'all!

<a href="https://github.com/Ant-OSS-Utility-Set/git-doc-synchronizer/graphs/contributors">
<img src="https://contrib.rocks/image?repo=Ant-OSS-Utility-Set/git-doc-synchronizer" />
</a>