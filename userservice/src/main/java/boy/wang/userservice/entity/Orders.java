package boy.wang.userservice.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_COMPLETED = 1;
    public static final int STATUS_CANCELED = 2;

    private Long id;
    private String orderNo;
    private Long userId;
    private Long hospitalId;
    private Long setmealId;
    private Long doctorId;
    private LocalDate orderDate;
    private Integer orderStatus;
    private LocalDateTime createTime;
    private LocalDateTime cancelTime;

    private String hospitalName;
    private String setmealName;
    private String setmealType;
    private BigDecimal setmealPrice;
    private String doctorName;
    private String doctorDepartment;
}
