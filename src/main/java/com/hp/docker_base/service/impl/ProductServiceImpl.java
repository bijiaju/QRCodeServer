package com.hp.docker_base.service.impl;

import com.hp.docker_base.bean.Product;
import com.hp.docker_base.mapper.ProductMapper;
import com.hp.docker_base.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Product> selectAll() {
        return productMapper.selectAll();
    }


}
