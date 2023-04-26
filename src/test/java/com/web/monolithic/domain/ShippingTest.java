package com.web.monolithic.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.web.monolithic.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ShippingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Shipping.class);
        Shipping shipping1 = new Shipping();
        shipping1.setId(UUID.randomUUID());
        Shipping shipping2 = new Shipping();
        shipping2.setId(shipping1.getId());
        assertThat(shipping1).isEqualTo(shipping2);
        shipping2.setId(UUID.randomUUID());
        assertThat(shipping1).isNotEqualTo(shipping2);
        shipping1.setId(null);
        assertThat(shipping1).isNotEqualTo(shipping2);
    }
}
