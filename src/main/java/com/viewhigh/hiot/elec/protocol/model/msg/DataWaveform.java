package com.viewhigh.hiot.elec.protocol.model.msg;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName DataWaveform
 * Description 波形
 * @Author huyang
 * Date 2019/5/23 16:05
 **/
public class DataWaveform implements Serializable {
    private int timeDifferent; // 时差
    private int waveFormDataCount;//波形数据个数
    private List<Integer> waveFormValue;

    public int getTimeDifferent() {
        return timeDifferent;
    }

    public void setTimeDifferent(int timeDifferent) {
        this.timeDifferent = timeDifferent;
    }

    public List<Integer> getWaveFormValue() {
        return waveFormValue;
    }

    public void setWaveFormValue(List<Integer> waveFormValue) {
        this.waveFormValue = waveFormValue;
    }

    public int getWaveFormDataCount() {
        return waveFormDataCount;
    }

    public void setWaveFormDataCount(int waveFormDataCount) {
        this.waveFormDataCount = waveFormDataCount;
    }

    @Override
    public String toString() {
        return "DataWaveform{" +
                "timeDifferent=" + timeDifferent +
                ", waveFormDataCount=" + waveFormDataCount +
                ", waveFormValue=" + waveFormValue +
                '}';
    }
}

