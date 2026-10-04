package boy.wang.userservice.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OverallResult implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int STATUS_DRAFT = 0;
    public static final int STATUS_PUBLISHED = 1;

    private Long id;
    private Long orderId;
    private Long doctorId;
    private String summary;
    private String doctorName;
    private Integer resultStatus;
    private LocalDateTime createTime;
    private LocalDateTime publishTime;
    private Integer status;
}
