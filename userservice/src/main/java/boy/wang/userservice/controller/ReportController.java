package boy.wang.userservice.controller;

import boy.wang.userservice.common.Result;
import boy.wang.userservice.dto.OverallResultVO;
import boy.wang.userservice.dto.ReportVO;
import boy.wang.userservice.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/{orderId}")
    public Result<ReportVO> report(@PathVariable Long orderId) {
        ReportVO vo = reportService.getReport(orderId);
        return Result.success(vo);
    }

    @GetMapping("/{orderId}/overall")
    public Result<OverallResultVO> overall(@PathVariable Long orderId) {
        OverallResultVO vo = reportService.getOverallResult(orderId);
        return Result.success(vo);
    }
}
