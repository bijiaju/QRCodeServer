package com.hp.docker_base.mapper;

import com.hp.docker_base.bean.Product;
import com.hp.docker_base.bean.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface ProductMapper {

	List<Product> selectAll();

	/*int insertUser(User user);

	User selectUserByNameAndPassword(HashMap<String, Object> params);*/

	/*List<OrderDir> selectAll();



	OrderDir selectOrderByOrderId(String orderName);

	int delOrderInfo(String orderName);

	int updateOrderByOrderId(OrderDir orderDir);*/

}
