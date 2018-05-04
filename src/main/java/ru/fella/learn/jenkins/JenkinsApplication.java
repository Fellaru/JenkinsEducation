package ru.fella.learn.jenkins;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JenkinsApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(JenkinsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        for (int i = 1; i <= 10; i++) {
            System.out.println("Hello, Jenkins World! It is " + i + " time.");
        }
    }
}
