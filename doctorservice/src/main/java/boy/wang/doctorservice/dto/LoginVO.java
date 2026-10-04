package boy.wang.doctorservice.dto;

import lombok.Data;

@Data
public class LoginVO {

    private String token;
    private DoctorVO doctorInfo;
}
