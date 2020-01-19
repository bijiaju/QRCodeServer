package com.hp.docker_base.service;

import com.hp.docker_base.bean.OrderDir;

import java.util.List;

public interface OrderService {

    List<OrderDir> selectAll();

    int insertOrder(OrderDir orderDir);

    OrderDir selectOrderByOrderId(String orderName);

    int delOrderInfo(String orderName);

    int updateOrderByOrderId(OrderDir orderDir);
}
