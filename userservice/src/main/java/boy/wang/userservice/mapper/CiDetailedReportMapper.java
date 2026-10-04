package boy.wang.userservice.mapper;

import boy.wang.userservice.entity.CiDetailedReport;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CiDetailedReportMapper {

    List<CiDetailedReport> selectByCireportId(@Param("cireportId") Long cireportId);
}
