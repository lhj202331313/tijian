package boy.wang.doctorservice.dto;

import lombok.Data;

@Data
public class OrderQueryDTO {

    private Integer status;
    private String orderDate;
    private Integer page = 1;
    private Integer size = 10;
}
