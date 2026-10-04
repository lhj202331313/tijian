package boy.wang.doctorservice.controller;

import boy.wang.doctorservice.common.Result;
import boy.wang.doctorservice.dto.OverallResultSaveRequest;
import boy.wang.doctorservice.dto.OverallResultVO;
import boy.wang.doctorservice.service.OverallResultService;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/overall")
public class OverallResultController {

    @Autowired
    private OverallResultService overallResultService;

    @GetMapping("/{orderId}")
    public Result<OverallResultVO> get(@PathVariable Long orderId) {
        return Result.success(overallResultService.getByOrderId(orderId));
    }

    @PostMapping("/save")
    public Result<Void> save(@RequestBody OverallResultSaveRequest request, HttpServletRequest httpRequest) {
        Long doctorId = (Long) httpRequest.getAttribute("doctorId");
        overallResultService.save(request, doctorId);
        return Result.success();
    }

    @PutMapping("/{id}/publish")
    public Result<Void> publish(@PathVariable Long id) {
        overallResultService.publish(id);
        return Result.success();
    }
}
