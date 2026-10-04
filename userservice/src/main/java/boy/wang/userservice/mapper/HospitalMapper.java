package boy.wang.userservice.mapper;

import boy.wang.userservice.entity.Hospital;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HospitalMapper {

    List<Hospital> selectAllActive();

    Hospital selectById(@Param("id") Long id);

    Hospital selectByIdWithSetmeals(@Param("id") Long id);
}
