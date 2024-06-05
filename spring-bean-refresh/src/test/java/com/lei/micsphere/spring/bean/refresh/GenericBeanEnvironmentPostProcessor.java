package com.lei.micsphere.spring.bean.refresh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;

/**
 * <p>
 * ClasspathEnvironmentPostProcessor    <br/>
 * 这里只是演示效果 <br/>
 * 直接固定从某个文件下获取，这样当RefreshEvent刷新后，此后置处理器又会重新被调用，然后重新添加到environment中，
 * 对原来的数据进行覆盖
 * </p>
 *
 * @author 伍磊
 */
public class GenericBeanEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    public static final String filePath = "file:/Users/wulei/IdeaProjects/personal/spring-refresh/spring-bean-refresh/src/test/java/com/lei/micsphere/spring/bean/refresh/genericBean.properties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            ResourcePropertySource resourcePropertySource = new ResourcePropertySource(
                    "genericBean", filePath, this.getClass().getClassLoader());
            MutablePropertySources propertySources = environment.getPropertySources();
            propertySources.addLast(resourcePropertySource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
