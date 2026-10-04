package boy.wang.doctorservice.entity;

import lombok.Data;

import java.util.Date;

@Data
public class CiReport {

    private Long id;
    private Long orderId;
    private Long checkitemId;
    private Integer resultStatus;
    private Date reportTime;
}
