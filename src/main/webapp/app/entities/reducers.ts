import cart from 'app/entities/cart/cart.reducer';
import cartItem from 'app/entities/cart-item/cart-item.reducer';
import product from 'app/entities/product/product.reducer';
import order from 'app/entities/order/order.reducer';
import orderItem from 'app/entities/order-item/order-item.reducer';
import orderItemProduct from 'app/entities/order-item-product/order-item-product.reducer';
import shipping from 'app/entities/shipping/shipping.reducer';
import payment from 'app/entities/payment/payment.reducer';
import notification from 'app/entities/notification/notification.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  cart,
  cartItem,
  product,
  order,
  orderItem,
  orderItemProduct,
  shipping,
  payment,
  notification,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
