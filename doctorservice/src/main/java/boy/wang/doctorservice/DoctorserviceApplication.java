package boy.wang.doctorservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("boy.wang.doctorservice.mapper")
public class DoctorserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoctorserviceApplication.class, args);
    }
}
