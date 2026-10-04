package boy.wang.userservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class OrderListVO {

    private Long id;
    private String orderNo;
    private Long hospitalId;
    private String hospitalName;
    private Long setmealId;
    private String setmealName;
    private String setmealType;
    private BigDecimal setmealPrice;
    private Long doctorId;
    private String doctorName;
    private String doctorDepartment;
    private LocalDate orderDate;
    private Integer orderStatus;
    private LocalDateTime createTime;
}
