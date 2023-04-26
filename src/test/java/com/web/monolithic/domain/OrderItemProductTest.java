package com.web.monolithic.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.web.monolithic.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrderItemProductTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderItemProduct.class);
        OrderItemProduct orderItemProduct1 = new OrderItemProduct();
        orderItemProduct1.setId(UUID.randomUUID());
        OrderItemProduct orderItemProduct2 = new OrderItemProduct();
        orderItemProduct2.setId(orderItemProduct1.getId());
        assertThat(orderItemProduct1).isEqualTo(orderItemProduct2);
        orderItemProduct2.setId(UUID.randomUUID());
        assertThat(orderItemProduct1).isNotEqualTo(orderItemProduct2);
        orderItemProduct1.setId(null);
        assertThat(orderItemProduct1).isNotEqualTo(orderItemProduct2);
    }
}
