package com.pan.simpledelaytask.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.pan.simpledelaytask.abstracts.ExecutorSupplier;
import com.pan.simpledelaytask.threads.SdtDefaultThreadFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(value = ExecutorSupplier.class, ignored = DefaultExecutorSupplier.class)
public class DefaultExecutorSupplier implements ExecutorSupplier {


    @Value("${sdt.thread-pool.core:5}")
    private Integer core;


    @Value("${sdt.thread-pool.max:8}")
    private Integer max;

    @Value("${sdt.thread-pool.alive-sec:120}")
    private Integer keepAliveTimeSec;

	@Override
    public ExecutorService getExecutor() {
        return new ThreadPoolExecutor(core, 
            max, 
            keepAliveTimeSec, 
            TimeUnit.SECONDS, 
            new ArrayBlockingQueue<>(1024),
            SdtDefaultThreadFactory.INSTANCE, 
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

}
