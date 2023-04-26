import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Shipping from './shipping';
import ShippingDetail from './shipping-detail';
import ShippingUpdate from './shipping-update';
import ShippingDeleteDialog from './shipping-delete-dialog';

const ShippingRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Shipping />} />
    <Route path="new" element={<ShippingUpdate />} />
    <Route path=":id">
      <Route index element={<ShippingDetail />} />
      <Route path="edit" element={<ShippingUpdate />} />
      <Route path="delete" element={<ShippingDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ShippingRoutes;
