package com.pan.simpledelaytask.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.pan.simpledelaytask.TaskHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HandlerConfig {

    @Autowired
    private List<TaskHandler> handlers;

    private Map<String, List<TaskHandler>> mapping = null;


    @PostConstruct
    public void init() {
        this.mapping = new HashMap<>();
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
