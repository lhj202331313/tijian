package boy.wang.doctorservice.dto;

import lombok.Data;

@Data
public class OverallResultSaveRequest {

    private Long orderId;
    private String summary;
    private Integer resultStatus;
}
