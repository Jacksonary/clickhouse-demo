import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author liuwg-a
 * @date 2020/7/9 14:36
 * @description
 */
@SpringBootApplication(scanBasePackages = {"org.example"})
@MapperScan(basePackages = {"org.example.dao.db1.mapper"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
