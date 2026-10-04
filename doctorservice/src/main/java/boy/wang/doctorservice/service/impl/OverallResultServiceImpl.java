package boy.wang.doctorservice.service.impl;

import boy.wang.doctorservice.common.BusinessException;
import boy.wang.doctorservice.common.ResultCode;
import boy.wang.doctorservice.dto.OverallResultSaveRequest;
import boy.wang.doctorservice.dto.OverallResultVO;
import boy.wang.doctorservice.entity.Doctor;
import boy.wang.doctorservice.entity.OverallResult;
import boy.wang.doctorservice.mapper.DoctorMapper;
import boy.wang.doctorservice.mapper.OverallResultMapper;
import boy.wang.doctorservice.service.OverallResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OverallResultServiceImpl implements OverallResultService {

    @Autowired
    private OverallResultMapper overallResultMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Override
    public OverallResultVO getByOrderId(Long orderId) {
        OverallResult result = overallResultMapper.findByOrderId(orderId);
        return result == null ? null : toVO(result);
    }

    @Override
    @Transactional
    public void save(OverallResultSaveRequest request, Long doctorId) {
        if (request == null || request.getOrderId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "orderId 不能为空");
        }
        OverallResult existing = overallResultMapper.findByOrderId(request.getOrderId());
        if (existing != null) {
            existing.setSummary(request.getSummary());
            existing.setResultStatus(request.getResultStatus());
            overallResultMapper.update(existing);
        } else {
            Doctor doctor = doctorMapper.findById(doctorId);
            OverallResult result = new OverallResult();
            result.setOrderId(request.getOrderId());
            result.setDoctorId(doctorId);
            result.setDoctorName(doctor == null ? null : doctor.getRealName());
            result.setSummary(request.getSummary());
            result.setResultStatus(request.getResultStatus());
            result.setStatus(0);
            result.setCreateTime(new Date());
            overallResultMapper.insert(result);
        }
    }

    @Override
    @Transactional
    public void publish(Long id) {
        OverallResult result = overallResultMapper.findById(id);
        if (result == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "总检结论不存在");
        }
        if (result.getSummary() == null || result.getSummary().trim().isEmpty()) {
            throw new BusinessException("总检结论摘要不能为空，无法发布");
        }
        overallResultMapper.updatePublish(id, new Date());
    }

    private OverallResultVO toVO(OverallResult result) {
        OverallResultVO vo = new OverallResultVO();
        vo.setId(result.getId());
        vo.setOrderId(result.getOrderId());
        vo.setDoctorId(result.getDoctorId());
        vo.setSummary(result.getSummary());
        vo.setDoctorName(result.getDoctorName());
        vo.setResultStatus(result.getResultStatus());
        vo.setCreateTime(result.getCreateTime());
        vo.setPublishTime(result.getPublishTime());
        vo.setStatus(result.getStatus());
        return vo;
    }
}
