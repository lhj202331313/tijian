package boy.wang.userservice.controller;

import boy.wang.userservice.common.CurrentUser;
import boy.wang.userservice.common.Result;
import boy.wang.userservice.dto.LoginRequest;
import boy.wang.userservice.dto.LoginResponse;
import boy.wang.userservice.dto.RegisterRequest;
import boy.wang.userservice.dto.UserUpdateRequest;
import boy.wang.userservice.entity.User;
import boy.wang.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return Result.success(user);
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return Result.success(response);
    }

    @GetMapping("/info")
    public Result<User> info(@CurrentUser Long userId) {
        User user = userService.getUserInfo(userId);
        return Result.success(user);
    }

    @PutMapping("/info")
    public Result<User> updateInfo(@CurrentUser Long userId, @RequestBody UserUpdateRequest request) {
        User user = userService.updateUserInfo(userId, request);
        return Result.success(user);
    }
}
