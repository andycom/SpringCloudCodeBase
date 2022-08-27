package com.fancv.service.impl;

import com.fancv.dao.OrderDao;
import com.fancv.dto.AccountReduceBalanceDTO;
import com.fancv.dto.ProductReduceStockDTO;
import com.fancv.entity.OrderDO;
import com.fancv.feign.AccountServiceFeignClient;
import com.fancv.feign.ProductServiceFeignClient;
import com.fancv.service.OrderService;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private AccountServiceFeignClient accountService;
    @Autowired
    private ProductServiceFeignClient productService;
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private LoadBalancerClient loadBalancerClient;


    @Override
    @GlobalTransactional
    public Integer createOrder(Long userId, Long productId, Integer price) {
        Integer amount = 1; // 购买数量，暂时设置为 1。

        logger.info("[createOrder] 当前 XID: {}", RootContext.getXID());
        //debug
        ServiceInstance instance;
        if (true) {
            // 获取服务 `demo-provider` 对应的实例列表
            List<ServiceInstance> instances = discoveryClient.getInstances("product-service");
            // 选择第一个
            instance = instances.size() > 0 ? instances.get(0) : null;
        } else {
            instance = loadBalancerClient.choose("demo-provider");
        }
        // 发起调用
        if (instance == null) {
            throw new IllegalStateException("获取不到实例");
        }
        String targetUrl = instance.getUri() + "";


        // 扣减库存
        productService.reduceStock(new ProductReduceStockDTO().setProductId(productId).setAmount(amount));

        // 扣减余额
        accountService.reduceBalance(new AccountReduceBalanceDTO().setUserId(userId).setPrice(price));

        // 保存订单
        OrderDO order = new OrderDO().setUserId(userId).setProductId(productId).setPayAmount(amount * price);
        orderDao.saveOrder(order);
        logger.info("[createOrder] 保存订单: {}", order.getId());

        // 返回订单编号
        return order.getId();
    }

}
