package boy.wang.doctorservice.mapper;

import boy.wang.doctorservice.entity.SetmealDetailed;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SetmealDetailedMapper {

    List<SetmealDetailed> findBySetmealId(@Param("setmealId") Long setmealId);
}
