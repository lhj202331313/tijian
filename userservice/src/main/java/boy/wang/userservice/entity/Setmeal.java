package boy.wang.userservice.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class Setmeal implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long hospitalId;
    private String hospitalName;
    private String name;
    private String type;
    private BigDecimal price;
    private String description;
    private Integer status;
    private Integer itemCount;

    private List<Checkitem> checkitemList;
}
