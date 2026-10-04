package boy.wang.doctorservice.controller;

import boy.wang.doctorservice.common.Result;
import boy.wang.doctorservice.dto.OrderDetailVO;
import boy.wang.doctorservice.dto.OrderListVO;
import boy.wang.doctorservice.dto.OrderQueryDTO;
import boy.wang.doctorservice.dto.OrderStatusRequest;
import boy.wang.doctorservice.dto.PageResult;
import boy.wang.doctorservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/list")
    public Result<PageResult<OrderListVO>> list(OrderQueryDTO query) {
        return Result.success(orderService.listOrders(query));
    }

    @GetMapping("/{id}")
    public Result<OrderDetailVO> detail(@PathVariable Long id) {
        return Result.success(orderService.getOrderDetail(id));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody OrderStatusRequest request) {
        orderService.updateStatus(id, request.getStatus());
        return Result.success();
    }
}
