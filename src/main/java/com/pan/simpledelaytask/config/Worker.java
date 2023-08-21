package com.pan.simpledelaytask.config;


import com.pan.simpledelaytask.TaskHandler;
import com.pan.simpledelaytask.bean.TaskParam;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public static final String LUA = "";
    public static final String TASK_QUEUE_NAME = "SDT-delay-task:queue";
    public static final String TASK_DETAIL_PREFIX = "SDT-delay-task:detail:";
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private HandlerConfig handlerConfig;

    // todo 自定义线程池
    @Autowired
    private ExecutorService executorService;

    @PostConstruct
    public void init() {

        this.startTaskPoll();
    }

    private void startTaskPoll() {
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            // 自定义处理器
            List<TaskHandler> handlers = handlerConfig.getByType(taskParam.getType());

            if (null == handlers || handlers.isEmpty()) {
                // log.warn("can not find handlers");
            }

            final TaskParam fTask = taskParam;
            for (TaskHandler handler : handlers) {
                executorService.submit(() -> {
                    handler.pocesseTask(fTask);
                });
            }

        });


    }


}
