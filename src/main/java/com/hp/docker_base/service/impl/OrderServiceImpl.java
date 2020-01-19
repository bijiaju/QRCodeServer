package com.hp.docker_base.service.impl;

import com.hp.docker_base.bean.OrderDir;
import com.hp.docker_base.mapper.OrderMapper;
import com.hp.docker_base.service.OrderService;
import com.mysql.cj.x.protobuf.MysqlxCrud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public List<OrderDir> selectAll() {
        return orderMapper.selectAll();
    }

    @Override
    public int insertOrder(OrderDir orderDir) {
        return orderMapper.insertOrder(orderDir);
    }

    @Override
    public OrderDir selectOrderByOrderId(String orderName) {
        return orderMapper.selectOrderByOrderId(orderName);
    }

    @Override
    public int delOrderInfo(String orderName) {
        return orderMapper.delOrderInfo(orderName);
    }

    @Override
    public int updateOrderByOrderId(OrderDir orderDir) {
        return orderMapper.updateOrderByOrderId(orderDir);
    }
}
