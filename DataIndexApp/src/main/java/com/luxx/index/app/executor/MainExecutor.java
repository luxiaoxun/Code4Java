package com.luxx.index.app.executor;

import com.luxx.index.executor.DataIndexExecutor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class MainExecutor implements InitializingBean, DisposableBean {
    @Autowired
    @Qualifier("poiIndexExecutor")
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
