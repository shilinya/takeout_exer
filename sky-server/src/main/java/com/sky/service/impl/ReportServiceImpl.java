package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
