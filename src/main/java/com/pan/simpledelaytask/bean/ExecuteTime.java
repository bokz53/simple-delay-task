package com.pan.simpledelaytask.bean;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ExecuteTime {

    private TimeUnit delayTimeUnit;

    private Long delayTime;

    private Type type;

    private Date scheduleDate;

    public TimeUnit getDelayTimeUnit() {
        return delayTimeUnit;
    }

    public void setDelayTimeUnit(TimeUnit delayTimeUnit) {
        this.delayTimeUnit = delayTimeUnit;
    }

    public Long getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(Long delayTime) {
        this.delayTime = delayTime;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Date getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }
    




    public static enum Type {
        DELAY,
        SCHEDULE
        ;
    }

    private ExecuteTime() { }

    public static ExecuteTime ofDelay(long delayTime, TimeUnit timeUnit) {
        ExecuteTime time =  new ExecuteTime();
        time.setType(Type.DELAY);
        time.setDelayTime(delayTime);
        time.setDelayTimeUnit(timeUnit);
        return time;
    }


    public static ExecuteTime ofSchedule(Date scheduleDate) {
        if (scheduleDate.before(new Date())) {
            throw new IllegalArgumentException("current date can not be lated than scheduleDate");
        }
        
        ExecuteTime time =  new ExecuteTime();
        time.setType(Type.SCHEDULE);
        time.setScheduleDate(scheduleDate);
        return time;
    }

    public long getRealExecuteTime(long currentTimeStamp) {
        return 0l;
    }


}
