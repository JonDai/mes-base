package com.ccpd.excel.service;

import org.apache.poi.ss.usermodel.Workbook;

/**
 * Created by jondai on 2017/11/22.
 */
@FunctionalInterface
public interface ExcelProcessable {
    /**
     * 实现如何处理文档
     */
    void process(Workbook workbook) throws Exception;
}
