package com.viewhigh.hiot.elec.support.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletInputStream;

/**
 * 	字节处理工具。
 * @author sunhl
 *
 */
public class ByteET {
	
	/**
	 * 	字节截取
	 * 	-- 长度不足时报错：下标超界
	 * @param byteArr
	 * @param start
	 * @param len
	 * @return
	 */
	public static byte[] getByteArr(byte[] byteArr, int start, int len) {
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++) {
			result[i] = byteArr[i + start];
		}
		return result;
	}

	/**
	 * short >> byte[2]
	 * @param n
	 * @return
	 */
	public static byte[] short2Bytes(short n) {
		byte[] b = new byte[2];
		b[1] = (byte) (n & 0xff);
		b[0] = (byte) ((n >> 8) & 0xff);
		return b;
	}

	/**
	 *  byte[2] >> short
	 * @param b
	 * @return
	 */
	public static short bytes2Short(byte[] b) {
		return (short) (b[1] & 0xff | (b[0] & 0xff) << 8);
	}

	/**
	 * int >> byte[4]
	 * @param num
	 * @return
	 */
	public static byte[] int2Bytes(int num) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (num >>> (24 - i * 8));
		}
		return b;
	}

	/**
	 *  byte[4] >> int
	 * @param b
	 * @return
	 */
	public static int bytes2int(byte[] b) {
		int mask = 0xff;
		int temp = 0;
		int res = 0;
		for (int i = 0; i < 4; i++) {
			res <<= 8;
			temp = b[i] & mask;
			res |= temp;
		}
		return res;
	}

	/**
	 *  long >> byte[8]
	 * @param num
	 * @return
	 */
	public static byte[] long2Bytes(long num) {
		byte[] b = new byte[8];
		for (int i = 0; i < 8; i++) {
			b[i] = (byte) (num >>> (56 - i * 8));
		}
		return b;
	}

	/**
	 *  byte[8] >> long
	 * @param bb
	 * @return
	 */
	public static long bytes2Long(byte[] bb) {
		return ((((long) bb[0] & 0xff) << 56) | (((long) bb[1] & 0xff) << 48)
				| (((long) bb[2] & 0xff) << 40) | (((long) bb[3] & 0xff) << 32)
				| (((long) bb[4] & 0xff) << 24) | (((long) bb[5] & 0xff) << 16)
				| (((long) bb[6] & 0xff) << 8) | (((long) bb[7] & 0xff) << 0));
	}

	/**
	 * 	复制/克隆字节流
	 * @param byteArr
	 * @return
	 */
	public static byte[] byteArr2ByteArr(Byte[] byteArr) {
		byte[] result = new byte[byteArr.length];
		for (int i = 0; i < byteArr.length; i++) {
			byteArr[i] = result[i];
		}
		return result;
	}

	/**
	 * byte >> string
	 * 字节转可读字符串（char字符）
	 * @param byteArr
	 * @return
	 */
	public static String bytes2ReadStr(byte[] byteArr) {
		return new String(byteArr);
	}

	/**
	 * 左补充字节清除。
	 * -- 返回从左边起第一个非补充字节到最后。
	 * @param byteArr
	 * @param rep
	 * @return
	 */
	public static byte[] leftReplenishClean(byte[] byteArr,byte rep) {
		int i =0;
		int l = byteArr.length;
		for( ;i<l;i++) {
			if(byteArr[i]!=rep) {
				break;
			}
		}
		return getByteArr(byteArr, i, l-i);
	}

	/**
	 *  bytes >> HexString
	 * @param bArray
	 * @return
	 */
	public static String bytes2HexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2) {
				sb.append(0);
			}
			sb.append(sTemp.toUpperCase());
			sb.append(" ");
		}
		return sb.toString();
	}

	/**
	 *  bytes >> string
	 *  字节对应的数字（10进制）输出成字符串，多用于日志输出
	 * @param byteArr
	 * @return
	 */
	public static String bytes2String(byte[] byteArr) {
		StringBuffer result = new StringBuffer();
		for (byte b : byteArr) {
			result.append(b);
			result.append(" ");
		}
		return result.toString();
	}

	/**
	 *  HexString >> Bytes
	 * @param hexString
	 * @return
	 */
	public static byte[] hexString2Bytes(String hexString) {
		String chars = "0123456789ABCDEF";
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (chars.indexOf(hexChars[pos]) << 4 | chars
					.indexOf(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * string >> byte[n]
	 * @param param
	 * @return
	 */
	public static byte[] string2Bytes(String param) {
		return param.getBytes();
	}

	/**
	 *  str >> HexString
	 * @param str
	 * @return
	 */
	public static String string2HexString(String str) {
		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;
		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
		}
		return sb.toString();
	}

	/**
	 *  开头匹配
	 * @param source
	 * @param with
	 * @return
	 */
	public static boolean startWith(byte[] source, byte[] with) {
		if(source == null || with == null || source.length == 0 || with.length == 0 || with.length > source.length){
			return false;
		} else {
			for (int i = 0; i < with.length; i++) {
				if(with[i] != source[i]){
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 *  结尾匹配
	 * @param source
	 * @param with
	 * @return
	 */
	public static boolean endWith(byte[] source, byte[] with) {
		if(source == null || with == null || source.length == 0 || with.length == 0 || with.length > source.length){
			return false;
		} else {
			for (int i = 0; i < with.length; i++) {
				if(with[i] != source[source.length - with.length + i]){
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 *  读二进制流（防止死循环）
	 * 
	 * @param
	 * @return
	 * @throws IOException 
	 */
	public static byte[] readBytes(ServletInputStream is, int len) throws IOException {
		int readBytes = 0;
		byte[] bb = new byte[len];
		while (readBytes < len) {
			int read = is.read(bb, readBytes, len - readBytes);
			// 判断是不是读到了数据流的末尾 ，防止出现死循环。
			if (read == -1) {
				break;
			}
			readBytes += read;
		}
		return bb;
	}
	
	public static void main(String[] args) {
		byte[] a = new byte[3];
		a[0] = 0x00;
		a[1] = 0x01;
		a[2] = 0x31;
		
		
		
		String a1 = bytes2String(a);
		
		String a2 = new String(a);
		
		
		System.out.println(a1);
		System.out.println(a2);
	}
}
