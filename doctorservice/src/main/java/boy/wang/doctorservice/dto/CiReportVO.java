package boy.wang.doctorservice.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CiReportVO {

    private Long id;
    private Long orderId;
    private Long checkitemId;
    private String checkitemName;
    private Integer resultStatus;
    private Date reportTime;
    private List<CiDetailedReportVO> details;
}
