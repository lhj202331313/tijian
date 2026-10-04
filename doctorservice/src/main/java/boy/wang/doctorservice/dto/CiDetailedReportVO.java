package boy.wang.doctorservice.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CiDetailedReportVO {

    private Long id;
    private Long cireportId;
    private Long checkitemdetailedId;
    private String name;
    private String unit;
    private String normalRange;
    private String resultValue;
    private Integer isAbnormal;
    private Date createTime;
}
