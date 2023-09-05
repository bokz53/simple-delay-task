package com.pan.simpledelaytask.abstracts;

import java.util.List;

import com.pan.simpledelaytask.bean.TaskParam;

public abstract class TaskHandler {

    public void processTask(TaskParam taskParam) {
        this.doProcess(taskParam);
    }

	public abstract void doProcess(TaskParam taskParam);

    public abstract List<String> getTargetType();
}
