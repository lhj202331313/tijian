package boy.wang.doctorservice.dto;

import lombok.Data;

import java.util.Date;

@Data
public class OrderListVO {

    private Long id;
    private String orderNo;
    private Long userId;
    private String userName;
    private String userPhone;
    private Long hospitalId;
    private String hospitalName;
    private Long setmealId;
    private String setmealName;
    private Long doctorId;
    private String doctorName;
    private String doctorDepartment;
    private Date orderDate;
    private Integer orderStatus;
    private Date createTime;
    private Date cancelTime;
}
