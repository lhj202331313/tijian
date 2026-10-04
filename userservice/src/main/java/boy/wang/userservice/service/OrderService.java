package boy.wang.userservice.service;

import boy.wang.userservice.dto.OrderCreateRequest;
import boy.wang.userservice.dto.OrderDetailVO;
import boy.wang.userservice.dto.OrderListVO;
import boy.wang.userservice.entity.Orders;

import java.util.List;

public interface OrderService {

    Orders createOrder(Long userId, OrderCreateRequest request);

    List<OrderListVO> getOrderList(Long userId, Integer status);

    OrderDetailVO getOrderDetail(Long userId, Long id);

    Orders cancelOrder(Long userId, Long id);
}
