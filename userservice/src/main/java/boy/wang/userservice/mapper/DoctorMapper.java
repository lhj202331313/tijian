package boy.wang.userservice.mapper;

import boy.wang.userservice.entity.Doctor;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DoctorMapper {

    /**
     * 查询某医院的在职医生（供用户预约时选择接待医生）
     */
    List<Doctor> findByHospitalId(@Param("hospitalId") Long hospitalId);

    Doctor findById(@Param("id") Long id);
}
