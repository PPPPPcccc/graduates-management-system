// entity/AuxItem.java
// 关联表查询结果的中间映射类
package com.kunshan.graduates.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuxItem {
    private Integer graduateId;
    private String value;  // demand / service / service_date
}
