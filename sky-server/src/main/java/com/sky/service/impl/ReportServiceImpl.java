package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private UserMapper  userMapper;
    @Autowired
    private WorkspaceService workspaceService;

    @Override
    public TurnoverReportVO getTurnoverReport(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList=new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        ArrayList<Double> turnoverList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            //查询每天的营业额，已完成的订单
            LocalDateTime beginTime=LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime=LocalDateTime.of(localDate, LocalTime.MAX);

            HashMap map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover=ordersMapper.sumByMap(map);
            turnover=turnover==null?0.0: turnover;
            turnoverList.add(turnover);


        }
        String dateString = StringUtils.join(dateList, ",");
        return TurnoverReportVO.builder()
                .dateList(dateString)
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();


    }

    @Override
    public UserReportVO getUserReport(LocalDate begin, LocalDate end) {
        ArrayList<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        //新增用户数量
        List<Integer> newUserList = new ArrayList<>();
        //总用户数量
        List<Integer>totalUserList = new ArrayList<>();

        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime=LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime=LocalDateTime.of(localDate, LocalTime.MAX);
            HashMap map = new HashMap();
            map.put("end", endTime);
            Integer total = userMapper.countByMap(map);
            totalUserList.add(total);
            map.put("begin", beginTime);
            Integer newUser = userMapper.countByMap(map);
            newUserList.add(newUser);
        }
        UserReportVO result = UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
        return result;
    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        ArrayList<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        ArrayList<Integer> totalList = new ArrayList<>();
        ArrayList<Integer> validList = new ArrayList<>();
        int count=0;
        int validCount=0;
        for (LocalDate localDate : dateList) {

            //查询每天订单总数，和有效订单数
            LocalDateTime beginTime=LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime=LocalDateTime.of(localDate, LocalTime.MAX);
            Integer total = getOrderCount(beginTime, endTime, null);
            Integer valid = getOrderCount(beginTime, endTime, Orders.COMPLETED);
            count+=total;
            validCount+=valid;
            totalList.add(total);
            validList.add(valid);


        }
        Double orderCompletionRate=0.0;
        if(count!=0){
            orderCompletionRate=(double)validCount/count;
        }
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .orderCountList(StringUtils.join(totalList,","))
                .validOrderCountList(StringUtils.join(validList,","))
                .orderCompletionRate(orderCompletionRate)
                .totalOrderCount(count)
                .validOrderCount(validCount)
                .build();

    }

    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime=LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime=LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> salesTop10 = ordersMapper.getSalesTop10(beginTime, endTime);
        List<String> name = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameString = StringUtils.join(name, ",");
        List<Integer> numberList = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberString = StringUtils.join(numberList, ",");
        return SalesTop10ReportVO.builder()
                .numberList(numberString)
                .nameList(nameString)
                .build();


    }

    @Override
    public void export(HttpServletResponse response) {
        //查询数据库，获取营业数据
        //最近30天
        LocalDate end = LocalDate.now().minusDays(1);
        LocalDate begin = end.minusDays(29);

        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        BusinessDataVO businessData = workspaceService.getBusinessData(beginTime, endTime);

        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            XSSFWorkbook excel = new XSSFWorkbook(in);

            //填充数据
            XSSFSheet sheetAt = excel.getSheetAt(0);
            //填充数据 时间区间
            sheetAt.getRow(1).getCell(1).setCellValue("时间："+begin+"--"+end);
            //营业额
            sheetAt.getRow(3).getCell(2).setCellValue(businessData.getTurnover());
            //订单完成率
            sheetAt.getRow(3).getCell(4).setCellValue(businessData.getOrderCompletionRate());
            //新增用户数
            sheetAt.getRow(3).getCell(6).setCellValue(businessData.getNewUsers());
            //有效订单
            sheetAt.getRow(4).getCell(2).setCellValue(businessData.getValidOrderCount());
            //平均客单价
            sheetAt.getRow(3).getCell(4).setCellValue(businessData.getUnitPrice());

            //明细数据

            for(int i=0;i<30;i++){
                LocalDate localDate = begin.plusDays(i);
                BusinessDataVO businessData1 = workspaceService.getBusinessData(LocalDateTime.of(localDate,LocalTime.MIN),LocalDateTime.of(localDate,LocalTime.MAX));
                sheetAt.getRow(7+i).getCell(1).setCellValue(String.valueOf(localDate));
                sheetAt.getRow(7+i).getCell(2).setCellValue(businessData1.getTurnover());
                sheetAt.getRow(7+i).getCell(3).setCellValue(businessData1.getValidOrderCount());
                sheetAt.getRow(7+i).getCell(4).setCellValue(businessData1.getOrderCompletionRate());
                sheetAt.getRow(7+i).getCell(5).setCellValue(businessData1.getUnitPrice());
                sheetAt.getRow(7+i).getCell(6).setCellValue(businessData1.getNewUsers());

            }

            //下载

            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);

            outputStream.close();
            in.close();
            excel.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //通过poi将数据写入excel

        //将excel文件下载到客户端浏览器
    }

    /**
     * 根据条件统计订单数量
     * @param begin
     * @param end
     * @param status
     * @return
     */
    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end,Integer status){
        Map map=new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);

        return ordersMapper.countByMap(map);
    }
}
