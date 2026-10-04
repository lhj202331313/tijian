package boy.wang.doctorservice.mapper;

import boy.wang.doctorservice.entity.CiDetailedReport;
import org.apache.ibatis.annotations.Param;

public interface CiDetailedReportMapper {

    CiDetailedReport findByCireportAndDetail(@Param("cireportId") Long cireportId,
                                             @Param("checkitemdetailedId") Long checkitemdetailedId);

    int insert(CiDetailedReport ciDetailedReport);

    int updateResult(@Param("id") Long id,
                     @Param("resultValue") String resultValue,
                     @Param("isAbnormal") Integer isAbnormal);
}
