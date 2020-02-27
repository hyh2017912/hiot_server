package com.viewhigh.hiot.elec.protocol.model.msg;

import java.io.Serializable;
import java.util.Date;

/**
 * 意外断电。
 * 
 * @author huyang
 *
 */
public class DataUnexpectedPowerOff implements Serializable {

	private String device;// 设备编号
	private Date time;// 采集时间
	private int times;

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

//	public String toString() {
//		StringBuffer ss = new StringBuffer();
//		ss.append("device=").append(device).append(",time=").append(time)
//			.append(",times=").append(times);
//		return ss.toString();
//	}


	@Override
	public String toString() {
		return "DataUnexpectedPowerOff{" +
				"device='" + device + '\'' +
				", time=" + time +
				", times=" + times +
				'}';
	}
}
