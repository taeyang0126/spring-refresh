package com.lei.micsphere.spring.properties.refresh;

import com.lei.micsphere.spring.properties.refresh.properties.UserProperties;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.ConfigurationPropertiesRebinderAutoConfiguration;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * <p>
 * UserPropertiesRefreshTest
 * </p>
 *
 * @author 伍磊
 */
@SpringBootTest
@TestPropertySource(properties = {
        "user.username=塔尔",
        "user.age=20",
        "user.address=湖南省长沙市岳麓区茶子山"
})
public class UserPropertiesRefreshTest {

    @Autowired
    private UserProperties userProperties;

    @Autowired
    private Environment environment;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Test
    public void test_initBinder() {
        assertNotNull(userProperties);
        assertEquals(userProperties.getUsername(), "塔尔");
        assertEquals(userProperties.getAge(), 20);
        assertEquals(userProperties.getAddress(), "湖南省长沙市岳麓区茶子山");

        assertEquals(environment.getProperty("user.username"), "塔尔");
        assertEquals(environment.getProperty("user.age"), "20");
        assertEquals(environment.getProperty("user.address"), "湖南省长沙市岳麓区茶子山");
    }

    @Test
    public void test_withEnvironmentChangeEvent() {
        // 1. 重新设置环境变量的值
        LinkedHashMap<String, String> testProperties = (LinkedHashMap<String, String>) applicationContext.getEnvironment().getPropertySources().get("Inlined Test Properties").getSource();
        testProperties.put("user.age", "22");
        // 未发布变更事件时bean没有任何变更
        assertEquals(environment.getProperty("user.age"), "22");
        assertEquals(userProperties.getAge(), 20);

        // 2. 发布变更事件
        EnvironmentChangeEvent environmentChangeEvent = new EnvironmentChangeEvent(Sets.set("user.age"));
        applicationContext.publishEvent(environmentChangeEvent);

        // 3. 验证是否成功
        assertEquals(userProperties.getAge(), 22);
    }


    @Configuration
    @EnableConfigurationProperties(UserProperties.class)
    @ImportAutoConfiguration(classes = {
            // 根据 ConfigurationPropertiesRebinderAutoConfiguration
            ConfigurationPropertiesRebinderAutoConfiguration.class
    })
    public static class UserPropertiesRefreshTestConfiguration {

    }
}
