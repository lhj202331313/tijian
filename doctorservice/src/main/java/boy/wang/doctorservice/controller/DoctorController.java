package boy.wang.doctorservice.controller;

import boy.wang.doctorservice.common.Result;
import boy.wang.doctorservice.dto.DoctorUpdateRequest;
import boy.wang.doctorservice.dto.DoctorVO;
import boy.wang.doctorservice.dto.LoginRequest;
import boy.wang.doctorservice.dto.LoginVO;
import boy.wang.doctorservice.service.DoctorService;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginRequest request) {
        return Result.success(doctorService.login(request));
    }

    @GetMapping("/info")
    public Result<DoctorVO> info(HttpServletRequest request) {
        Long doctorId = (Long) request.getAttribute("doctorId");
        return Result.success(doctorService.getDoctorInfo(doctorId));
    }

    @PutMapping("/info")
    public Result<DoctorVO> updateInfo(@RequestBody DoctorUpdateRequest request, HttpServletRequest httpRequest) {
        Long doctorId = (Long) httpRequest.getAttribute("doctorId");
        return Result.success(doctorService.updateProfile(doctorId, request));
    }
}
