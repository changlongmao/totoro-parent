package org.totoro.generator.javabean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 员工信息出国展示类
 *
 * @author ChangLF 2023/07/28
 */
@Data
@Accessors(chain = true)
public class HrpAbroadVO {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 用户编码
     */
    private Integer userCode;

    /**
     * 所去国家地区
     */
    private String country;

    /**
     * 出国出境时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date abroadDate;

    /**
     * 回国时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date repatriateDate;

    /**
     * 出国出境任务
     */
    private String abroadTask;

    /**
     * 团组名称
     */
    private String termName;

    /**
     * 在团组中担任职务
     */
    private String termDuty;

    /**
     * 审批单位
     */
    private String passCompany;

    /**
     * 审批文号
     */
    private String passNumber;

    /**
     * 派出单位
     */
    private String expediteCompany;

    /**
     * 经费来源
     */
    private String moneyOrigin;

    /**
     * 备注
     */
    private String remark;

    /**
     * 附件
     */
    private String folderId;

    /**
     * 排序
     */
    private Integer seq;

    /**
     * 删除标识
     */
    private Byte delete;

    /**
     * 创建人用户编码
     */
    private Integer createUserCode;

    /**
     * 创建人部门编码
     */
    private String createDeptCode;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 修改人用户编码
     */
    private Integer modifyUserCode;

    /**
     * 修改人部门编码
     */
    private String modifyDeptCode;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modifyTime;

    /**
     * OrgCode
     */
    private Integer orgCode;

}
