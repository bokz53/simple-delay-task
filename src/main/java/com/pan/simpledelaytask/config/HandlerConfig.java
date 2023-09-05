package com.pan.simpledelaytask.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.pan.simpledelaytask.abstracts.TaskHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class HandlerConfig {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Bean
    public Worker getWorker() {
        return new Worker();
    }

    @Autowired(required = false)
    private List<TaskHandler> handlers;

    private Map<String, List<TaskHandler>> mapping = null;


    @PostConstruct
    public void init() {
        this.mapping = new HashMap<>();
        if (null == this.handlers || this.handlers.isEmpty()) {
            log.warn("you didn't implements any taskHandler, your task will not be executed. ");
            return;
        }
        for (TaskHandler handler : handlers) {
            List<String> types = handler.getTargetType();
            for (String type : types) {
                this.mapping.compute(type, (k, v) -> {
                    List<TaskHandler> list = null;
                    if (null != v) {
                        list = v;
                    } else {
                        list = new ArrayList<>();
                    }

                    list.add(handler);
                    return list;
                });
            }
        }
    }

    public List<TaskHandler> getByType(String type) {
        return this.mapping.get(type);
    }


}
