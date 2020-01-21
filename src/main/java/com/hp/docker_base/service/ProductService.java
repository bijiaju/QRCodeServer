package com.hp.docker_base.service;

import com.hp.docker_base.bean.Product;

import java.util.List;

public interface ProductService {

    List<Product> selectAll();
}
