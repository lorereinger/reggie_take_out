package com.itheima.reggie.dto;

import com.itheima.reggie.entity.OrderDetail;
import lombok.Data;

import java.util.List;

@Data
public class OrdersDto {
    private int sumNum;
    private List<OrderDetail> orderDetails;
}
