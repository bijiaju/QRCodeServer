package com.hp.docker_base.mapper;

import com.hp.docker_base.bean.OrderDir;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {

	List<OrderDir> selectAll();

	int insertOrder(OrderDir orderDir);

	OrderDir selectOrderByOrderId(String orderName);

	int delOrderInfo(String orderName);

	int updateOrderByOrderId(OrderDir orderDir);

}
