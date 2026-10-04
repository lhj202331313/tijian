package boy.wang.userservice.service;

import boy.wang.userservice.dto.LoginRequest;
import boy.wang.userservice.dto.LoginResponse;
import boy.wang.userservice.dto.RegisterRequest;
import boy.wang.userservice.dto.UserUpdateRequest;
import boy.wang.userservice.entity.User;

public interface UserService {

    User register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    User getUserInfo(Long userId);

    User updateUserInfo(Long userId, UserUpdateRequest request);
}
