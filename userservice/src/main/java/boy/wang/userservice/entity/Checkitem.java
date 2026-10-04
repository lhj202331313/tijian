package boy.wang.userservice.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Checkitem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String type;
    private Integer sortNo;
    private Integer status;
}
