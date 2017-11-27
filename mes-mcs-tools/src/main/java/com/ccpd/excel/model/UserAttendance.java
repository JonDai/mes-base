package com.ccpd.excel.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by jondai on 2017/11/22.
 * 自定义编号	姓名	日期	对应时段	签到时间	签到地点	签退时间	签退地点	迟到时间	早退时间	是否旷工	工作时间	部门	工作时间2
 * 人员出勤模型
 */
@Entity
public class UserAttendance {
    /**
     * 唯一主键标识
     */
    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private String uuid;

    /**
     * 人员ID
     */
    @Column
    private String userId;

    /**
     * 人员姓名
     */
    @Column
    private String userName;

    /**
     * 考勤日期
     */
    @Column
    private Date attendanceDate;

    /**
     * 对应时段
     */
    @Column
    private String timeDuan;

    /**
     * 签到时间
     */
    @Column
    private String signTime;

    /**
     * 签到地址
     */
    @Column
    private String signAddress;

    /**
     * 签退时间
     */
    @Column
    private String signOutTime;

    /**
     * 签退地点
     */
    @Column
    private String signOutAddress;

    /**
     * 迟到时间
     */
    @Column
    private String lagTime;

    /**
     * 早退时间
     */
    @Column
    private String leaveEarlyTime;

    /**
     * 是否旷工
     */
    @Column
    private String isAbsenteeism;


    /**
     * 部门
     */
    @Column
    private String deptName;

    /**
     * 厂商
     */
    @Column
    private String manufacturer;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getTimeDuan() {
        return timeDuan;
    }

    public void setTimeDuan(String timeDuan) {
        this.timeDuan = timeDuan;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }


    public String getSignAddress() {
        return signAddress;
    }

    public void setSignAddress(String signAddress) {
        this.signAddress = signAddress;
    }


    public String getSignOutAddress() {
        return signOutAddress;
    }

    public void setSignOutAddress(String signOutAddress) {
        this.signOutAddress = signOutAddress;
    }


    public String getIsAbsenteeism() {
        return isAbsenteeism;
    }

    public void setIsAbsenteeism(String isAbsenteeism) {
        this.isAbsenteeism = isAbsenteeism;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getSignTime() {
        return signTime;
    }

    public void setSignTime(String signTime) {
        this.signTime = signTime;
    }

    public String getSignOutTime() {
        return signOutTime;
    }

    public void setSignOutTime(String signOutTime) {
        this.signOutTime = signOutTime;
    }

    public String getLagTime() {
        return lagTime;
    }

    public void setLagTime(String lagTime) {
        this.lagTime = lagTime;
    }

    public String getLeaveEarlyTime() {
        return leaveEarlyTime;
    }

    public void setLeaveEarlyTime(String leaveEarlyTime) {
        this.leaveEarlyTime = leaveEarlyTime;
    }

    @Override
    public String toString() {
        return "UserAttendance{" +
                "uuid='" + uuid + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", attendanceDate=" + attendanceDate +
                ", timeDuan='" + timeDuan + '\'' +
                ", signTime=" + signTime +
                ", signAddress='" + signAddress + '\'' +
                ", signOutTime=" + signOutTime +
                ", signOutAddress='" + signOutAddress + '\'' +
                ", lagTime=" + lagTime +
                ", leaveEarlyTime=" + leaveEarlyTime +
                ", isAbsenteeism='" + isAbsenteeism + '\'' +
                ", deptName='" + deptName + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                '}';
    }
}
