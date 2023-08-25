package com.pan.simpledelaytask.config;


import com.pan.simpledelaytask.abstracts.ExecutorSupplier;
import com.pan.simpledelaytask.bean.ExecuteTime;
import com.pan.simpledelaytask.bean.TaskParam;
import com.pan.simpledelaytask.abstracts.TaskHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class Worker {

    private Logger log = LoggerFactory.getLogger(getClass());

    public static final String LUA = "";
    public static final String TASK_QUEUE_NAME = "SDT-delay-task:queue";
    public static final String TASK_DETAIL_PREFIX = "SDT-delay-task:detail:";
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private HandlerConfig handlerConfig;

    @Autowired
    private ExecutorSupplier executorSupplier;

    @PostConstruct
    public void init() {
        this.startTaskPoll();
    }

    private void startTaskPoll() {
        ExecutorService executorService = executorSupplier.getExecutor();

        executorService.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            String taskId = redisTemplate.execute(RedisScript.of(LUA),
                    Collections.singletonList(TASK_QUEUE_NAME),
                    System.currentTimeMillis());
            if (!StringUtils.hasText(taskId)) {
                return;
            }

            String detail = redisTemplate.opsForValue().get(TASK_DETAIL_PREFIX + taskId);
            if (!StringUtils.hasText(detail)) {
                return;
            }

            TaskParam taskParam = null;
            try {
				taskParam = OBJECT_MAPPER.readValue(detail, TaskParam.class);

			} catch (JsonProcessingException e) {
                log.error("taskParam parsing error!", e);
			}

            // 自定义处理器
            List<TaskHandler> handlers = handlerConfig.getByType(taskParam.getType());

            if (null == handlers || handlers.isEmpty()) {
                log.warn("can not find handlers. TaskId : {} is done.", taskId);
            }

            final TaskParam fTask = taskParam;
            for (TaskHandler handler : handlers) {
                executorService.submit(() -> {
                    handler.pocesseTask(fTask);
                });
            }

        });

    }

    public String addTask(TaskParam taskParam, ExecuteTime executeTime) {
        return addTask(taskParam, executeTime, null);
    }

    public String addTask(TaskParam taskParam, ExecuteTime executeTime, String taskId) {
        if (null == taskId || taskId.isEmpty()) {
            taskId = randomTaskId();
            log.info("taskId is null ,'{}' is generated as taskId.", taskId);
        }

        String json = null;
        try {
			json = OBJECT_MAPPER.writeValueAsString(taskParam);
		} catch (JsonProcessingException e) {
            log.error("convert param to json error", e);
            throw new IllegalArgumentException("convert param to json error", e);
		}

        long exeTime = executeTime.getRealExecuteTime(System.currentTimeMillis());
        
        redisTemplate.opsForValue().set(TASK_DETAIL_PREFIX + taskId, json);
        redisTemplate.opsForZSet().add(TASK_QUEUE_NAME, taskId, exeTime);
        return taskId;
    }


    public String randomTaskId() {
        // todo 
        return "";
    }


}
