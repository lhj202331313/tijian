package boy.wang.userservice.service;

import boy.wang.userservice.dto.OverallResultVO;
import boy.wang.userservice.dto.ReportVO;

public interface ReportService {

    ReportVO getReport(Long orderId);

    OverallResultVO getOverallResult(Long orderId);
}
