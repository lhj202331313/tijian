package boy.wang.userservice.controller;

import boy.wang.userservice.common.CurrentUser;
import boy.wang.userservice.common.Result;
import boy.wang.userservice.dto.OrderCreateRequest;
import boy.wang.userservice.dto.OrderDetailVO;
import boy.wang.userservice.dto.OrderListVO;
import boy.wang.userservice.entity.Orders;
import boy.wang.userservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public Result<Orders> create(@CurrentUser Long userId, @RequestBody OrderCreateRequest request) {
        Orders order = orderService.createOrder(userId, request);
        return Result.success(order);
    }

    @GetMapping("/list")
    public Result<List<OrderListVO>> list(@CurrentUser Long userId,
                                          @RequestParam(required = false) Integer status) {
        List<OrderListVO> list = orderService.getOrderList(userId, status);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<OrderDetailVO> detail(@CurrentUser Long userId, @PathVariable Long id) {
        OrderDetailVO vo = orderService.getOrderDetail(userId, id);
        return Result.success(vo);
    }

    @PutMapping("/{id}/cancel")
    public Result<Orders> cancel(@CurrentUser Long userId, @PathVariable Long id) {
        Orders order = orderService.cancelOrder(userId, id);
        return Result.success(order);
    }
}
