package boy.wang.doctorservice.entity;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class Orders {

    private Long id;
    private String orderNo;
    private Long userId;
    private Long hospitalId;
    private Long setmealId;
    private Long doctorId;
    private LocalDate orderDate;
    private Integer orderStatus;
    private Date createTime;
    private Date cancelTime;
}
