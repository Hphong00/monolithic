package com.web.monolithic.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderItemProductMapperTest {

    private OrderItemProductMapper orderItemProductMapper;

    @BeforeEach
    public void setUp() {
        orderItemProductMapper = new OrderItemProductMapperImpl();
    }
}
