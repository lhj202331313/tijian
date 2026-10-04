package boy.wang.doctorservice.entity;

import lombok.Data;

@Data
public class Hospital {

    private Long id;
    private String name;
    private String address;
    private String phone;
    private String businessHours;
    private String ruleDesc;
    private Integer status;
}
