package com.viewhigh.hiot.elec.protocol.model.msg;

import java.io.Serializable;
import java.util.Date;

/**
 * 设备更新
 * @author huyang
 */
public class DataDeviceState implements Serializable {

	private String device;// 设备编号
	private Date time;// 采集时间
	private byte state;

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

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

//	public String toString() {
//		StringBuffer ss = new StringBuffer();
//		ss.append("device=").append(device).append(",time=").append(time)
//			.append(",state=").append(state);
//		return ss.toString();
//	}


	@Override
	public String toString() {
		return "DataDeviceState{" +
				"device='" + device + '\'' +
				", time=" + time +
				", state=" + state +
				'}';
	}
}
