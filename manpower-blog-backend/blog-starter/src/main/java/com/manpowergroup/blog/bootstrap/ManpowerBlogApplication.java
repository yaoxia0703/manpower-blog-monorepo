package com.manpowergroup.blog.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.manpowergroup.blog")
public class ManpowerBlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManpowerBlogApplication.class, args);
    }

}
