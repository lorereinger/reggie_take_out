package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Emp;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.mapper.EmpMapper;
import com.itheima.reggie.mapper.EmployeeMapper;
import com.itheima.reggie.service.EmpService;
import com.itheima.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmpServiceImpl extends ServiceImpl<EmpMapper, Emp> implements EmpService {
}
