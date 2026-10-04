package boy.wang.doctorservice.service.impl;

import boy.wang.doctorservice.common.BusinessException;
import boy.wang.doctorservice.common.JwtUtil;
import boy.wang.doctorservice.common.ResultCode;
import boy.wang.doctorservice.dto.DoctorUpdateRequest;
import boy.wang.doctorservice.dto.DoctorVO;
import boy.wang.doctorservice.dto.LoginRequest;
import boy.wang.doctorservice.dto.LoginVO;
import boy.wang.doctorservice.entity.Doctor;
import boy.wang.doctorservice.mapper.DoctorMapper;
import boy.wang.doctorservice.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginVO login(LoginRequest request) {
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            throw new BusinessException("用户名或密码不能为空");
        }
        Doctor doctor = doctorMapper.findByUsername(request.getUsername());
        if (doctor == null || !request.getPassword().equals(doctor.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (doctor.getStatus() != null && doctor.getStatus() != 1) {
            throw new BusinessException("该账号已被禁用");
        }
        String token = jwtUtil.generate(doctor.getId(), doctor.getUsername());
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setDoctorInfo(toVO(doctor));
        return vo;
    }

    @Override
    public DoctorVO getDoctorInfo(Long doctorId) {
        Doctor doctor = doctorMapper.findById(doctorId);
        if (doctor == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "医生信息不存在");
        }
        return toVO(doctor);
    }

    @Override
    public DoctorVO updateProfile(Long doctorId, DoctorUpdateRequest request) {
        Doctor doctor = doctorMapper.findById(doctorId);
        if (doctor == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "医生信息不存在");
        }
        // 仅允许修改真实姓名、联系电话、科室
        Doctor update = new Doctor();
        update.setId(doctorId);
        update.setRealName(request.getRealName());
        update.setPhone(request.getPhone());
        update.setDepartment(request.getDepartment());
        doctorMapper.updateProfile(update);
        // 返回最新信息
        return toVO(doctorMapper.findById(doctorId));
    }

    private DoctorVO toVO(Doctor doctor) {
        DoctorVO vo = new DoctorVO();
        vo.setId(doctor.getId());
        vo.setUsername(doctor.getUsername());
        vo.setRealName(doctor.getRealName());
        vo.setDepartment(doctor.getDepartment());
        vo.setHospitalId(doctor.getHospitalId());
        vo.setHospitalName(doctor.getHospitalName());
        vo.setPhone(doctor.getPhone());
        vo.setStatus(doctor.getStatus());
        vo.setCreateTime(doctor.getCreateTime());
        return vo;
    }
}
