package team.CowsAndHorses;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author PatrickStar
 */
@SpringBootApplication
@EnableScheduling
public class JxFruitApplication {

    public static void main(String[] args) {
        SpringApplication.run(JxFruitApplication.class, args);
    }

    @Scheduled(cron = "0 30 14 * * ?")
    public void sayHello() {
        System.out.println("hello");
    }

}
