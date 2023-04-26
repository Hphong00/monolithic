package com.web.monolithic.service.mapper;

import com.web.monolithic.domain.Shipping;
import com.web.monolithic.service.dto.ShippingDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Shipping} and its DTO {@link ShippingDTO}.
 */
@Mapper(componentModel = "spring")
public interface ShippingMapper extends EntityMapper<ShippingDTO, Shipping> {}
