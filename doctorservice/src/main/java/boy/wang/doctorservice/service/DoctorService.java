package boy.wang.doctorservice.service;

import boy.wang.doctorservice.dto.DoctorUpdateRequest;
import boy.wang.doctorservice.dto.DoctorVO;
import boy.wang.doctorservice.dto.LoginRequest;
import boy.wang.doctorservice.dto.LoginVO;

public interface DoctorService {

    LoginVO login(LoginRequest request);

    DoctorVO getDoctorInfo(Long doctorId);

    DoctorVO updateProfile(Long doctorId, DoctorUpdateRequest request);
}
