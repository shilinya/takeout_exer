package com.sky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;
import com.sky.result.Result;

public interface EmployeeService extends IService<Employee> {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);



    /**
     * 新增员工
     * @param employeeDTO
     */
    void insertEmployee(EmployeeDTO employeeDTO);

    /**
     * 分页查询员工
     * @param employeePageQueryDTO
     * @return
     */
    Result<PageResult> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);


    /**
     * 根据DIO修改员工信息
     * @param employeeDTO
     */
    void updateEmployee(EmployeeDTO employeeDTO);
}
