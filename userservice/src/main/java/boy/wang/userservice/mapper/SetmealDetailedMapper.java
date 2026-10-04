package boy.wang.userservice.mapper;

import boy.wang.userservice.entity.SetmealDetailed;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SetmealDetailedMapper {

    List<SetmealDetailed> selectBySetmealId(@Param("setmealId") Long setmealId);

    SetmealDetailed selectBySetmealIdAndCheckitemId(@Param("setmealId") Long setmealId, @Param("checkitemId") Long checkitemId);
}
