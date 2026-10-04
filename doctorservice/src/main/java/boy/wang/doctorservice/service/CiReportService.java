package boy.wang.doctorservice.service;

import boy.wang.doctorservice.dto.CiReportSaveRequest;
import boy.wang.doctorservice.dto.CiReportVO;

import java.util.List;

public interface CiReportService {

    List<CiReportVO> listByOrderId(Long orderId);

    CiReportVO getByOrderAndCheckitem(Long orderId, Long checkitemId);

    void saveReport(CiReportSaveRequest request);
}
