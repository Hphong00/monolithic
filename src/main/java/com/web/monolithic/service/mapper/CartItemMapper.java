package com.web.monolithic.service.mapper;

import com.web.monolithic.domain.CartItem;
import com.web.monolithic.service.dto.CartItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CartItem} and its DTO {@link CartItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface CartItemMapper extends EntityMapper<CartItemDTO, CartItem> {}
