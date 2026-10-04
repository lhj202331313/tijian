package boy.wang.userservice.service.impl;

import boy.wang.userservice.common.ResultCode;
import boy.wang.userservice.entity.Hospital;
import boy.wang.userservice.exception.BusinessException;
import boy.wang.userservice.mapper.HospitalMapper;
import boy.wang.userservice.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HospitalServiceImpl implements HospitalService {

    private final HospitalMapper hospitalMapper;

    @Autowired
    public HospitalServiceImpl(HospitalMapper hospitalMapper) {
        this.hospitalMapper = hospitalMapper;
    }

    @Override
    public List<Hospital> getHospitalList() {
        return hospitalMapper.selectAllActive();
    }

    @Override
    public Hospital getHospitalDetail(Long id) {
        Hospital hospital = hospitalMapper.selectByIdWithSetmeals(id);
        if (hospital == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "医院不存在");
        }
        return hospital;
    }
}
