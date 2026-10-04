package boy.wang.doctorservice.service;

import boy.wang.doctorservice.dto.OverallResultSaveRequest;
import boy.wang.doctorservice.dto.OverallResultVO;

public interface OverallResultService {

    OverallResultVO getByOrderId(Long orderId);

    void save(OverallResultSaveRequest request, Long doctorId);

    void publish(Long id);
}
