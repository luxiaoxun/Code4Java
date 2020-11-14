package com.luxx.log.executor;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MainExecutor implements InitializingBean, DisposableBean {
    @Autowired
    private DataIndexExecutor dataIndexExecutor;

    @Override
    public void afterPropertiesSet() throws Exception {
        dataIndexExecutor.start();
    }

    @Override
    public void destroy() throws Exception {
        dataIndexExecutor.stop();
    }
}
