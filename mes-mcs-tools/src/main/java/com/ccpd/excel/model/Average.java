package com.ccpd.excel.model;

import java.util.Date;

/**
 * Created by jondai on 2017/11/23.
 */
public class Average {

    public Average() {
        this.numberOfOperators = 0;
        this.workingHours = 0.0;
        this.averageWorkingHours = 0.0;
    }

    private Date date;
    /**
     * 作业人数
     */
    private Integer numberOfOperators;

    /**
     * 作业时数
     */
    private Double workingHours;

    private String workingHoursString;

    /**
     * 平均时数
     */
    private Double averageWorkingHours;
    private String averageWorkingHoursString;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getNumberOfOperators() {
        return numberOfOperators;
    }

    public void setNumberOfOperators(Integer numberOfOperators) {
        this.numberOfOperators = numberOfOperators;
    }

    public Double getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(Double workingHours) {
        this.workingHours = workingHours;
    }

    public Double getAverageWorkingHours() {
        return averageWorkingHours;
    }

    public void setAverageWorkingHours(Double averageWorkingHours) {
        this.averageWorkingHours = averageWorkingHours;
    }

    public void numberOfOperatorAutoIncrement(){
        this.numberOfOperators++;
    }

    public  void addWorkingHours(double workingHours){
        this.workingHours += workingHours;
    }

    public String getWorkingHoursString() {
        return workingHoursString;
    }

    public void setWorkingHoursString(String workingHoursString) {
        this.workingHoursString = workingHoursString;
    }

    public String getAverageWorkingHoursString() {
        return averageWorkingHoursString;
    }

    public void setAverageWorkingHoursString(String averageWorkingHoursString) {
        this.averageWorkingHoursString = averageWorkingHoursString;
    }
}
