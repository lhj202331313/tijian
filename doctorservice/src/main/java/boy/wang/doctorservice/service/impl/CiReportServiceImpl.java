package boy.wang.doctorservice.service.impl;

import boy.wang.doctorservice.common.BusinessException;
import boy.wang.doctorservice.common.ResultCode;
import boy.wang.doctorservice.dto.CiDetailedReportVO;
import boy.wang.doctorservice.dto.CiReportSaveRequest;
import boy.wang.doctorservice.dto.CiReportVO;
import boy.wang.doctorservice.entity.Checkitem;
import boy.wang.doctorservice.entity.CheckitemDetailed;
import boy.wang.doctorservice.entity.CiDetailedReport;
import boy.wang.doctorservice.entity.CiReport;
import boy.wang.doctorservice.mapper.CheckitemDetailedMapper;
import boy.wang.doctorservice.mapper.CheckitemMapper;
import boy.wang.doctorservice.mapper.CiDetailedReportMapper;
import boy.wang.doctorservice.mapper.CiReportMapper;
import boy.wang.doctorservice.service.CiReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CiReportServiceImpl implements CiReportService {

    @Autowired
    private CiReportMapper ciReportMapper;

    @Autowired
    private CiDetailedReportMapper ciDetailedReportMapper;

    @Autowired
    private CheckitemDetailedMapper checkitemDetailedMapper;

    @Autowired
    private CheckitemMapper checkitemMapper;

    @Override
    public List<CiReportVO> listByOrderId(Long orderId) {
        return ciReportMapper.findVOByOrderId(orderId);
    }

    @Override
    public CiReportVO getByOrderAndCheckitem(Long orderId, Long checkitemId) {
        CiReportVO vo = ciReportMapper.findVOByOrderAndCheckitem(orderId, checkitemId);
        // 指标模板（检查项配置的全部指标，结果留空）
        List<CiDetailedReportVO> templateDetails = buildTemplateDetails(checkitemId);
        if (vo != null) {
            // 报告已存在：用模板补齐尚未录入的指标（兼容历史空报告/后补的指标配置）
            mergeTemplateDetails(vo, templateDetails);
            return vo;
        }
        // 报告尚未录入：返回检查项指标模板（结果为空），供医生录入
        CiReportVO template = new CiReportVO();
        template.setOrderId(orderId);
        template.setCheckitemId(checkitemId);
        Checkitem checkitem = checkitemMapper.findById(checkitemId);
        if (checkitem != null) {
            template.setCheckitemName(checkitem.getName());
        }
        template.setDetails(templateDetails);
        return template;
    }

    /**
     * 将模板中存在但报告明细里缺失的指标行补入 vo.details
     */
    private void mergeTemplateDetails(CiReportVO vo, List<CiDetailedReportVO> templateDetails) {
        List<CiDetailedReportVO> existing = vo.getDetails() == null
                ? new ArrayList<>() : vo.getDetails();
        java.util.Set<Long> existingIds = new java.util.HashSet<>();
        for (CiDetailedReportVO d : existing) {
            if (d.getCheckitemdetailedId() != null) {
                existingIds.add(d.getCheckitemdetailedId());
            }
        }
        for (CiDetailedReportVO t : templateDetails) {
            if (!existingIds.contains(t.getCheckitemdetailedId())) {
                existing.add(t);
            }
        }
        vo.setDetails(existing);
    }

    /**
     * 构建指标模板行：带出指标名称、单位、参考范围（检查结果留空）
     */
    private List<CiDetailedReportVO> buildTemplateDetails(Long checkitemId) {
        List<CiDetailedReportVO> details = new ArrayList<>();
        List<CheckitemDetailed> detailedList = checkitemDetailedMapper.findByCheckitemId(checkitemId);
        if (detailedList != null) {
            for (CheckitemDetailed d : detailedList) {
                CiDetailedReportVO row = new CiDetailedReportVO();
                row.setCheckitemdetailedId(d.getId());
                row.setName(d.getName());
                row.setUnit(d.getUnit());
                row.setNormalRange(d.getNormalRange());
                row.setResultValue("");
                details.add(row);
            }
        }
        return details;
    }

    @Override
    @Transactional
    public void saveReport(CiReportSaveRequest request) {
        if (request == null || request.getOrderId() == null || request.getCheckitemId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "orderId 和 checkitemId 不能为空");
        }

        CiReport ciReport = ciReportMapper.findEntityByOrderAndCheckitem(request.getOrderId(), request.getCheckitemId());
        if (ciReport == null) {
            ciReport = new CiReport();
            ciReport.setOrderId(request.getOrderId());
            ciReport.setCheckitemId(request.getCheckitemId());
            ciReport.setResultStatus(0);
            ciReport.setReportTime(new Date());
            ciReportMapper.insert(ciReport);
        }

        List<CheckitemDetailed> detailedList = checkitemDetailedMapper.findByCheckitemId(request.getCheckitemId());
        Map<Long, String> normalRangeMap = new HashMap<>();
        if (detailedList != null) {
            for (CheckitemDetailed d : detailedList) {
                normalRangeMap.put(d.getId(), d.getNormalRange());
            }
        }

        boolean hasAbnormal = false;
        if (request.getDetails() != null) {
            for (CiReportSaveRequest.Detail detail : request.getDetails()) {
                String normalRange = normalRangeMap.get(detail.getCheckitemdetailedId());
                int isAbnormal = checkAbnormal(normalRange, detail.getResultValue());
                if (isAbnormal == 1) {
                    hasAbnormal = true;
                }
                CiDetailedReport existing = ciDetailedReportMapper.findByCireportAndDetail(
                        ciReport.getId(), detail.getCheckitemdetailedId());
                if (existing != null) {
                    ciDetailedReportMapper.updateResult(existing.getId(), detail.getResultValue(), isAbnormal);
                } else {
                    CiDetailedReport cdr = new CiDetailedReport();
                    cdr.setCireportId(ciReport.getId());
                    cdr.setCheckitemdetailedId(detail.getCheckitemdetailedId());
                    cdr.setResultValue(detail.getResultValue());
                    cdr.setIsAbnormal(isAbnormal);
                    cdr.setCreateTime(new Date());
                    ciDetailedReportMapper.insert(cdr);
                }
            }
        }

        ciReportMapper.updateStatusAndTime(ciReport.getId(), hasAbnormal ? 1 : 0, new Date());
    }

    /**
     * 异常判断：根据 normal_range 与 result_value 比较。
     * 返回 0 正常，1 异常。任何解析异常均默认 0 正常。
     */
    public static int checkAbnormal(String normalRange, String resultValue) {
        if (normalRange == null || normalRange.trim().isEmpty()
                || resultValue == null || resultValue.trim().isEmpty()) {
            return 0;
        }
        try {
            // 血压类含 "/"，按收缩压/舒张压分别比较
            if (normalRange.contains("/")) {
                String[] rangeParts = normalRange.split("/");
                String[] valueParts = resultValue.split("/");
                int len = Math.min(rangeParts.length, valueParts.length);
                for (int i = 0; i < len; i++) {
                    if (checkRangeAbnormal(rangeParts[i], valueParts[i]) == 1) {
                        return 1;
                    }
                }
                return 0;
            }
            return checkRangeAbnormal(normalRange, resultValue);
        } catch (Exception e) {
            return 0;
        }
    }

    private static int checkRangeAbnormal(String range, String value) {
        try {
            String cleanedRange = stripPrefix(range);
            String separator = cleanedRange.contains("~") ? "~" : "-";
            if (cleanedRange.contains(separator)) {
                String[] parts = cleanedRange.split(separator);
                if (parts.length == 2) {
                    double min = parseDouble(parts[0]);
                    double max = parseDouble(parts[1]);
                    double v = parseDouble(value);
                    if (v < min || v > max) {
                        return 1;
                    }
                    return 0;
                }
            }
            // 非数值范围，按文本相等判断
            return range.trim().equalsIgnoreCase(value.trim()) ? 0 : 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /** 去掉前导非数字字符（如 "男155-190" → "155-190"），无数字则原样返回。 */
    private static String stripPrefix(String s) {
        if (s == null) {
            return "";
        }
        for (int i = 0; i < s.length(); i++) {
            if (Character.isDigit(s.charAt(i)) || s.charAt(i) == '.') {
                return s.substring(i);
            }
        }
        return s;
    }

    private static double parseDouble(String s) {
        return Double.parseDouble(s.trim().replaceAll("[^0-9.]", ""));
    }
}
