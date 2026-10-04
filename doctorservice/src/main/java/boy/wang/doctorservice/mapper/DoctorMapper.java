package boy.wang.doctorservice.mapper;

import boy.wang.doctorservice.entity.Doctor;
import org.apache.ibatis.annotations.Param;

public interface DoctorMapper {

    Doctor findByUsername(@Param("username") String username);

    Doctor findById(@Param("id") Long id);

    int updateProfile(Doctor doctor);
}
