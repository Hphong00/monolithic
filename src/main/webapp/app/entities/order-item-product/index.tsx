import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import OrderItemProduct from './order-item-product';
import OrderItemProductDetail from './order-item-product-detail';
import OrderItemProductUpdate from './order-item-product-update';
import OrderItemProductDeleteDialog from './order-item-product-delete-dialog';

const OrderItemProductRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<OrderItemProduct />} />
    <Route path="new" element={<OrderItemProductUpdate />} />
    <Route path=":id">
      <Route index element={<OrderItemProductDetail />} />
      <Route path="edit" element={<OrderItemProductUpdate />} />
      <Route path="delete" element={<OrderItemProductDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default OrderItemProductRoutes;
