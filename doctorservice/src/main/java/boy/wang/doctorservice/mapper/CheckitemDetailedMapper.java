package boy.wang.doctorservice.mapper;

import boy.wang.doctorservice.entity.CheckitemDetailed;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CheckitemDetailedMapper {

    List<CheckitemDetailed> findByCheckitemId(@Param("checkitemId") Long checkitemId);
}
