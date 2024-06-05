package com.lei.micsphere.spring.bean.refresh;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * <p>
 * 普通的 pojo bean 刷新
 * </p>
 *
 * @author 伍磊
 */
@SpringBootTest
public class GenericBeanRefreshTest {

    @Autowired
    private GenericBeanRefreshTestConfiguration genericBeanRefreshTestConfiguration;

    @Autowired
    private User user;

    @Autowired
    private Environment environment;

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Test
    public void test() throws InterruptedException, IOException {
        assertNotNull(user);
        assertEquals(user.username, "xx");
        assertEquals(user.age, 10);
        assertEquals(genericBeanRefreshTestConfiguration.getUsername(), "xx");

        // 修改properties，xx->yy
        // 修改配置文件
        Properties properties = new Properties();
        String filePath = GenericBeanEnvironmentPostProcessor.filePath.replace("file:", "");
        properties.load(new FileInputStream(filePath));
        properties.put("test.user.name", "yy");
        properties.store(new FileOutputStream(filePath), properties.toString());

        // 发布refresh事件
        RefreshEvent refreshEvent = new RefreshEvent(this, null, null);
        applicationContext.publishEvent(refreshEvent);

        // refresh事件导致环境变量的刷新 org.springframework.cloud.context.refresh.ContextRefresher.refreshEnvironment
        // 从而 @Value 修饰的数据会变化
        assertEquals(genericBeanRefreshTestConfiguration.getUsername(), "yy");

        // 但是非@Value修饰的不会变化，因为没有事件进行处理
        // 需要主动消费 EnvironmentChangeEvent 事件
        assertEquals(user.username, "xx");

        // 还原
        properties.put("test.user.name", "xx");
        properties.store(new FileOutputStream(filePath), properties.toString());
    }


    @Configuration
    @RefreshScope
    @ImportAutoConfiguration(classes = {RefreshAutoConfiguration.class})
    public static class GenericBeanRefreshTestConfiguration {

        @Value("${test.user.name}")
        private String username;

        @Value("${test.user.age}")
        private Integer age;

        @Bean
        public User user() {
            User user = new User();
            user.setUsername(username);
            user.setAge(age);
            return user;
        }

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
    }

    public static class User {
        private String username;
        private Integer age;

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
    }
}
