package boy.wang.userservice.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class CheckitemDetailed implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long checkitemId;
    private String name;
    private String unit;
    private String normalRange;
    private Integer status;
}
