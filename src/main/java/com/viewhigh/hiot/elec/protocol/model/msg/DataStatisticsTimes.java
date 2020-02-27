package com.viewhigh.hiot.elec.protocol.model.msg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 统计次数。
 * 
 * @author huyang
 *
 */
public class DataStatisticsTimes implements Serializable {

	private String device;// 设备编号
	private Date time;// 采集时间
	private int cumulativePower;// 累计点亮
	private int useTimes;// 使用次数
	private List<DataUseDetail> listSta = new ArrayList<>();

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

	public int getCumulativePower() {
		return cumulativePower;
	}

	public void setCumulativePower(int cumulativePower) {
		this.cumulativePower = cumulativePower;
	}

	public int getUseTimes() {
		return useTimes;
	}

	public void setUseTimes(int useTimes) {
		this.useTimes = useTimes;
	}

	public List<DataUseDetail> getListSta() {
		return listSta;
	}

	public void setListSta(List<DataUseDetail> listSta) {
		this.listSta = listSta;
	}

	@Override
	public String toString() {
		return "DataStatisticsTimes{" +
				"device='" + device + '\'' +
				", time=" + time +
				", cumulativePower=" + cumulativePower +
				", useTimes=" + useTimes +
				", listSta=" + listSta +
				'}';
	}
}
