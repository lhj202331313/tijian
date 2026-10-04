package boy.wang.doctorservice.service.impl;

import boy.wang.doctorservice.common.BusinessException;
import boy.wang.doctorservice.common.ResultCode;
import boy.wang.doctorservice.dto.OrderDetailVO;
import boy.wang.doctorservice.dto.OrderListVO;
import boy.wang.doctorservice.dto.OrderQueryDTO;
import boy.wang.doctorservice.dto.PageResult;
import boy.wang.doctorservice.entity.Checkitem;
import boy.wang.doctorservice.entity.CiReport;
import boy.wang.doctorservice.entity.Orders;
import boy.wang.doctorservice.mapper.CheckitemMapper;
import boy.wang.doctorservice.mapper.CiReportMapper;
import boy.wang.doctorservice.mapper.OrdersMapper;
import boy.wang.doctorservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private CheckitemMapper checkitemMapper;

    @Autowired
    private CiReportMapper ciReportMapper;

    @Override
    public PageResult<OrderListVO> listOrders(OrderQueryDTO query) {
        if (query == null) {
            query = new OrderQueryDTO();
        }
        int page = (query.getPage() == null || query.getPage() < 1) ? 1 : query.getPage();
        int size = (query.getSize() == null || query.getSize() < 1) ? 10 : query.getSize();
        query.setPage(page);
        query.setSize(size);
        int offset = (page - 1) * size;
        List<OrderListVO> records = ordersMapper.findList(query, offset, size);
        long total = ordersMapper.countList(query);
        return new PageResult<>(records, total, page, size);
    }

    @Override
    public OrderDetailVO getOrderDetail(Long id) {
        OrderDetailVO vo = ordersMapper.findDetailById(id);
        if (vo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "订单不存在");
        }
        if (vo.getSetmeal() != null && vo.getSetmeal().getId() != null) {
            vo.setCheckitems(checkitemMapper.findBySetmealId(vo.getSetmeal().getId()));
        }
        vo.setReports(ciReportMapper.findVOByOrderId(id));
        return vo;
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (status == null || status != 1) {
            throw new BusinessException("仅允许将订单状态更新为已完成(1)");
        }
        Orders order = ordersMapper.findById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "订单不存在");
        }
        if (order.getOrderStatus() == null || order.getOrderStatus() != 0) {
            throw new BusinessException("当前订单状态不允许完成体检操作");
        }
        Long setmealId = order.getSetmealId();
        if (setmealId == null) {
            throw new BusinessException("订单缺少套餐信息，无法完成体检");
        }
        List<Checkitem> checkitems = checkitemMapper.findBySetmealId(setmealId);
        if (checkitems != null) {
            for (Checkitem ci : checkitems) {
                CiReport report = ciReportMapper.findEntityByOrderAndCheckitem(id, ci.getId());
                if (report == null) {
                    throw new BusinessException("检查项「" + ci.getName() + "」尚未录入分项报告，无法完成体检");
                }
            }
        }
        ordersMapper.updateStatus(id, 1);
    }
}
