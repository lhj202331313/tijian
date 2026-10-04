package boy.wang.userservice.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Hospital implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String address;
    private String phone;
    private String businessHours;
    private String ruleDesc;
    private Integer status;

    private List<Setmeal> setmealList;
}
