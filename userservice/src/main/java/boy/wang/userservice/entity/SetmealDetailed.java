package boy.wang.userservice.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class SetmealDetailed implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long setmealId;
    private Long checkitemId;
}
