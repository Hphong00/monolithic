package com.web.monolithic.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.web.monolithic.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CartItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CartItem.class);
        CartItem cartItem1 = new CartItem();
        cartItem1.setId(UUID.randomUUID());
        CartItem cartItem2 = new CartItem();
        cartItem2.setId(cartItem1.getId());
        assertThat(cartItem1).isEqualTo(cartItem2);
        cartItem2.setId(UUID.randomUUID());
        assertThat(cartItem1).isNotEqualTo(cartItem2);
        cartItem1.setId(null);
        assertThat(cartItem1).isNotEqualTo(cartItem2);
    }
}
