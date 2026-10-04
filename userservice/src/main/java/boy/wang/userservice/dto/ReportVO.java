package boy.wang.userservice.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReportVO {

    private Long orderId;
    private String orderNo;
    private String hospitalName;
    private String setmealName;
    private LocalDate orderDate;
    private Integer status;
    private List<ItemReport> items;

    @Data
    public static class ItemReport {
        private Long checkitemId;
        private String checkitemName;
        private String checkitemType;
        private Integer resultStatus;
        private LocalDateTime reportTime;
        private List<DetailReport> details;
    }

    @Data
    public static class DetailReport {
        private Long id;
        private String name;
        private String unit;
        private String normalRange;
        private String resultValue;
        private Integer isAbnormal;
    }
}
