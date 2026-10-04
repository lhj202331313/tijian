package boy.wang.userservice.service.impl;

import boy.wang.userservice.common.ResultCode;
import boy.wang.userservice.dto.OrderCreateRequest;
import boy.wang.userservice.dto.OrderDetailVO;
import boy.wang.userservice.dto.OrderListVO;
import boy.wang.userservice.entity.Checkitem;
import boy.wang.userservice.entity.Doctor;
import boy.wang.userservice.entity.Hospital;
import boy.wang.userservice.entity.Orders;
import boy.wang.userservice.entity.Setmeal;
import boy.wang.userservice.exception.BusinessException;
import boy.wang.userservice.mapper.CheckitemMapper;
import boy.wang.userservice.mapper.DoctorMapper;
import boy.wang.userservice.mapper.HospitalMapper;
import boy.wang.userservice.mapper.OrdersMapper;
import boy.wang.userservice.mapper.SetmealMapper;
import boy.wang.userservice.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrdersMapper ordersMapper;
    private final HospitalMapper hospitalMapper;
    private final SetmealMapper setmealMapper;
    private final CheckitemMapper checkitemMapper;
    private final DoctorMapper doctorMapper;

    @Autowired
    public OrderServiceImpl(OrdersMapper ordersMapper, HospitalMapper hospitalMapper,
                           SetmealMapper setmealMapper, CheckitemMapper checkitemMapper,
                           DoctorMapper doctorMapper) {
        this.ordersMapper = ordersMapper;
        this.hospitalMapper = hospitalMapper;
        this.setmealMapper = setmealMapper;
        this.checkitemMapper = checkitemMapper;
        this.doctorMapper = doctorMapper;
    }

    @Override
    public synchronized Orders createOrder(Long userId, OrderCreateRequest request) {
        if (request.getHospitalId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "医院ID不能为空");
        }
        if (request.getSetmealId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "套餐ID不能为空");
        }
        if (request.getOrderDate() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "预约日期不能为空");
        }
        Hospital hospital = hospitalMapper.selectById(request.getHospitalId());
        if (hospital == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "医院不存在");
        }
        Setmeal setmeal = setmealMapper.selectById(request.getSetmealId());
        if (setmeal == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "套餐不存在");
        }
        // 校验接待医生：必须存在、在职且属于所选医院
        Doctor doctor = null;
        if (request.getDoctorId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "请选择接待医生");
        }
        doctor = doctorMapper.findById(request.getDoctorId());
        if (doctor == null || doctor.getStatus() == null || doctor.getStatus() != 1) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "医生不存在或已停诊");
        }
        if (!doctor.getHospitalId().equals(request.getHospitalId())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "接待医生不属于所选医院");
        }
        Orders order = new Orders();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setHospitalId(request.getHospitalId());
        order.setSetmealId(request.getSetmealId());
        order.setDoctorId(request.getDoctorId());
        order.setOrderDate(request.getOrderDate());
        order.setOrderStatus(Orders.STATUS_PENDING);
        order.setCreateTime(LocalDateTime.now());
        ordersMapper.insert(order);
        order.setHospitalName(hospital.getName());
        order.setSetmealName(setmeal.getName());
        order.setDoctorName(doctor.getRealName());
        order.setDoctorDepartment(doctor.getDepartment());
        return order;
    }

    private String generateOrderNo() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String millis = String.valueOf(System.currentTimeMillis());
        String suffix = millis.length() >= 6 ? millis.substring(millis.length() - 6) : String.format("%06d", millis.length());
        return "TJ" + datePart + suffix;
    }

    @Override
    public List<OrderListVO> getOrderList(Long userId, Integer status) {
        List<Orders> orders = ordersMapper.selectByUserIdAndStatus(userId, status);
        List<OrderListVO> result = new ArrayList<>();
        for (Orders order : orders) {
            OrderListVO vo = new OrderListVO();
            BeanUtils.copyProperties(order, vo);
            result.add(vo);
        }
        return result;
    }

    @Override
    public OrderDetailVO getOrderDetail(Long userId, Long id) {
        Orders order = ordersMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "无权查看该订单");
        }
        OrderDetailVO vo = new OrderDetailVO();
        BeanUtils.copyProperties(order, vo);
        List<Checkitem> checkitems = checkitemMapper.selectBySetmealId(order.getSetmealId());
        vo.setCheckitemList(checkitems);
        return vo;
    }

    @Override
    public Orders cancelOrder(Long userId, Long id) {
        Orders order = ordersMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "无权操作该订单");
        }
        if (order.getOrderStatus() != Orders.STATUS_PENDING) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "仅待体检订单可取消");
        }
        ordersMapper.updateOrderStatus(id, Orders.STATUS_CANCELED, LocalDateTime.now());
        order.setOrderStatus(Orders.STATUS_CANCELED);
        order.setCancelTime(LocalDateTime.now());
        return order;
    }
}
