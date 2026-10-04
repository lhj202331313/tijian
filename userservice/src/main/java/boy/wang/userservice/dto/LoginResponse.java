package boy.wang.userservice.dto;

import boy.wang.userservice.entity.User;
import lombok.Data;

@Data
public class LoginResponse {

    private String token;
    private UserInfo userInfo;

    @Data
    public static class UserInfo {
        private Long id;
        private String username;
        private String realName;
        private String idCard;
        private String phone;
        private Integer sex;

        public static UserInfo fromUser(User user) {
            UserInfo info = new UserInfo();
            info.setId(user.getId());
            info.setUsername(user.getUsername());
            info.setRealName(user.getRealName());
            info.setIdCard(user.getIdCard());
            info.setPhone(user.getPhone());
            info.setSex(user.getSex());
            return info;
        }
    }
}
