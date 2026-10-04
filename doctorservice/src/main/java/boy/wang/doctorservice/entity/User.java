package boy.wang.doctorservice.entity;

import lombok.Data;

@Data
public class User {

    private Long id;
    private String username;
    private String realName;
    private String idCard;
    private String phone;
    private String sex;
}
