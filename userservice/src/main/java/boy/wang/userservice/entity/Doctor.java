package boy.wang.userservice.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户端只读的医生信息（不含账号密码）
 */
@Data
public class Doctor implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String realName;
    private String department;
    private String phone;
    private Long hospitalId;
    private String hospitalName;
    private Integer status;
}
