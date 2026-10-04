package boy.wang.userservice.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CiReport implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long orderId;
    private Long checkitemId;
    private Integer resultStatus;
    private LocalDateTime reportTime;

    private String checkitemName;
    private String checkitemType;
    private Integer sortNo;
}
