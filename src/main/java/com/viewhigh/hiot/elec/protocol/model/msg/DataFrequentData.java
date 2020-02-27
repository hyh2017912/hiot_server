package com.viewhigh.hiot.elec.protocol.model.msg;

import java.io.Serializable;
import java.util.Date;

/**
 * 频发数据。
 * 
 * @author sunhl
 *
 */
public class DataFrequentData implements Serializable {

	private String device;// 设备编号
	private Date time;// 采集时间
	private int cur;// 电流
	private int vol;// 电压
	private int pwr;// 功率
	private int curAlert;// 电流警报
	private int volAlert;// 电压警报

	public String toString() {
		StringBuffer ss = new StringBuffer();
		ss.append("device=").append(device).append(",time=").append(time)
			.append(",cur=").append(cur).append(",vol=").append(vol)
			.append(",pwr=").append(pwr).append(",curAlert=")
			.append(curAlert).append(",volAlert=").append(volAlert);

		return ss.toString();
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

	public int getCur() {
		return cur;
	}

	public void setCur(int cur) {
		this.cur = cur;
	}

	public int getVol() {
		return vol;
	}

	public void setVol(int vol) {
		this.vol = vol;
	}

	public int getCurAlert() {
		return curAlert;
	}

	public void setCurAlert(int curAlert) {
		this.curAlert = curAlert;
	}

	public int getVolAlert() {
		return volAlert;
	}

	public void setVolAlert(int volAlert) {
		this.volAlert = volAlert;
	}

	public int getPwr() {
		return pwr;
	}

	public void setPwr(int pwr) {
		this.pwr = pwr;
	}

}
