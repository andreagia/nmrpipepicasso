package org.cirmmp.nmrpipepicasso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NmrpipepicassoApplication {

    public static void main(String[] args) {
        SpringApplication.run(NmrpipepicassoApplication.class, args);
    }

}
