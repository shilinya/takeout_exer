package com.sky.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String name;
    @JsonIgnore
    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;

    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time",fill= FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(value = "create_user",fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(value = "update_user",fill= FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
