package com.shenchu.app.vo;

/**
 * Created by admin on 2017/11/21.
 */

public class Message extends BaseBean {
    private String name;
    private byte content[];
    private int type;//0.自已 1.服务器
    private int command;//0.消息类型
    private long time;//时间戳

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }
}
