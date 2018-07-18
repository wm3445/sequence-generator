package com.sequence.generator.dto;

/**
 * @author wangmeng
 */
public class SeqGen {

    private int id;
    private String ip;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public SeqGen(int id, String ip){
        this.id = id;
        this.ip = ip;
    }
}
