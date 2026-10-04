package boy.wang.doctorservice.mapper;

import boy.wang.doctorservice.dto.OrderDetailVO;
import boy.wang.doctorservice.dto.OrderListVO;
import boy.wang.doctorservice.dto.OrderQueryDTO;
import boy.wang.doctorservice.entity.Orders;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrdersMapper {

    List<OrderListVO> findList(@Param("query") OrderQueryDTO query,
                              @Param("offset") int offset,
                              @Param("size") int size);

    long countList(@Param("query") OrderQueryDTO query);

    OrderDetailVO findDetailById(@Param("id") Long id);

    Orders findById(@Param("id") Long id);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
