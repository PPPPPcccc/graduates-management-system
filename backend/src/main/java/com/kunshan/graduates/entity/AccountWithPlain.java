// entity/AccountWithPlain.java
package com.kunshan.graduates.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AccountWithPlain {
    private Integer id;
    private String username;
    private String name;
    private String role;
    private Boolean enabled;
    private String password;
    private LocalDateTime createdAt;
}