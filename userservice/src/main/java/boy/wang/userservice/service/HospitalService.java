package boy.wang.userservice.service;

import boy.wang.userservice.entity.Hospital;

import java.util.List;

public interface HospitalService {

    List<Hospital> getHospitalList();

    Hospital getHospitalDetail(Long id);
}
