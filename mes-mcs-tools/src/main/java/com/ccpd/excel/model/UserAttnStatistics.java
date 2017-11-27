package com.ccpd.excel.model;

import java.util.List;
import java.util.Map;

/**
 * Created by jondai on 2017/11/23.
 * 人员考勤分析模型对象
 */
public class UserAttnStatistics {

    /**
     * 厂商
     */
    private String manufacturer;

    /**
     * 负责人
     */
    private String personInCharge;

    /**
     * 办卡人数
     */
    private String NumberOfCard;

    /**
     * 入厂人数
     */
    private String NumberOfPerson;

    private Double totalHours;

    private List<Average> averages;

    private Map<String,Map<String,String>> rowAverages;

    public UserAttnStatistics() {
        this.totalHours = 0.0;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getPersonInCharge() {
        return personInCharge;
    }

    public void setPersonInCharge(String personInCharge) {
        this.personInCharge = personInCharge;
    }

    public String getNumberOfCard() {
        return NumberOfCard;
    }

    public void setNumberOfCard(String numberOfCard) {
        NumberOfCard = numberOfCard;
    }

    public String getNumberOfPerson() {
        return NumberOfPerson;
    }

    public void setNumberOfPerson(String numberOfPerson) {
        NumberOfPerson = numberOfPerson;
    }

    public List<Average> getAverages() {
        return averages;
    }

    public void setAverages(List<Average> averages) {
        this.averages = averages;
    }

    @Override
    public String toString() {
        return "UserAttnStatistics{" +
                "manufacturer='" + manufacturer + '\'' +
                ", personInCharge='" + personInCharge + '\'' +
                ", NumberOfCard=" + NumberOfCard +
                ", NumberOfPerson=" + NumberOfPerson +
                ", averages=" + averages +
                '}';
    }

    public Double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Double totalHours) {
        this.totalHours = totalHours;
    }

    public Map<String, Map<String, String>> getRowAverages() {
        return rowAverages;
    }

    public void setRowAverages(Map<String, Map<String, String>> rowAverages) {
        this.rowAverages = rowAverages;
    }
}
