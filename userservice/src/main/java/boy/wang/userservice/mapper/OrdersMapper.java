package boy.wang.userservice.mapper;

import boy.wang.userservice.entity.Orders;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrdersMapper {

    int insert(Orders order);

    Orders selectById(@Param("id") Long id);

    List<Orders> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("orderStatus") Integer orderStatus);

    int updateOrderStatus(@Param("id") Long id, @Param("orderStatus") Integer orderStatus, @Param("cancelTime") java.time.LocalDateTime cancelTime);

    String selectLastOrderNoByDate(@Param("datePrefix") String datePrefix);
}
