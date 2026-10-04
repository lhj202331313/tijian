package boy.wang.doctorservice.mapper;

import boy.wang.doctorservice.entity.Hospital;
import org.apache.ibatis.annotations.Param;

public interface HospitalMapper {

    Hospital findById(@Param("id") Long id);
}
