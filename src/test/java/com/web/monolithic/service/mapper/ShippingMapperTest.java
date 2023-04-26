package com.web.monolithic.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShippingMapperTest {

    private ShippingMapper shippingMapper;

    @BeforeEach
    public void setUp() {
        shippingMapper = new ShippingMapperImpl();
    }
}
