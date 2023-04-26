package com.web.monolithic.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.web.monolithic.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ShippingDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ShippingDTO.class);
        ShippingDTO shippingDTO1 = new ShippingDTO();
        shippingDTO1.setId(UUID.randomUUID());
        ShippingDTO shippingDTO2 = new ShippingDTO();
        assertThat(shippingDTO1).isNotEqualTo(shippingDTO2);
        shippingDTO2.setId(shippingDTO1.getId());
        assertThat(shippingDTO1).isEqualTo(shippingDTO2);
        shippingDTO2.setId(UUID.randomUUID());
        assertThat(shippingDTO1).isNotEqualTo(shippingDTO2);
        shippingDTO1.setId(null);
        assertThat(shippingDTO1).isNotEqualTo(shippingDTO2);
    }
}
