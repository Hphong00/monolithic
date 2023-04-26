import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Cart from './cart';
import CartItem from './cart-item';
import Product from './product';
import Order from './order';
import OrderItem from './order-item';
import OrderItemProduct from './order-item-product';
import Shipping from './shipping';
import Payment from './payment';
import Notification from './notification';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="cart/*" element={<Cart />} />
        <Route path="cart-item/*" element={<CartItem />} />
        <Route path="product/*" element={<Product />} />
        <Route path="order/*" element={<Order />} />
        <Route path="order-item/*" element={<OrderItem />} />
        <Route path="order-item-product/*" element={<OrderItemProduct />} />
        <Route path="shipping/*" element={<Shipping />} />
        <Route path="payment/*" element={<Payment />} />
        <Route path="notification/*" element={<Notification />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
