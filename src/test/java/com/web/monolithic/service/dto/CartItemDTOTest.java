package com.web.monolithic.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.web.monolithic.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CartItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CartItemDTO.class);
        CartItemDTO cartItemDTO1 = new CartItemDTO();
        cartItemDTO1.setId(UUID.randomUUID());
        CartItemDTO cartItemDTO2 = new CartItemDTO();
        assertThat(cartItemDTO1).isNotEqualTo(cartItemDTO2);
        cartItemDTO2.setId(cartItemDTO1.getId());
        assertThat(cartItemDTO1).isEqualTo(cartItemDTO2);
        cartItemDTO2.setId(UUID.randomUUID());
        assertThat(cartItemDTO1).isNotEqualTo(cartItemDTO2);
        cartItemDTO1.setId(null);
        assertThat(cartItemDTO1).isNotEqualTo(cartItemDTO2);
    }
}
