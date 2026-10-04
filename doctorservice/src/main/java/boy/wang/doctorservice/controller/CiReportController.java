package boy.wang.doctorservice.controller;

import boy.wang.doctorservice.common.Result;
import boy.wang.doctorservice.dto.CiReportSaveRequest;
import boy.wang.doctorservice.dto.CiReportVO;
import boy.wang.doctorservice.service.CiReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cireport")
public class CiReportController {

    @Autowired
    private CiReportService ciReportService;

    @GetMapping("/list")
    public Result<List<CiReportVO>> list(@RequestParam Long orderId) {
        return Result.success(ciReportService.listByOrderId(orderId));
    }

    @GetMapping("/{orderId}/{checkitemId}")
    public Result<CiReportVO> detail(@PathVariable Long orderId, @PathVariable Long checkitemId) {
        return Result.success(ciReportService.getByOrderAndCheckitem(orderId, checkitemId));
    }

    @PostMapping("/save")
    public Result<Void> save(@RequestBody CiReportSaveRequest request) {
        ciReportService.saveReport(request);
        return Result.success();
    }
}
