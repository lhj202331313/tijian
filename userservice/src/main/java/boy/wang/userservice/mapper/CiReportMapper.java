package boy.wang.userservice.mapper;

import boy.wang.userservice.entity.CiReport;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CiReportMapper {

    List<CiReport> selectByOrderId(@Param("orderId") Long orderId);

    CiReport selectByOrderIdAndCheckitemId(@Param("orderId") Long orderId, @Param("checkitemId") Long checkitemId);
}
