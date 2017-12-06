package com.ccpd.excel.model;

import java.util.Date;

/**
 * Created by jondai on 2017/12/5.
 * 原始数据对象
 */
public class RawData {


    /**
     * 厂商
     */
    private String manufacturer;

    /**
     * 人员ID
     */
    private String userId;

    /**
     * 人员姓名
     */
    private String userName;

    /**
     * 考勤日期
     */
    private Date attendanceDate;

    private String signDate;
    private String signTime;

    /**
     * 打卡区域
     */
    private String signArea;

    /**
     * 部门名称
     */
    private String deptName;


    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(Date attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getSignArea() {
        return signArea;
    }

    public void setSignArea(String signArea) {
        this.signArea = signArea;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }


    public String getSignDate() {
        return signDate;
    }

    public void setSignDate(String signDate) {
        this.signDate = signDate;
    }


    public String getSignTime() {
        return signTime;
    }

    public void setSignTime(String signTime) {
        this.signTime = signTime;
    }
}
