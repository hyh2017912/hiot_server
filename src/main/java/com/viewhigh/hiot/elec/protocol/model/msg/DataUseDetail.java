package com.viewhigh.hiot.elec.protocol.model.msg;

import java.io.Serializable;

/**
 * @ClassName DataUseDetail
 * Description 使用明细
 * @Author huyang
 * Date 2019/5/23 16:18
 **/
public class DataUseDetail implements Serializable {
    private long timeStart; // 开始时间
    private int useTime; // 使用时间

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public int getUseTime() {
        return useTime;
    }

    public void setUseTime(int useTime) {
        this.useTime = useTime;
    }

    @Override
    public String toString() {
        return "DataUseDetail{" +
                "timeStart=" + timeStart +
                ", useTime=" + useTime +
                '}';
    }
}
