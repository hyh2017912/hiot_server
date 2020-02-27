package com.viewhigh.hiot.elec.service.serviceimp;

import com.alibaba.fastjson.JSONObject;
import com.viewhigh.hiot.elec.common.RedisPrefix;
import com.viewhigh.hiot.elec.common.ServerConfig;
import com.viewhigh.hiot.elec.common.ServerConfig2;
import com.viewhigh.hiot.elec.protocol.model.msg.DataFrequentData;
import com.viewhigh.hiot.elec.service.RedisService;
import com.viewhigh.hiot.elec.service.serviceimp.RedisServiceImpl;
import com.viewhigh.hiot.elec.support.utils.ByteET;
import com.viewhigh.hiot.elec.support.utils.CRC16ET;
import com.viewhigh.hiot.elec.support.utils.NettyUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 电流监测设备协议处理
 * @author sunhl
 *
 */
@Service
public class ProtocolElecService {

//	private RedisService redisService = new RedisServiceImpl();
	@Autowired
	private RedisService redisService;

	/* ********************************************************
	   	协议结构
	 	*******
	  	起始位置	说明	数值类型	字节长度	备注
		0	起始符	byte	2	固定字符0x2323( ASCII字符“##”)
		2	流水号	long	8	无符号数。每天由0开始计数，每条消息+1，重发消息保留原数值，达最大值后从0循环。
		10	加密类型	byte	1	加密仅针对数据区域	0x00：不加密
		11	消息标识	byte	1	详见“消息标识”定义
		12	数据长度	short	2	数据区域长度n（加密后长度）
		14	数据区	byte[n]	n	数据内容，详见各接口“数据区域”定义
		14+n	校验位	word	2	crc16校验码，用于数据一致验证。
		
		****************************************************************
		
		0x00	心跳	上行	若无数据时，保持每10s发一次心跳。3个心跳周期没有响应，则断开链接。
		0x01	身份鉴权	上行	发送身份信息，鉴权通过后该链路数据才会被服务端接收。
		0x10	采集频发数据	上行	电压、电流、功率等秒级采集上报的数据
		0x11	设备状态更新	上行	设备状态变更时触发
		0x12	意外断电数据	上行	发生意外断电后，上报意外断电次数。
		0x13	电流超限	上行	发送超限电流及电流波形数据
		0x14	电压超限	上行	发送超限电压及电压波形数据
		0x15	统计数据	上行	每天一次，累计电量、累计使用时长及明细
		0x16	配置数据	上行	设备启动时发送，超限配额、基站标识
					
		0x80	通用应答	下行	无数据区，仅作为应答判断。
		使用请求消息的流水号
		0x81	身份鉴权结果	下行	身份鉴权结果。鉴权成功后才接受其他数据消息。
		使用请求消息的流水号。

	 **********************************************************/

	
	public static final int PROTOCOL_LEN = 14;
	public static final int PROTOCOL_ALL_LEN = 16;
	private static final byte[] msgFlag = {0x01,0x02};
	
	/**
	 * 消息打包
	 * @param sn
	 * @param msgId
	 * @param data
	 * @return
	 */
	public byte[] encode(byte[] sn, byte msgId, byte[] data) {
		short len = 0;
		if (data != null){
			len = (short) data.length;
		}
		byte[] msg = new byte[len+PROTOCOL_ALL_LEN];
		
		//start
		msg[0] = 0x23;
		msg[1] = 0x23;
		
		//sn
		msg[2] = sn[0];
		msg[3] = sn[1];
		msg[4] = sn[2];
		msg[5] = sn[3];
		msg[6] = sn[4];
		msg[7] = sn[5];
		msg[8] = sn[6];
		msg[9] = sn[7];
		
		//加密：不加密
		msg[10] = 0x00;
		
		//消息标识
		msg[11] = msgId;

		
		//数据长度
		byte[] lenb = ByteET.short2Bytes(len);
		msg[12] = lenb[0];
		msg[13] = lenb[1];
		
		//数据区域
		for(short i=0;i<len;i++) {
			msg[14+i] = data[i];
		}
		
		//验证码
		short crc = CRC16ET.getCRC16(ByteET.getByteArr(msg,0,PROTOCOL_LEN+len));
		byte[] crcb = ByteET.short2Bytes(crc);
		msg[PROTOCOL_LEN+len] = crcb[0];
		msg[PROTOCOL_LEN+len+1] = crcb[1];
		
		return msg;
	}
	
	/**
	 * 消息校验,每次都会对消息进行校验，起始符，长度，鉴权，校验码
	 * @param msg 消息数据
	 * @param channelId 通道id
	 * @return
	 */
	public byte checkMsg(byte[] msg, String channelId) {
		/*
		 * 校验结果说明
		 * 
		 *  0x00：正常
			0x01：消息结构错误（响应流水号为0xFFFFFFFFFFFFFFFF）
			0x02：未鉴权	//根据链路id查缓存/内存状态。
			0x03：消息标识错误
			0x04：数据长度错误
			0x05：校验码错误
			0x06：数据错误
		 */
		//0	起始符	byte	2	固定字符0x2323( ASCII字符“##”)
		byte[] start = new byte[2];
		start[0] = msg[0];
		start[1] = msg[1];
		if(start[0]!=0x23 || start[1]!=0x23) {
			return 0x01;
		}
		
		//10	加密类型	byte	1	加密仅针对数据区域 0x00：不加密
		//TODO: 后期再处理		
		if(msg[10]!=0x00) {
			return 0x06;
		}

		//12	数据长度	short	2	数据区域长度n（加密后长度）
		short len = 0;
		byte[] lenb = ByteET.getByteArr(msg,12,2);
		len = ByteET.bytes2Short(lenb);
		if(msg.length != len + 16) {
			return 0x04;
		}

		//14+n	校验位	word	2	crc16校验码，用于数据一致验证。
		short crc1 = CRC16ET.getCRC16(ByteET.getByteArr(msg,0,PROTOCOL_LEN+len));
		short crc_msg = ByteET.bytes2Short(ByteET.getByteArr(msg, PROTOCOL_LEN+len, 2));
		if(crc1-crc_msg!=0) {
			return 0x05;
		}

		// 消息标识
		boolean isMsg = true;
		for (byte b: ServerConfig2.MSG_FLAG) {
			if (msg[11] == b){
				isMsg = false;
				break;
			}
		}
		if (isMsg){
			return 0x03;
		}

		// 是否鉴权，从缓存获取该通道是否鉴权
		boolean isCheck = isCheck(channelId);
		if (!isCheck && msg[11] != 0x01){ // 未鉴权，且消息不是鉴权消息
			return 0x02;
		}
		return 0x00;
	}

	/**
	 * 是否鉴权
	 * @param channelId
	 * @return
	 */
	private boolean isCheck(String channelId) {
		// 判断是否鉴权，查询是否存在key对应值：key：prefix + "_" + channelId
		String key = (RedisPrefix.GLOBAL_PREFIX + RedisPrefix.DATA_CHECK + channelId).toUpperCase();
		String value = redisService.get(key);
		if ("T".equals(value)){// 已鉴权
			return true;
		}else {
			return false; // 未鉴权
		}
	}

	/**
	 * 生成鉴权结果，并保存；0x00：鉴权成功。0x01：接入代码错误；0x02：鉴权码错误
	 * @param channelId  本次链接通道id
	 * @param msg
	 * @return
	 */
	public byte[] getCheckCode(String channelId, byte[] msg){
		// 鉴权,接入代码和token是线下给，数据库保存一份
		byte[] res = new byte[1];
		String key = (RedisPrefix.GLOBAL_PREFIX + RedisPrefix.DATA_CHECK + channelId).toUpperCase();
		String value = "10001_12345678901234567890123456789012";
//		String value = redisService.get(key);
		if (value == null){
			res[0] = 0x01;
			return res;  // todo 鉴权 保存？
		}
		String[] codeAndTokenLocal = value.split("_");
		String codeClient = ByteET.bytes2ReadStr(ByteET.getByteArr(msg, 14, 5));
		String tokenClient = ByteET.bytes2ReadStr(ByteET.getByteArr(msg, 19, 32));
		if (!codeAndTokenLocal[0].equals(codeClient)){
			res[0] = 0x01;
			redisService.set(key,"F");
		}else if (!codeAndTokenLocal[1].equals(tokenClient)){
			res[0] = 0x02;
			redisService.set(key,"F");
		}else{
			//鉴权成功，写入库
			redisService.set(key,"T");
		}
		return res;
	}

	/**
	 * 判断数据是否重复发送
	 * @param sn 数据流水号
	 * @param channelId  连接通道id
	 * @param magFlag 数据类型
	 * @return
	 */
	public boolean isDataRepeat(long sn, String channelId, byte magFlag) {
		//查询是否存在key对应值：key：prefix + "_" + channelId + "_" + magFlag ； value ： sn
		String key = (RedisPrefix.GLOBAL_PREFIX + RedisPrefix.DATA_REPEAT + channelId + "_" + ((int) magFlag)).toUpperCase();
		String value = redisService.get(key);
		if (value != null && (Long.valueOf(value) == sn)){//重复
			return true;
		}else{//不重复，跟新值
			redisService.set(key,String.valueOf(sn), (long) ServerConfig.DATA_EXPIRE_TIME.getValue(),TimeUnit.SECONDS);
			return false;
		}
	}

	/**
	 * 更新设备状态
	 * @param dev 设备编号
	 * @param b 设备状态
	 */
	public void updateDeviceState(String dev, byte b){
		String key = RedisPrefix.GLOBAL_PREFIX + RedisPrefix.DEVICE_ALIVE + dev.toUpperCase();
		redisService.set(key,String.valueOf(b), (long) ServerConfig.DEVICE_OFFLINE.getValue(), TimeUnit.SECONDS);
	}

	/**
	 *关闭连接通道时移除缓存中的鉴权信息
	 * @param ctx
	 */
	public void doClosed(ChannelHandlerContext ctx) {
		String channelId = NettyUtils.getChannelId(ctx);
		String key = (RedisPrefix.GLOBAL_PREFIX + RedisPrefix.DATA_CHECK + channelId).toUpperCase();
		redisService.remove(key);
		ctx.close();
	}

	/**
	 * 保存到缓存
	 * @param t
	 * @param device
	 * @param msgFlag
	 */
	public <T> void saveDataToReids(T t, String device, Date time,byte msgFlag) {
		String key = RedisPrefix.GLOBAL_PREFIX + (device + "_" + (int)msgFlag + "_" + time.getTime()).toUpperCase();
        Object value = JSONObject.toJSON(t);
		redisService.set(key,value.toString(),86400L,TimeUnit.SECONDS); // TODO 设定一个过期时间为1天
//		String s = redisService.get(key);
//		Object parse = JSONObject.parse(s);
//		System.out.println(parse);
	}

	public static void main(String[] strings){
//		boolean isMsg = true;
//		for (byte b: ServerConfig2.MSG_FLAG) {
//			if (0x14 == b){
//				isMsg = false;
//				break;
//			}
//		}
//		if (isMsg){
//			return;
//		}
	}
}
