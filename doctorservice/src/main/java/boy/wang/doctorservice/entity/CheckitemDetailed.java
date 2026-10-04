package boy.wang.doctorservice.entity;

import lombok.Data;

@Data
public class CheckitemDetailed {

    private Long id;
    private Long checkitemId;
    private String name;
    private String unit;
    private String normalRange;
    private Integer status;
}
