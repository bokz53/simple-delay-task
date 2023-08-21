package com.pan.simpledelaytask.bean;

public class TaskParam {

    private String taskId;

    private String type;

    private String taskDetail;

    private Long createTime;

    public String getTaskId() {
        return this.taskId;
    }

    public String setTaskId(String taskId) {
        return this.taskId = taskId;
    }


    public String getType() {
        return this.type;
    }

    public String setType(String type) {
        return this.type = type;
    }

    public String getTaskDetail() {
        return this.taskDetail;
    }

    public void setTaskDetail(String taskDetail) {
        this.taskDetail = taskDetail;
    }

}
