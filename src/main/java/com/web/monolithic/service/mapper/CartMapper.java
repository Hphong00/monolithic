package com.web.monolithic.service.mapper;

import com.web.monolithic.domain.Cart;
import com.web.monolithic.service.dto.CartDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Cart} and its DTO {@link CartDTO}.
 */
@Mapper(componentModel = "spring")
public interface CartMapper extends EntityMapper<CartDTO, Cart> {}
