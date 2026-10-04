package boy.wang.userservice.mapper;

import boy.wang.userservice.entity.OverallResult;
import org.apache.ibatis.annotations.Param;

public interface OverallResultMapper {

    OverallResult selectPublishedByOrderId(@Param("orderId") Long orderId);

    OverallResult selectByOrderId(@Param("orderId") Long orderId);
}
