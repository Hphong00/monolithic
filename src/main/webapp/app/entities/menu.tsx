import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/cart">
        <Translate contentKey="global.menu.entities.cart" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/cart-item">
        <Translate contentKey="global.menu.entities.cartItem" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/product">
        <Translate contentKey="global.menu.entities.product" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/order">
        <Translate contentKey="global.menu.entities.order" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/order-item">
        <Translate contentKey="global.menu.entities.orderItem" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/order-item-product">
        <Translate contentKey="global.menu.entities.orderItemProduct" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/shipping">
        <Translate contentKey="global.menu.entities.shipping" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/payment">
        <Translate contentKey="global.menu.entities.payment" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/notification">
        <Translate contentKey="global.menu.entities.notification" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
