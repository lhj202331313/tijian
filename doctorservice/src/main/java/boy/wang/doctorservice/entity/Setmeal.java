package boy.wang.doctorservice.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Setmeal {

    private Long id;
    private Long hospitalId;
    private String name;
    private String type;
    private BigDecimal price;
    private String description;
    private Integer status;
}
