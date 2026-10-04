package boy.wang.userservice.dto;

import lombok.Data;

@Data
public class RegisterRequest {

    private String username;
    private String password;
    private String realName;
    private String idCard;
    private String phone;
    private Integer sex;
}
