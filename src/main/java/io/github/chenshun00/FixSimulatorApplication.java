package io.github.chenshun00;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("io.github.chenshun00.fix.dao.mapper")
public class FixSimulatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(FixSimulatorApplication.class, args);
    }

}
