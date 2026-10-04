package boy.wang.doctorservice.mapper;

import boy.wang.doctorservice.dto.CiReportVO;
import boy.wang.doctorservice.entity.CiReport;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CiReportMapper {

    List<CiReportVO> findVOByOrderId(@Param("orderId") Long orderId);

    CiReportVO findVOByOrderAndCheckitem(@Param("orderId") Long orderId,
                                         @Param("checkitemId") Long checkitemId);

    CiReport findEntityByOrderAndCheckitem(@Param("orderId") Long orderId,
                                           @Param("checkitemId") Long checkitemId);

    int insert(CiReport ciReport);

    int updateStatusAndTime(@Param("id") Long id,
                            @Param("resultStatus") Integer resultStatus,
                            @Param("reportTime") java.util.Date reportTime);
}
