package com.viewhigh.hiot.elec.protocol.model.msg;

import java.io.Serializable;
import java.util.Date;

/**
 * 配置数据。
 * 
 * @author huyang
 *
 */
public class DataConfigData implements Serializable {

	private String device;// 设备编号
	private Date time;// 采集时间
	private int curHigh;// 最高电流
	private int curLow;// 最低电流
	private int volHigh;// 最高电压
	private int volLow;// 最低电压
	private int baseMark;// 基站位置码
	private int baseArea;//基站区域码

	public int getBaseArea() {
		return baseArea;
	}

	public void setBaseArea(int baseArea) {
		this.baseArea = baseArea;
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

	public int getCurHigh() {
		return curHigh;
	}

	public void setCurHigh(int curHigh) {
		this.curHigh = curHigh;
	}

	public int getCurLow() {
		return curLow;
	}

	public void setCurLow(int curLow) {
		this.curLow = curLow;
	}

	public int getVolHigh() {
		return volHigh;
	}

	public void setVolHigh(int volHigh) {
		this.volHigh = volHigh;
	}

	public int getVolLow() {
		return volLow;
	}

	public void setVolLow(int volLow) {
		this.volLow = volLow;
	}

	public int getBaseMark() {
		return baseMark;
	}

	public void setBaseMark(int baseMark) {
		this.baseMark = baseMark;
	}

//	public String toString() {
//		StringBuffer ss = new StringBuffer();
//		ss.append("device=").append(device).append(",time=").append(time)
//			.append(",curHigh=").append(curHigh).append(",curLow=").append(curLow)
//			.append(",volHigh=").append(volHigh).append(",volLow=")
//			.append(volLow).append(",baseMark=").append(baseMark);
//
//		return ss.toString();
//	}


	@Override
	public String toString() {
		return "DataConfigData{" +
				"device='" + device + '\'' +
				", time=" + time +
				", curHigh=" + curHigh +
				", curLow=" + curLow +
				", volHigh=" + volHigh +
				", volLow=" + volLow +
				", baseMark=" + baseMark +
				", baseArea=" + baseArea +
				'}';
	}
}
