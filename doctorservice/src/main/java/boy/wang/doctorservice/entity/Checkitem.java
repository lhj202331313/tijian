package boy.wang.doctorservice.entity;

import lombok.Data;

@Data
public class Checkitem {

    private Long id;
    private String name;
    private String type;
    private Integer sortNo;
    private Integer status;
}
