package io.github.aicyi.example.domain.entity.base;

import io.github.aicyi.commons.lang.BaseEntity;
import io.github.aicyi.commons.lang.type.BooleanType;
import io.github.aicyi.example.domain.type.GenderType;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户表:t_user
 */
public class User extends BaseEntity implements Serializable {
    /**
     * 主键:id
     */
    private Long id;

    /**
     * 年龄:age
     */
    private Integer age;

    /**
     * 身份证:id_card
     */
    private String idCard;

    /**
     * 手机号:mobile
     */
    private String mobile;

    /**
     * 性别，1:男；2：女；:gender_type
     */
    private GenderType genderType;

    /**
     * 生日:birthday
     */
    private LocalDate birthday;

    /**
     * 删除标记，0：未删除，1：已删除:deleted
     */
    private BooleanType deleted;

    /**
     * 版本:version
     */
    private Integer version;

    /**
     * 创建时间:create_time
     */
    private LocalDateTime createTime;

    /**
     * 更新时间:update_time
     */
    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public GenderType getGenderType() {
        return genderType;
    }

    public void setGenderType(GenderType genderType) {
        this.genderType = genderType;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public BooleanType getDeleted() {
        return deleted;
    }

    public void setDeleted(BooleanType deleted) {
        this.deleted = deleted;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}