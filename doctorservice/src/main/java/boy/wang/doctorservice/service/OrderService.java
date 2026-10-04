package boy.wang.doctorservice.service;

import boy.wang.doctorservice.dto.OrderDetailVO;
import boy.wang.doctorservice.dto.OrderListVO;
import boy.wang.doctorservice.dto.OrderQueryDTO;
import boy.wang.doctorservice.dto.PageResult;

public interface OrderService {

    PageResult<OrderListVO> listOrders(OrderQueryDTO query);

    OrderDetailVO getOrderDetail(Long id);

    void updateStatus(Long id, Integer status);
}
