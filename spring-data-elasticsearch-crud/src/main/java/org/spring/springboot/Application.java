package org.spring.springboot;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.spring.springboot.domain.City;
import org.spring.springboot.service.CityService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Spring Boot 应用启动类
 * <p>
 * Created by bysocket on 16/4/26.
 */
// Spring Boot 应用的标识
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        // 程序启动入口
        // 启动嵌入式的 Tomcat 并初始化 Spring 环境及其各 Spring 组件
        ConfigurableApplicationContext run = SpringApplication.run(Application.class, args);
        CityService cityService = run.getBean(CityService.class);

        for (int i = 0; i < 1000000000; i++) {
            City city = new City();
            city.setId(Long.decode(i + ""));
            city.setName(RandomStringUtils.randomAlphanumeric(6) + i);
            city.setDescription(RandomStringUtils.randomAlphanumeric(100));
            city.setScore(i);
            System.out.println(cityService.saveCity(city));
        }
    }
}
