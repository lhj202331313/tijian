package boy.wang.userservice.mapper;

import boy.wang.userservice.entity.Setmeal;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SetmealMapper {

    List<Setmeal> selectByCondition(@Param("hospitalId") Long hospitalId, @Param("type") String type);

    Setmeal selectById(@Param("id") Long id);

    Setmeal selectByIdWithCheckitems(@Param("id") Long id);
}
