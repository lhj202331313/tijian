package boy.wang.doctorservice.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Doctor {

    private Long id;
    private String username;
    private String password;
    private String realName;
    private String department;
    private Long hospitalId;
    private String hospitalName;
    private String phone;
    private Date createTime;
    private Integer status;
}
