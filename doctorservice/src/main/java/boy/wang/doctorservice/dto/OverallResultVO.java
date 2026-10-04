package boy.wang.doctorservice.dto;

import lombok.Data;

import java.util.Date;

@Data
public class OverallResultVO {

    private Long id;
    private Long orderId;
    private Long doctorId;
    private String summary;
    private String doctorName;
    private Integer resultStatus;
    private Date createTime;
    private Date publishTime;
    private Integer status;
}
