package com.lht.oa.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: TODO
 * @author: lhtao
 * @date: 2022年10月14日 13:42
 */
@Data
public class Holidays implements Serializable {

    private static final long serialVersionUID = -1677539181011153614L;

    /**
     * 主键id
     */
    private Long id;
    /**
     * 出差申请单名称
     */
    private String holidaysName;
    /**
     * 出差天数
     */
    private Double num;
    /**
     * 预计开始时间
     */
    private Date beginDate;
    /**
     * 预计结束时间
     */
    private Date endDate;
    /**
     * 目的地
     */
    private String destination;
    /**
     * 出差事由
     */
    private String reson;
}
