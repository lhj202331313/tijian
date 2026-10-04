package boy.wang.userservice.controller;

import boy.wang.userservice.common.Result;
import boy.wang.userservice.entity.Doctor;
import boy.wang.userservice.mapper.DoctorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * 用户端：预约时选择接待医生
 */
@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    private final DoctorMapper doctorMapper;

    @Autowired
    public DoctorController(DoctorMapper doctorMapper) {
        this.doctorMapper = doctorMapper;
    }

    @GetMapping("/list")
    public Result<List<Doctor>> listByHospital(@RequestParam Long hospitalId) {
        if (hospitalId == null) {
            return Result.success(Collections.emptyList());
        }
        return Result.success(doctorMapper.findByHospitalId(hospitalId));
    }
}
