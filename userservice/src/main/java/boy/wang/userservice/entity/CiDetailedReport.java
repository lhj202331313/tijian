package boy.wang.userservice.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CiDetailedReport implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long cireportId;
    private Long checkitemdetailedId;
    private String resultValue;
    private Integer isAbnormal;
    private LocalDateTime createTime;

    private String name;
    private String unit;
    private String normalRange;
}
