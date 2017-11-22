package com.ccpd.log.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by jondai on 2017/11/7.
 * 请求消息
 */
@Entity
//@Table(name = "MCS-LOG-REQUESTMESSAGE")
public class RequestMessage {


    /**
     * 消息事物 ID，由 Request 发送方生成，建议采用 UUID 或其它方法确保唯一性，
     * Response 应答消息的事物 ID 与此保持一致。
     */
    @Id
    private String transcationid;

    /**
     * 接口的名称
     */
    @Column
    private String messagename;

    /**
     * MES 侧对厂区(Shop)的编号
     */
    @Column
    private String orgrrn;


    /**
     * 厂区的名称
     */
    @Column
    private String orgname;

    /**
     * 用户名称，MCS 系统使用固定值 MCS
     */
    @Column
    private String username;

    public String getTranscationid() {
        return transcationid;
    }

    public void setTranscationid(String transcationid) {
        this.transcationid = transcationid;
    }

    public String getMessagename() {
        return messagename;
    }

    public void setMessagename(String messagename) {
        this.messagename = messagename;
    }

    public String getOrgrrn() {
        return orgrrn;
    }

    public void setOrgrrn(String orgrrn) {
        this.orgrrn = orgrrn;
    }

    public String getOrgname() {
        return orgname;
    }

    public void setOrgname(String orgname) {
        this.orgname = orgname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    @Override
    public String toString() {
        return "RequestMessage{" +
                "transcationid='" + transcationid + '\'' +
                ", messagename='" + messagename + '\'' +
                ", orgrrn='" + orgrrn + '\'' +
                ", orgname='" + orgname + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
