package com.web.monolithic.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.web.monolithic.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CartTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Cart.class);
        Cart cart1 = new Cart();
        cart1.setId(UUID.randomUUID());
        Cart cart2 = new Cart();
        cart2.setId(cart1.getId());
        assertThat(cart1).isEqualTo(cart2);
        cart2.setId(UUID.randomUUID());
        assertThat(cart1).isNotEqualTo(cart2);
        cart1.setId(null);
        assertThat(cart1).isNotEqualTo(cart2);
    }
}
