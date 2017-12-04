package com.shenchu.app.vo;

/**
 * Created by admin on 2017/11/21.
 */

public class RecvData extends BaseBean {
    private int command;
    private int length;
    private byte data[];

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
