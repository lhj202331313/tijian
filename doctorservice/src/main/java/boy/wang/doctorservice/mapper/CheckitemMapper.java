package boy.wang.doctorservice.mapper;

import boy.wang.doctorservice.entity.Checkitem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CheckitemMapper {

    Checkitem findById(@Param("id") Long id);

    List<Checkitem> findBySetmealId(@Param("setmealId") Long setmealId);
}
