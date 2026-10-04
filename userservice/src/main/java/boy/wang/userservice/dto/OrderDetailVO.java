package boy.wang.userservice.dto;

import boy.wang.userservice.entity.Checkitem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailVO {

    private Long id;
    private String orderNo;
    private Long userId;
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
    private LocalDateTime cancelTime;
    private List<Checkitem> checkitemList;
}
