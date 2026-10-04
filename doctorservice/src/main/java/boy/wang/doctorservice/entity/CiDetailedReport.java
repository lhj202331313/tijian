package boy.wang.doctorservice.entity;

import lombok.Data;

import java.util.Date;

@Data
public class CiDetailedReport {

    private Long id;
    private Long cireportId;
    private Long checkitemdetailedId;
    private String resultValue;
    private Integer isAbnormal;
    private Date createTime;
}
