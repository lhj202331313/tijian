package boy.wang.userservice.mapper;

import boy.wang.userservice.entity.Checkitem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CheckitemMapper {

    Checkitem selectById(@Param("id") Long id);

    List<Checkitem> selectBySetmealId(@Param("setmealId") Long setmealId);

    List<Checkitem> selectAllActive();
}
