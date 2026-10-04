package boy.wang.doctorservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class CiReportSaveRequest {

    private Long orderId;
    private Long checkitemId;
    private List<Detail> details;

    @Data
    public static class Detail {
        private Long checkitemdetailedId;
        private String resultValue;
    }
}
