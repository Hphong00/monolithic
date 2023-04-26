package com.web.monolithic.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.web.monolithic.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrderItemProductDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderItemProductDTO.class);
        OrderItemProductDTO orderItemProductDTO1 = new OrderItemProductDTO();
        orderItemProductDTO1.setId(UUID.randomUUID());
        OrderItemProductDTO orderItemProductDTO2 = new OrderItemProductDTO();
        assertThat(orderItemProductDTO1).isNotEqualTo(orderItemProductDTO2);
        orderItemProductDTO2.setId(orderItemProductDTO1.getId());
        assertThat(orderItemProductDTO1).isEqualTo(orderItemProductDTO2);
        orderItemProductDTO2.setId(UUID.randomUUID());
        assertThat(orderItemProductDTO1).isNotEqualTo(orderItemProductDTO2);
        orderItemProductDTO1.setId(null);
        assertThat(orderItemProductDTO1).isNotEqualTo(orderItemProductDTO2);
    }
}
