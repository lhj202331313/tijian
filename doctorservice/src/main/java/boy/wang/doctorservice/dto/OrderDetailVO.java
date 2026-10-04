package boy.wang.doctorservice.dto;

import boy.wang.doctorservice.entity.Checkitem;
import boy.wang.doctorservice.entity.Doctor;
import boy.wang.doctorservice.entity.Hospital;
import boy.wang.doctorservice.entity.Setmeal;
import boy.wang.doctorservice.entity.User;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
public class OrderDetailVO {

    private Long id;
    private String orderNo;
    private LocalDate orderDate;
    private Integer orderStatus;
    private Date createTime;
    private Date cancelTime;
    private User user;
    private Hospital hospital;
    private Setmeal setmeal;
    private Doctor doctor;
    private List<Checkitem> checkitems;
    private List<CiReportVO> reports;
}
