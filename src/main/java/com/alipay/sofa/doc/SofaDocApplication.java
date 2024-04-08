package com.alipay.sofa.doc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@ImportResource(locations = "classpath*:spring/*.xml")
public class SofaDocApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(SofaDocApplication.class);

    public static void main(String[] args) {
        try {
            SpringApplication.run(SofaDocApplication.class, args);
            LOGGER.info("SOFABoot Application Start!!!");
        } catch (Throwable e) {
            LOGGER.error("SOFABoot Application Start Fail!!! More logs can be found on 1) logs/sofa-runtime/common-error.log"
                    + " 2) logs/spring/spring.log 3) logs/mvc/common-error.log 4) logs/health-check/common-error.log", e);
            throw e;
        }
    }

}
