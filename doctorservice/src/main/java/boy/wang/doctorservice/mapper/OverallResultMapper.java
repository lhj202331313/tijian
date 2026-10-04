package boy.wang.doctorservice.mapper;

import boy.wang.doctorservice.entity.OverallResult;
import org.apache.ibatis.annotations.Param;

public interface OverallResultMapper {

    OverallResult findByOrderId(@Param("orderId") Long orderId);

    OverallResult findById(@Param("id") Long id);

    int insert(OverallResult overallResult);

    int update(OverallResult overallResult);

    int updatePublish(@Param("id") Long id, @Param("publishTime") java.util.Date publishTime);
}
