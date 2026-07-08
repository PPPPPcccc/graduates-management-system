package com.kunshan.graduates.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 毕业生实体类，对应数据库表 graduate
 */
@Data
@TableName("graduate")
public class Graduate {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String idCard;
    private String name;
    private String education;
    private String school;
    private String graduationDate;
    private String major;
    private String phone;
    private String householdAddress;
    private String residenceAddress;
    private String investigator;
    private String investigationDate;
    private String investigationMethod;
    private String employmentStatus;
    private String employmentType;
    private String employmentLocation;
    private String unitName;
    private String unitNature;
    private String majorMatched;
    private String otherEmployment;
    private String specialEmployment;
    private String otherSituation;
    private String unemployedReason;
    private String employmentWillingness;
    private String provide1151Service;
    private String recommendUnitPosition;
    private String remarks;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    /**
     * 就业服务需求（来自 employment_service_demand 表）
     * JSON 反序列化用，不对应 graduate 表列
     */
    @TableField(exist = false)
    private List<String> employmentServiceNeeds;

    /**
     * 已接受就业服务（来自 accepted_employment_service 表）
     */
    @TableField(exist = false)
    private List<String> receivedServices;

    /**
     * 服务时间（来自 service_time 表）
     */
    @TableField(exist = false)
    private List<String> serviceTime;
}
