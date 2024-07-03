package com.sky.controller.admin;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "后台员工管理接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @ApiOperation(value = "员工登录接口")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {


        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @ApiOperation(value = "员工退出登录接口")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     * @param employeeDTO
     * @return
     */
    @ApiOperation(value = "新增员工")
    @PostMapping()
    public Result save(@RequestBody EmployeeDTO employeeDTO) {
        employeeService.insertEmployee(employeeDTO);
        return Result.success();
    }

    /**
     * 分页查询员工
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("员工分页查询")
    public Result<PageResult> queryEmployee(EmployeePageQueryDTO employeePageQueryDTO) {
        return employeeService.pageQuery(employeePageQueryDTO);

    }
    /**
     * 修改账号状态———启用或禁用
     */
    @ApiOperation("修改员工账号的状态")
    @PostMapping("/status/{status}")
    public Result updateStatus(@PathVariable Integer status,Long id) {
        if(status!=null){
        LambdaUpdateWrapper<Employee> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Employee::getId, id);
        wrapper.set(Employee::getStatus, status);

        employeeService.update(wrapper);
        }
        return Result.success();
    }

    /**
     * 根据员工id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据员工id查询员工信息")
    public Result<Employee> getById(@PathVariable Long id) {
        Employee result = employeeService.getById(id);
        return Result.success(result);
    }

    /**
     * 编辑员工信息4
     * @param employeeDTO
     * @return
     */
    @PutMapping
    @ApiOperation("编辑员工信息")
    public Result updateEmployee(@RequestBody EmployeeDTO employeeDTO) {
        employeeService.updateEmployee(employeeDTO);
        return Result.success();
    }

}