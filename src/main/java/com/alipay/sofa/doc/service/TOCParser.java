package com.alipay.sofa.doc.service;

import com.alipay.sofa.doc.model.MenuItem;
import com.alipay.sofa.doc.model.Repo;
import com.alipay.sofa.doc.model.Context;

/**
 *
 */
public interface TOCParser {

    MenuItem parse(Repo repo, Context context);
}
