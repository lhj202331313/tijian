package boy.wang.userservice.controller;

import boy.wang.userservice.common.Result;
import boy.wang.userservice.entity.Hospital;
import boy.wang.userservice.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospital")
public class HospitalController {

    private final HospitalService hospitalService;

    @Autowired
    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @GetMapping("/list")
    public Result<List<Hospital>> list() {
        List<Hospital> list = hospitalService.getHospitalList();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Hospital> detail(@PathVariable Long id) {
        Hospital hospital = hospitalService.getHospitalDetail(id);
        return Result.success(hospital);
    }
}
