package com.viewhigh.hiot.elec.protocol.model.msg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 电流超限。
 * 
 * @author huyang
 *
 */
public class DataCurAlert implements Serializable {

	private String device;// 设备编号
	private Date time; // 超限开始时间 作为时间戳使用
	private Date sTime;//开始时间
	private Date eTime; // 结束时间
	private int curHigh;// 峰值电流
	private int curAlert;// 电流超限
	private int count; // 波形个数
	private List<DataWaveform> listWF = new ArrayList<>();

	public Date getsTime() {
		return sTime;
	}

	public void setsTime(Date sTime) {
		this.sTime = sTime;
	}

	public Date geteTime() {
		return eTime;
	}

	public void seteTime(Date eTime) {
		this.eTime = eTime;
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

	public int getCurAlert() {
		return curAlert;
	}

	public void setCurAlert(int curAlert) {
		this.curAlert = curAlert;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<DataWaveform> getListWF() {
		return listWF;
	}

	public void setListWF(List<DataWaveform> listWF) {
		this.listWF = listWF;
	}

	@Override
	public String toString() {
		return "DataCurAlert{" +
				"device='" + device + '\'' +
				", time=" + time +
				", sTime=" + sTime +
				", eTime=" + eTime +
				", curHigh=" + curHigh +
				", curAlert=" + curAlert +
				", count=" + count +
				", listWF=" + listWF +
				'}';
	}
//	public String toString() {
//		StringBuffer ss = new StringBuffer();
//		ss.append("device=").append(device).append(",time=").append(time)
//			.append(",cur=").append(cur).append(",vol=").append(vol)
//			.append(",pwr=").append(pwr).append(",curAlert=")
//			.append(curAlert).append(",volAlert=").append(volAlert);
//
//		return ss.toString();
//	}

}
