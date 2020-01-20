package com.hp.docker_base.service;

import com.hp.docker_base.bean.OrderDir;
import com.hp.docker_base.bean.User;

import java.util.HashMap;
import java.util.List;

public interface UserService {


    int insertUser(User user);

    User selectUserByNameAndPassword(String name,String password);

  /*  List<OrderDir> selectAll();

    int insertOrder(OrderDir orderDir);

    OrderDir selectOrderByOrderId(String orderName);

    int delOrderInfo(String orderName);

    int updateOrderByOrderId(OrderDir orderDir);*/
}
