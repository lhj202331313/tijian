package boy.wang.doctorservice.mapper;

import boy.wang.doctorservice.entity.Setmeal;
import org.apache.ibatis.annotations.Param;

public interface SetmealMapper {

    Setmeal findById(@Param("id") Long id);
}
