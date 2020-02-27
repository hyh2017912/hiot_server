package com.viewhigh.hiot.elec.cache;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.viewhigh.hiot.elec.protocol.model.msg.DataFrequentData;
import com.viewhigh.hiot.elec.support.DeviceState;

/**
 * 集中缓存
 * //TODO：后续根据实际情况调整
 * @author sunhl
 *
 */
public class UniCache {

	/**
	 * 链路在线状态
	 * key - links id
	 * value - 接入code：为空表示没有鉴权，鉴权后
	 */
	private static Map<String,String> LINKS_STATE = new HashMap<>();
	
	/**
	 * 合作伙状态
	 * key - partner code
	 * value - link id
	 */
	private static Map<String,String> PARTNER_STATE = new HashMap<>();
	
	/**
	 * 设备状态
	 * key - deviceID
	 * value - DeviceState
	 */
	private static Map<String,DeviceState> DEVICE_STATE = new HashMap<>();
	
	/**
	 * 设备实时数据
	 * -- 读数和超限标识
	 * key - deviceID
	 * value - Integer
	 */
	private static Map<String, DataFrequentData> DEVICE_DATA = new HashMap<>();
	
	
	@PostConstruct
	public void init() {
		
	}
	
	/*********************************************************************/
	
	/**
	 * 链路接入
	 * @param linkId
	 */
	public static void linksConnect(String linkId) {
		LINKS_STATE.put(linkId, null);
	}
	
	/**
	 * 链路断开
	 * -- 主动断开、超时断开。
	 * @param linkId
	 */
	public static void linksClose(String linkId) {
		PARTNER_STATE.remove(LINKS_STATE.get(linkId));
		LINKS_STATE.remove(linkId);
	}
	
	/**
	 * 鉴权成功
	 * @param linkId
	 * @param code
	 */
	public static void authSuccess(String linkId,String code) {
		LINKS_STATE.remove(PARTNER_STATE.get(code));
		PARTNER_STATE.put(code, linkId);
		LINKS_STATE.put(linkId, code);
	}
	
	/**
	 * 设备实时数据
	 * 
	 * @param data
	 */
	public static void setDeviceData(DataFrequentData data) {
		DEVICE_DATA.put(data.getDevice(), data);
	}
	
	public static DataFrequentData getDeviceData(String deviceId) {
		return DEVICE_DATA.get(deviceId);
	}
	
	public static void setDeviceState(String deviceId,DeviceState state) {
		DEVICE_STATE.put(deviceId, state);
	}
	
	public static DeviceState getDeviceState(String deviceId) {
		return DEVICE_STATE.get(deviceId);
	}
}
