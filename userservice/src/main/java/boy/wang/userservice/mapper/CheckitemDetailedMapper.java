package boy.wang.userservice.mapper;

import boy.wang.userservice.entity.CheckitemDetailed;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CheckitemDetailedMapper {

    CheckitemDetailed selectById(@Param("id") Long id);

    List<CheckitemDetailed> selectByCheckitemId(@Param("checkitemId") Long checkitemId);
}
