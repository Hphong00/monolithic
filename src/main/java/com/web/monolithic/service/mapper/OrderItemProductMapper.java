package com.web.monolithic.service.mapper;

import com.web.monolithic.domain.OrderItemProduct;
import com.web.monolithic.service.dto.OrderItemProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrderItemProduct} and its DTO {@link OrderItemProductDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderItemProductMapper extends EntityMapper<OrderItemProductDTO, OrderItemProduct> {}
