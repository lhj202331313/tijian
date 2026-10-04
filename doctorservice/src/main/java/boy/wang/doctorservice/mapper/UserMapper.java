package boy.wang.doctorservice.mapper;

import boy.wang.doctorservice.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

    User findById(@Param("id") Long id);
}
