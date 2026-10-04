package boy.wang.userservice.service.impl;

import boy.wang.userservice.common.ResultCode;
import boy.wang.userservice.dto.OverallResultVO;
import boy.wang.userservice.dto.ReportVO;
import boy.wang.userservice.entity.CiDetailedReport;
import boy.wang.userservice.entity.CiReport;
import boy.wang.userservice.entity.OverallResult;
import boy.wang.userservice.entity.Orders;
import boy.wang.userservice.exception.BusinessException;
import boy.wang.userservice.mapper.CiDetailedReportMapper;
import boy.wang.userservice.mapper.CiReportMapper;
import boy.wang.userservice.mapper.OrdersMapper;
import boy.wang.userservice.mapper.OverallResultMapper;
import boy.wang.userservice.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final OrdersMapper ordersMapper;
    private final CiReportMapper ciReportMapper;
    private final CiDetailedReportMapper ciDetailedReportMapper;
    private final OverallResultMapper overallResultMapper;

    @Autowired
    public ReportServiceImpl(OrdersMapper ordersMapper, CiReportMapper ciReportMapper,
                            CiDetailedReportMapper ciDetailedReportMapper,
                            OverallResultMapper overallResultMapper) {
        this.ordersMapper = ordersMapper;
        this.ciReportMapper = ciReportMapper;
        this.ciDetailedReportMapper = ciDetailedReportMapper;
        this.overallResultMapper = overallResultMapper;
    }

    @Override
    public ReportVO getReport(Long orderId) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "订单不存在");
        }
        ReportVO vo = new ReportVO();
        vo.setOrderId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setHospitalName(order.getHospitalName());
        vo.setSetmealName(order.getSetmealName());
        vo.setOrderDate(order.getOrderDate());
        vo.setStatus(order.getOrderStatus());

        List<CiReport> ciReports = ciReportMapper.selectByOrderId(orderId);
        List<ReportVO.ItemReport> items = new ArrayList<>();
        for (CiReport ciReport : ciReports) {
            ReportVO.ItemReport item = new ReportVO.ItemReport();
            item.setCheckitemId(ciReport.getCheckitemId());
            item.setCheckitemName(ciReport.getCheckitemName());
            item.setCheckitemType(ciReport.getCheckitemType());
            item.setResultStatus(ciReport.getResultStatus());
            item.setReportTime(ciReport.getReportTime());

            List<CiDetailedReport> details = ciDetailedReportMapper.selectByCireportId(ciReport.getId());
            List<ReportVO.DetailReport> detailList = new ArrayList<>();
            for (CiDetailedReport cdr : details) {
                ReportVO.DetailReport detail = new ReportVO.DetailReport();
                detail.setId(cdr.getId());
                detail.setName(cdr.getName());
                detail.setUnit(cdr.getUnit());
                detail.setNormalRange(cdr.getNormalRange());
                detail.setResultValue(cdr.getResultValue());
                detail.setIsAbnormal(cdr.getIsAbnormal());
                detailList.add(detail);
            }
            item.setDetails(detailList);
            items.add(item);
        }
        vo.setItems(items);
        return vo;
    }

    @Override
    public OverallResultVO getOverallResult(Long orderId) {
        OverallResult overallResult = overallResultMapper.selectPublishedByOrderId(orderId);
        if (overallResult == null) {
            return null;
        }
        OverallResultVO vo = new OverallResultVO();
        vo.setId(overallResult.getId());
        vo.setOrderId(overallResult.getOrderId());
        vo.setSummary(overallResult.getSummary());
        vo.setDoctorName(overallResult.getDoctorName());
        vo.setResultStatus(overallResult.getResultStatus());
        vo.setPublishTime(overallResult.getPublishTime());
        return vo;
    }
}
