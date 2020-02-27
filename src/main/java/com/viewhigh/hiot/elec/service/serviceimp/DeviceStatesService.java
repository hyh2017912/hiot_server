package com.viewhigh.hiot.elec.service.serviceimp;

import com.viewhigh.hiot.elec.common.RedisPrefix;
import com.viewhigh.hiot.elec.service.RedisService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName DeviceStatesUpdateJob
 * Description 更新维护设备状态，判断设备状态
 * @Author huyang
 * Date 2019/5/25 14:41
 **/
@Service
public class DeviceStatesService {
    private static final Logger logger = LogManager.getLogger(DeviceStatesService.class.getName());
    private static List<String> devices = new ArrayList<>();
    private static List<String> removeDevices = new ArrayList<>();

    private ReentrantLock rl = new ReentrantLock();

    @Autowired
    private ProtocolElecService protocolElecService;
    @Autowired
    private RedisService redisService;

    /**
     * 维护一个包含设备编号的list集合
     * @param device
     * @throws Exception
     */
    public void addDevice(String device) throws Exception {
        rl.tryLock(1, TimeUnit.SECONDS);
        try {
            if (!devices.contains(device)){
                logger.info("添加新的设备编号：{}",device);
                devices.add(device);
            }
        }finally {
            rl.unlock();
        }

    }

    /**
     * 定时任务，定时清除已离线设备的设备编号
     * @throws InterruptedException
     */
    @Scheduled(initialDelay=120000, fixedDelay=30000)
    public void removeDevice() throws InterruptedException {
        logger.info("启动定时任务：定时清除已离线设备的设备编号......");
        rl.tryLock(1,TimeUnit.SECONDS);
        try {
            for (String device: removeDevices) {
                devices.remove(device);
                logger.info("移除失效的设备编号：{}",device);
            }
            removeDevices.clear();
        }finally {
            rl.unlock();
        }
    }

    /**
     * 维护一个包含已离线设备的编号list
     * @param dev
     * @throws InterruptedException
     */
    public void addRemoveDev(String dev) throws InterruptedException {
        rl.tryLock(1,TimeUnit.SECONDS);
        try {
            removeDevices.add(dev);
            logger.info("添加一个将要移除的设备编号：{}",dev);
        }finally {
            rl.unlock();
        }
    }

    /**
     * 定时任务，定时检测设备是否离线
     * @throws InterruptedException
     */
    @Scheduled(initialDelay=60000, fixedDelay=30000)
    public void isDeviceOnline() throws InterruptedException {
        logger.info("启动定时任务：检测设备是否离线......");
        rl.lock();
        try {
            for (String device:devices) {
                String s = redisService.get(RedisPrefix.GLOBAL_PREFIX + RedisPrefix.DEVICE_ALIVE + device.toUpperCase());
                if (s == null){
                    // 设备不在线，做处理，修改数据库设备状态，并从内存中移除
                    logger.info("更新设备{}状态为{}",device,(byte)0x01);
                    protocolElecService.updateDeviceState(device,(byte)0x01);
                    addRemoveDev(device);
                }
            }
        }finally {
            rl.unlock();
        }
    }


}
