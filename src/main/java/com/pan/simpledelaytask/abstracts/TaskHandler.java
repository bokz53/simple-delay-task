package com.pan.simpledelaytask.abstracts;

import java.util.List;

import com.pan.simpledelaytask.bean.TaskParam;

public abstract class TaskHandler {

    public void pocesseTask(TaskParam taskParam) {
        this.doProcesse(taskParam);
    }

	public abstract void doProcesse(TaskParam taskParam);

    public abstract List<String> getTargetType();
}
