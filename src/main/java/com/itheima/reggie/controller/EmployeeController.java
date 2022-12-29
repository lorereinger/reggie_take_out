package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Emp;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmpService;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.SocketUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmpService empService;

    /**
     * 员工登录
     *
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //根据用户名和密码查询
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        if (emp == null) {
            return R.error("用户名不存在");
        }
        if (!emp.getPassword().equals(password)) {
            return R.error("密码错误");
        }
        //查看员工是否被禁用
        if (emp.getStatus() == 0) {
            return R.error("该员工已被禁用");
        }
        //登录成功，将员工信息存入session
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出登录
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工信息
     *
     * @param
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工信息:{}", employee.toString());
        //设置默认密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
/*        //设置创建时间
        employee.setCreateTime(LocalDateTime.now());
        //设置更新时间
        employee.setUpdateTime(LocalDateTime.now());
        //设置创建人
        employee.setCreateUser((Long) request.getSession().getAttribute("employee"));
        //设置更新人
        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));*/
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("员工信息分页查询:page={},pageSize={},name={}", page, pageSize, name);
        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Emp> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Emp::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Emp::getUpdateTime);
        //执行查询
        empService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("修改员工信息:{}", employee.toString());

        //输出线程id
        log.info("线程id:{}",Thread.currentThread().getId());
       /* //设置更新时间
        employee.setUpdateTime(LocalDateTime.now());
        //设置更新人
        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));*/

        employeeService.updateById(employee);
        employeeService.setDisable();
        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息:{}",id);
        Employee employee = employeeService.getById(id);
        if (employee == null){
            return R.error("该员工不存在");
        }
        return R.success(employee);
    }
}
