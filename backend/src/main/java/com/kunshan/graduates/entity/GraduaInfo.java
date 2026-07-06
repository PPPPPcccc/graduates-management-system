package com.kunshan.graduates.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 毕业生信息实体类
 * 对应数据库表 graduate_info
 */
@Data
@TableName("graduate_info")
public class GraduaInfo {

    /** 身份证号（主键） */
    @TableId(type = IdType.INPUT)
    private String idCard;

    /** 姓名 */
    private String name;

    /** 学历：专科/本科/硕士 */
    private String educationLevel;

    /** 毕业院校 */
    private String graduationSchool;

    /** 毕业日期 */
    private LocalDate graduationDate;

    /** 专业 */
    private String major;

    /** 联系电话 */
    private String phone;

    /** 户籍地址 */
    private String householdAddress;

    /** 现居住地（省市） */
    private String currentAddressProvince;

    /** 现居住详细地址 */
    private String currentAddressDetail;

    /** 就业状态：employed/unemployed */
    private String employmentStatus;

    /** 单位名称（仅已就业） */
    private String companyName;

    /** 单位性质：事业单位/国企/个体/社会组织/其他 */
    private String companyNature;

    /** 行业类型 */
    private String industryType;

    /** 产业类别 */
    private String industryCategory;

    /** 就业地点 */
    private String workLocation;

    /** 其他就业 */
    private String otherEmployment;

    /** 未就业原因：正在求职/拟升学/拟应征入伍/拟创业/暂无就业意愿 */
    private String unemployedReason;

    /** 是否核发登记失业证 */
    private Boolean hasUnemploymentCert;

    /** 登记失业证编号 */
    private String unemploymentCertNo;

    /** 享受服务类型（多选用逗号分隔） */
    private String servicesReceived;

    /** 是否享受免费服务 */
    private Boolean freeService;

    /** 是否接受援助 */
    private Boolean aidReceived;

    /** 调查日期 */
    private LocalDate surveyDate;

    /** 调查方式 */
    private String surveyMethod;

    /** 备注 */
    private String remark;

    /** 登记时间（系统自动填） */
    private LocalDateTime registeredAt;

    /** 最后修改时间（系统自动更新） */
    private LocalDateTime updatedAt;
}