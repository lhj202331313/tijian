package boy.wang.userservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OverallResultVO {

    private Long id;
    private Long orderId;
    private String summary;
    private String doctorName;
    private Integer resultStatus;
    private LocalDateTime publishTime;
}
