package com.viewhigh.hiot.elec.support;

/**
 * 设备状态
 * @author sunhl
 *
 */
public enum DeviceState {
	offline(0,"离线"),stop(1,"关机"),sleep(2,"待机"),run(3,"运行");
	
	private int state;
	private String desc;
	
	DeviceState(int state,String desc){
		this.state = state;
		this.desc = desc;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
}
