package com.alipay.sofa.doc.web;

import com.alipay.sofa.doc.model.DocRequest;
import com.alipay.sofa.doc.utils.SpringContextUtils;
import com.alipay.sofa.doc.service.ISyncService;
import com.alipay.sofa.doc.model.SyncResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class SyncController {

    /**
     * Logger
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(SyncController.class);
    @Value("${sofa.doc.sync.target}")
    public String target;


    /**
     * 同步接口
     * @return SyncResult 同步结果
     */
    @RequestMapping(value = "v1/rest/sync", method = RequestMethod.POST)
    @ResponseBody
    public SyncResult doRestSampleSync( @RequestBody DocRequest docRequest) {


        SyncResult result;
        try {
            Map<String, String> inputs = docRequest.getInputs();
            String gitBranch = inputs.get("gitBranch");
            // 对应逻辑bean
            Assert.hasLength(target,"target不能为空请在配置文件中配置");
            ISyncService syncService = (ISyncService) SpringContextUtils.getContext().getBean(target);
            // 执行同步操作
            result = syncService.doSync(gitBranch,docRequest);
        } catch (Exception e) {
            LOGGER.error("同步异常：" + e.getMessage(), e);
            result = new SyncResult(false, "同步异常！ 简单原因为：" + e.getMessage() + "，更多请查看后台日志");
        }
        return result;
    }
}

