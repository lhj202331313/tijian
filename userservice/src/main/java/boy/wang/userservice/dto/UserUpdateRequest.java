package boy.wang.userservice.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {

    private String realName;
    private String idCard;
    private String phone;
    private Integer sex;
}
