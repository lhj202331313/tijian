package boy.wang.userservice.mapper;

import boy.wang.userservice.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    int insert(User user);

    User selectByUsername(@Param("username") String username);

    User selectById(@Param("id") Long id);

    int updateById(User user);

    List<User> selectAll();
}
