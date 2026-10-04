package boy.wang.userservice.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderCreateRequest {

    private Long hospitalId;
    private Long setmealId;
    private Long doctorId;
    private LocalDate orderDate;
}
