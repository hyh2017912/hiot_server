package com.viewhigh.hiot.elec.service.serviceimp;

import com.alibaba.fastjson.JSONObject;
import com.viewhigh.hiot.elec.common.RedisPrefix;
import com.viewhigh.hiot.elec.protocol.model.msg.*;
import com.viewhigh.hiot.elec.support.utils.ByteET;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @ClassName DataAnalysisService
 * Description 解析消息数据区
 * @Author huyang
 * Date 2019/5/17 16:36
 **/
@Service
public class DataAnalysisService {
    private static final Logger logger = LogManager.getLogger(DataAnalysisService.class.getName());
    // 定义的数据区“长度”，用来校验数据区是否合法
    //频发数据长度
    private static final Integer DATA_FREQUENT_LEN = 46;
    //设备状态数据长度
    private static final Integer DATA_DEVICESTATE_LEN = 39;
    //意外断电数据长度
    private static final Integer DATA_UNEXPECTEDPOWEROFF_LEN = 40;
    //基站标识数据长度
    private static final Integer DATA_BASEMARK_LEN = 54;
    //统计数据长度（不包含使用明细）
    private static final Integer DATA_STATISTICSTIMES_LEN = 44;
    //波形个数（电流电压目前相同）
    private static final Integer DATA_WAVEFORM_ALERT_COUNT = 10; // 0X0a
    //波形数据个数（在一个波形内采集的数据点个数除以10）
    private static final Integer DATA_WAVEFORM_DATA_COUNT = 100;
    //电压电流超限数据长度（不含波形）
    private static final Integer DATA_ALERT_LENGTH = 50;

    @Autowired
    private ProtocolElecService protocolElecService;
    @Autowired
    private DeviceStatesService deviceStatesService;

    /**
     * 数据处理：调用这个方法前，数据必须确认可用，已经通过了起始符，长度，校验位，鉴权验证，且非心跳鉴权消息
     * @param msg  通信数据
     * @param channelId 通道id
     */
    public void msgDealAndWriterToDB(byte[] msg, String channelId) throws Exception {
        // 截取数据区
        short length = ByteET.bytes2Short(ByteET.getByteArr(msg,12,2));
        byte[] data = ByteET.getByteArr(msg,14,length);
        // todo 更新设备状态
        // 0	设备编号	string	30	设备编号，不足30位左侧补充0x00（注：字符”0”=0x30）
        byte[] device = ByteET.getByteArr(data, 0, 30);
        String dev = ByteET.bytes2ReadStr(ByteET.leftReplenishClean(device, (byte) 0x00));
        // 维护一个包含所有设备编号的list
        deviceStatesService.addDevice(dev);
        // 更新设备状态
        protocolElecService.updateDeviceState(dev,(byte)0x03);
        //处理数据 处理业务数据
        switch (msg[11]) {
            case 0x10:
                // 跳转频发数据处理
                this.msgFrequentData(data,length, (byte) 0x10);
                break;
            case 0x11:
                // 跳转设备状态更新
                this.msgDeviceStateUpdate(data,length, (byte) 0x11);
                break;
            case 0x12:
                // 跳转意外断电数据
                this.msgUnexpectedPowerOff(data,length, (byte) 0x12);
                break;
            case 0x13:
                // 跳转电流超限
                this.msgDataCurAlert(data,length, (byte) 0x13);
                break;
            case 0x14:
                // 跳转电压超限
                this.msgDataVolAlert(data,length, (byte) 0x14);
                break;
            case 0x15:
                // 跳转统计数据
                this.msgStatisticsTimes(data,length, (byte) 0x15);
                break;
            case 0x16:
                // 跳转配置数据
                this.msgConfigData(data,length, (byte) 0x16);
                break;
            }
    }

    /**
     * 电压超限处理
     * @param data 数据区
     * @param length 数据区长度
     * @param msgFlag
     */
    private void msgDataVolAlert(byte[] data, short length, byte msgFlag) {
        if((length - DATA_WAVEFORM_ALERT_COUNT * (DATA_WAVEFORM_DATA_COUNT + 2)) == DATA_ALERT_LENGTH) {
            logger.info("电压超限数据非法");
            return;
        }
        logger.info("开始处理电压超限数据......");
        DataVolAlert dataVolAlert = new DataVolAlert();
        if (data[46]!=0xFF && data[46]!=0xFE && data[47] != 0xFF && data[47] != 0xFE){
            dataVolAlert.setVolAlert(ByteET.bytes2Short(ByteET.getByteArr(data, 46, 2)));
        }else {
            dataVolAlert.setVolAlert(-1);
        }
        //开始时间
        long sTime = ByteET.bytes2Long(ByteET.getByteArr(data, 30, 8));
        dataVolAlert.setsTime(new Date(sTime));
        //结束时间
        long eTime = ByteET.bytes2Long(ByteET.getByteArr(data, 38, 8));
        dataVolAlert.seteTime(new Date(eTime));
        // 超限类型
        dataVolAlert.setVolAlert(data[48]);
        //波形个数
        byte waveCount = data[49];
        dataVolAlert.setCount(waveCount);
        // 超限电流波形记录
        List<DataWaveform> listWF = this.setWaveFormValue(data,waveCount);
        dataVolAlert.setListWF(listWF);
        logger.info("电压超限保存为数据对象:{}",dataVolAlert.toString());
        protocolElecService.saveDataToReids(dataVolAlert,dataVolAlert.getDevice(),dataVolAlert.getTime(),msgFlag);
    }

    /**
     * 电流超限处理
     * @param data  数据区
     * @param length  数据区长度
     * @param msgFlag
     * @throws Exception
     */
    private void msgDataCurAlert(byte[] data, short length, byte msgFlag) throws Exception {
       if((length - DATA_WAVEFORM_ALERT_COUNT * (DATA_WAVEFORM_DATA_COUNT + 2)) == DATA_ALERT_LENGTH) {
            logger.info("电流超限数据非法");
            return;
        }
        logger.info("开始处理电流超限数据......");
        DataCurAlert dataCurAlert = this.setDeviceAndTime(DataCurAlert.class,data);
        if (data[46]!=0xFF && data[46]!=0xFE && data[47] != 0xFF && data[47] != 0xFE){
            dataCurAlert.setCurAlert(ByteET.bytes2Short(ByteET.getByteArr(data, 46, 2)));
        }else {
            dataCurAlert.setCurAlert(-1);
        }
        //开始时间
        long sTime = ByteET.bytes2Long(ByteET.getByteArr(data, 30, 8));
        dataCurAlert.setsTime(new Date(sTime));
        //结束时间
        long eTime = ByteET.bytes2Long(ByteET.getByteArr(data, 38, 8));
        dataCurAlert.seteTime(new Date(eTime));
        // 超限类型
        dataCurAlert.setCurAlert(data[48]);
        //波形个数
        byte waveCount = data[49];
        dataCurAlert.setCount(waveCount);
        // 超限电流波形记录
        List<DataWaveform> listWF = this.setWaveFormValue(data,waveCount);
        dataCurAlert.setListWF(listWF);
        logger.info("电流超限保存为数据对象:{}",dataCurAlert.toString());
        protocolElecService.saveDataToReids(dataCurAlert,dataCurAlert.getDevice(),dataCurAlert.getTime(),msgFlag);
    }

    /**
     * 统计数据处理
     * @param data 数据区
     * @param length 数据区长度
     * @param msgFlag
     * @throws Exception
     */
    private void msgStatisticsTimes(byte[] data, short length, byte msgFlag) throws Exception {
        if((length - DATA_STATISTICSTIMES_LEN) % 12 != 0) {
            logger.info("数据非法");
            return;
        }
        logger.info("开始处理统计数据......");
        DataStatisticsTimes statisticsTimes = this.setDeviceAndTime(DataStatisticsTimes.class,data);
        // 累计电量
        statisticsTimes.setCumulativePower(ByteET.bytes2Short(ByteET.getByteArr(data, 38, 4)));
        // 使用次数
        short us = ByteET.bytes2Short(ByteET.getByteArr(data, 42, 2));
        statisticsTimes.setUseTimes(us);
        // 使用明细
        byte[] useDetail = ByteET.getByteArr(data, 44, 12 * us);
        List<DataUseDetail> listSta = new ArrayList<>();
        for (int i = 0; i <us; i++) {
            DataUseDetail dataUseDetail = new DataUseDetail();
            long timeStart = ByteET.bytes2Long(ByteET.getByteArr(useDetail, 12 * i, 8));
            short useTime = ByteET.bytes2Short(ByteET.getByteArr(useDetail, 12 * i + 8, 4));
            dataUseDetail.setTimeStart(timeStart);
            dataUseDetail.setUseTime(useTime);
            listSta.add(dataUseDetail);
        }
        statisticsTimes.setListSta(listSta);
        logger.info("统计数据保存为对象:{}",statisticsTimes.toString());
        // 入库
        protocolElecService.saveDataToReids(statisticsTimes,statisticsTimes.getDevice(),statisticsTimes.getTime(),msgFlag);
    }

    /**
     * 配置数据
     * @param data 数据区
     * @param length 数据区长度
     * @param msgFlag
     * @throws Exception
     */
    private void msgConfigData(byte[] data, short length, byte msgFlag) throws Exception {
        if(length - DATA_BASEMARK_LEN != 0) {
            logger.info("数据非法");
            return;
        }
        logger.info("开始处理配置数据数据......");
        DataConfigData configData = this.setDeviceAndTime(DataConfigData.class,data);
        if(data[38]!=0xFF && data[38]!=0xFE && data[39] != 0xFF && data[39] != 0xFE) {
            configData.setCurHigh(ByteET.bytes2Short(ByteET.getByteArr(data, 38, 2)));
        }else {
            configData.setCurHigh(-1);
        }
        if(data[40]!=0xFF && data[40]!=0xFE && data[41] != 0xFF && data[41] != 0xFE) {
            configData.setCurLow(ByteET.bytes2Short(ByteET.getByteArr(data, 40, 2)));
        }else {
            configData.setCurLow(-1);
        }
        if(data[42]!=0xFF && data[42]!=0xFE && data[43] != 0xFF && data[43] != 0xFE) {
            configData.setVolHigh(ByteET.bytes2Short(ByteET.getByteArr(data, 42, 2)));
        }else {
            configData.setVolHigh(-1);
        }
        if(data[44]!=0xFF && data[44]!=0xFE && data[45] != 0xFF && data[45] != 0xFE) {
            configData.setVolLow(ByteET.bytes2Short(ByteET.getByteArr(data, 44, 2)));
        }else {
            configData.setVolLow(-1);
        }
        if(data[46]!=0xFF && data[46]!=0xFE && data[47] != 0xFF && data[47] != 0xFE &&
                data[48]!=0xFF && data[48]!=0xFE && data[49] != 0xFF && data[49] != 0xFE) {
            configData.setBaseMark(ByteET.bytes2Short(ByteET.getByteArr(data, 46, 4)));
        }else {
            configData.setBaseMark(-1);
        }
        if(data[50]!=0xFF && data[50]!=0xFE && data[51] != 0xFF && data[51] != 0xFE &&
                data[52]!=0xFF && data[52]!=0xFE && data[53] != 0xFF && data[53] != 0xFE) {
            configData.setBaseArea(ByteET.bytes2Short(ByteET.getByteArr(data, 50, 4)));
        }else {
            configData.setBaseArea(-1);
        }
//        configData.setBaseMark(ByteET.bytes2Short(ByteET.getByteArr(data, 46, 4)));
        logger.info("配置数据保存为对象:{}",configData.toString());
        protocolElecService.saveDataToReids(configData,configData.getDevice(),configData.getTime(),msgFlag);
    }


    /**
     * 意外断电处理
     * @param data
     * @param len
     * @param msgFlag
     */
    private void msgUnexpectedPowerOff(byte[] data, short len, byte msgFlag) throws Exception {
        if(len - DATA_UNEXPECTEDPOWEROFF_LEN != 0) {
            logger.info("数据非法");
            return;
        }
        logger.info("开始处理意外断电数据......");
        DataUnexpectedPowerOff powerOff = this.setDeviceAndTime(DataUnexpectedPowerOff.class,data);
        if (data[38] != 0xFF && data[38] != 0xFE && data[39] != 0xFF && data[39] != 0xFE){
            powerOff.setTimes(ByteET.bytes2Short(ByteET.getByteArr(data, 38, 2)));
        }
        logger.info("意外断电保存为对象:{}",powerOff.toString());
        // 入库
        protocolElecService.saveDataToReids(powerOff,powerOff.getDevice(),powerOff.getTime(),msgFlag);
    }

    /**
     *  处理设备状态更新
     * @param data 数据区
     * @param len 数据区长度
     * @param msgFlag
     * @throws Exception
     */
    private void msgDeviceStateUpdate(byte[] data, short len, byte msgFlag) throws Exception {
        if(len - DATA_DEVICESTATE_LEN != 0) {
            logger.info("数据非法");
            return;
        }
        logger.info("开始处理设备更新数据......");
        DataDeviceState deviceState = this.setDeviceAndTime(DataDeviceState.class,data);
        // 设备状态 0x01:关机；0x02：待机；0x03：工作 0xFF表示无效；0xFE表示错误。
        if (data[38] != 0xFF && data[38] != 0xFE){
            deviceState.setState(data[38]);
        }
        logger.info("设备状态保存为对象:{}",deviceState.toString());
        // 写入库
        protocolElecService.saveDataToReids(deviceState,deviceState.getDevice(),deviceState.getTime(),msgFlag);
    }

    /**
     * 处理频发数据
     * @param data 数据区
     * @param len 数据区长度
     * @param msgFlag
     */
    private void msgFrequentData(byte[] data, short len, byte msgFlag) throws Exception {
        if(len- DATA_FREQUENT_LEN != 0) {
            logger.info("数据非法");
            return;
        }
        logger.info("开始处理频发数据......");
        DataFrequentData frequentData = this.setDeviceAndTime(DataFrequentData.class,data);
        //38	电流	short	2	精度1，单位mA  0xFFFF表示无效；0xFFFE表示错误。
        if(data[38]!=0xFF && data[38]!=0xFE && data[39] != 0xFF && data[39] != 0xFE) {
            frequentData.setCur(ByteET.bytes2Short(ByteET.getByteArr(data, 38, 2)));
        }else {
            frequentData.setCur(-1);
        }

        //40	电压	short	2	精度0.1，单位V，电压=实际电压*100xFFFF表示无效；0xFFFE表示错误。
        if(data[40]!=0xFF && data[40]!=0xFE && data[41] != 0xFF && data[41] != 0xFE) {
            frequentData.setVol(ByteET.bytes2Short(ByteET.getByteArr(data, 40, 2)));
        }else {
            frequentData.setVol(-1);
        }

        //42	功率	short	2	精度0.1，单位w，功率=实际功率*10 0xFFFF表示无效；0xFFFE表示错误。
        if(data[42]!=0xFF && data[42]!=0xFE && data[43] != 0xFF && data[43] != 0xFE) {
            frequentData.setPwr(ByteET.bytes2Short(ByteET.getByteArr(data, 42, 2)));
        }else {
            frequentData.setPwr(-1);
        }

        //44	电流超限	byte	1	0x00：正常；0x01：低于最低电流；0x11：高于最高电流。
        frequentData.setCurAlert(data[44]);

        //45	电压超限	byte	1	0x00：正常；0x01：低于最低电压；0x11：高于最高电压。
        frequentData.setVolAlert(data[45]);
        logger.info("频发数据保存为对象:{}",frequentData.toString());
        // 写入库
        protocolElecService.saveDataToReids(frequentData,frequentData.getDevice(),frequentData.getTime(),msgFlag);
    }

    /**
     * 电压电流超限的波形属性性是类似的，代码复用
     * @param data 数据区
     * @param waveCount 波形个数
     * @return
     */
    private List<DataWaveform> setWaveFormValue(byte[] data, byte waveCount) {
        List<DataWaveform> listWF = new ArrayList<>();
        byte[] waveForm = ByteET.getByteArr(data, 50, (DATA_WAVEFORM_DATA_COUNT + 2) * waveCount);
        for (int i = 0; i < waveCount; i++) {
            DataWaveform dataWaveform = new DataWaveform();
            // 时差
            byte timeDiff = waveForm[(DATA_WAVEFORM_DATA_COUNT + 2)  * i];
            dataWaveform.setTimeDifferent(timeDiff);
            //波形数据个数
            byte waveFormDataCount = waveForm[(DATA_WAVEFORM_DATA_COUNT + 2)  * i + 1];
            dataWaveform.setWaveFormDataCount(waveFormDataCount);
            // 电流值
            byte[] waveValue = ByteET.getByteArr(waveForm, (DATA_WAVEFORM_DATA_COUNT + 2) * i + 2, DATA_WAVEFORM_DATA_COUNT);
            List<Integer> waveFromValue = new ArrayList<>();
            for (int j = 0; j < DATA_WAVEFORM_DATA_COUNT; j++) {
                waveFromValue.add(ByteET.bytes2int(ByteET.getByteArr(waveValue,j,j+1)));
            }
            dataWaveform.setWaveFormValue(waveFromValue);
            listWF.add(dataWaveform);
        }
        return listWF;
    }

    /**
     * 因为每个方法中都有设备编号和时间戳的set属性，重复太多，所以这里用反射使代码复用，但也有了限制，就是实体类中的设备
     * 编号属性必须为String device；时间戳属性必须为Date time
     * @param c 实体类的class
     * @param data 数据区
     * @param <T> 实体类型
     * @return 该实体类对象
     * @throws Exception
     */
    private <T> T setDeviceAndTime(Class<T> c,byte[] data) throws Exception {
        // 0	设备编号	string	30	设备编号，不足30位左侧补充0x00（注：字符”0”=0x30）
        byte[] device = ByteET.getByteArr(data, 0, 30);
        //30	时间戳	long	8	采集数据时间戳
        long time = ByteET.bytes2Long(ByteET.getByteArr(data, 30, 8));
        T t = c.newInstance();
        Method setTime = c.getMethod("setTime", Date.class);
        setTime.invoke(t,new Date(time));
        Method setDevice = c.getMethod("setDevice", String.class);
        setDevice.invoke(t,ByteET.bytes2ReadStr(ByteET.leftReplenishClean(device,(byte) 0x00)));
        return t;
    }

    public static void main(String[] strings){
        System.out.println(0xff);
        System.out.println((byte)0xff);
        System.out.println(0xfe);
        System.out.println((byte)0xfe);
    }
}
