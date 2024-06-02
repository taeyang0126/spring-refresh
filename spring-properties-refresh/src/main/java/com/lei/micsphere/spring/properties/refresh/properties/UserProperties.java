package com.lei.micsphere.spring.properties.refresh.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * UserProperties
 * </p>
 *
 * @author 伍磊
 */
@ConfigurationProperties(prefix = "user")
public class UserProperties {

    private String username;

    private Integer age;

    private String address;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
