package com.ccpd.excel.dao;

import com.ccpd.excel.model.UserAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by jondai on 2017/11/22.
 */
@Repository
public interface UserAttendanceDao extends JpaRepository<UserAttendance, String> {

    List<UserAttendance> findByAttendanceDateAndDeptName(Date attendanceDate, String deptName);

    List<UserAttendance> findByManufacturerAndDeptName(String manufacturer, String deptName);
    List<UserAttendance> findByDeptName( String deptName);
    List<UserAttendance> findByDeptNameOrderByManufacturer( String deptName);

    List<UserAttendance> findByDeptNameOrderByAttendanceDate( String deptName);


}

