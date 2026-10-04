package boy.wang.doctorservice.dto;

import lombok.Data;

/**
 * 医生个人信息更新请求（仅允许修改部分字段）
 */
@Data
public class DoctorUpdateRequest {

    private String realName;
    private String phone;
    private String department;
}
