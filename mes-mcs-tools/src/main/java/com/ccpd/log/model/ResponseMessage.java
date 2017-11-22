package com.ccpd.log.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by jondai on 2017/11/7.
 * 应答消息
 */
@Entity
//@Table(name = "MCS-LOG-RESPONSEMESSAGE")
public class ResponseMessage {

    /**
     * 消息事物 ID 与请求消息的 Transaction ID 一致
     */
    @Id
    private String transcationid;


    /**
     * 消息处理结果，Success/Fail
     */
    @Column
    private String result;

    /**
     * 处理结果，只有当 RESULTCODE=FAIL 时，才会有值
     */
    @Column
    private String resultcode;

    /**
     * 错误消息，只有当 RESULTCODE=FAIL 时，才会有值，可能为空值
     */
    @Column
    private String resultmessage;

    public String getTranscationid() {
        return transcationid;
    }

    public void setTranscationid(String transcationid) {
        this.transcationid = transcationid;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultcode() {
        return resultcode;
    }

    public void setResultcode(String resultcode) {
        this.resultcode = resultcode;
    }

    public String getResultmessage() {
        return resultmessage;
    }

    public void setResultmessage(String resultmessage) {
        this.resultmessage = resultmessage;
    }
}
