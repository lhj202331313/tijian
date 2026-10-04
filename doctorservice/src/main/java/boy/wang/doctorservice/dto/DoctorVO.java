package boy.wang.doctorservice.dto;

import lombok.Data;

import java.util.Date;

@Data
public class DoctorVO {

    private Long id;
    private String username;
    private String realName;
    private String department;
    private Long hospitalId;
    private String hospitalName;
    private String phone;
    private Integer status;
    private Date createTime;
}
