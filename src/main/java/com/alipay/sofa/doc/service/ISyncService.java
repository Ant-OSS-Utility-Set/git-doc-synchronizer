package com.alipay.sofa.doc.service;

import com.alipay.sofa.doc.model.DocRequest;
import com.alipay.sofa.doc.model.SyncResult;

public interface ISyncService {
    SyncResult doSync(String gitBranch, DocRequest componentRequest);
}
