import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './order-item-product.reducer';

export const OrderItemProductDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const orderItemProductEntity = useAppSelector(state => state.orderItemProduct.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="orderItemProductDetailsHeading">
          <Translate contentKey="webApp.orderItemProduct.detail.title">OrderItemProduct</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="webApp.orderItemProduct.id">Id</Translate>
            </span>
          </dt>
          <dd>{orderItemProductEntity.id}</dd>
          <dt>
            <span id="orderItemId">
              <Translate contentKey="webApp.orderItemProduct.orderItemId">Order Item Id</Translate>
            </span>
          </dt>
          <dd>{orderItemProductEntity.orderItemId}</dd>
          <dt>
            <span id="productId">
              <Translate contentKey="webApp.orderItemProduct.productId">Product Id</Translate>
            </span>
          </dt>
          <dd>{orderItemProductEntity.productId}</dd>
          <dt>
            <span id="quantity">
              <Translate contentKey="webApp.orderItemProduct.quantity">Quantity</Translate>
            </span>
          </dt>
          <dd>{orderItemProductEntity.quantity}</dd>
        </dl>
        <Button tag={Link} to="/order-item-product" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/order-item-product/${orderItemProductEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default OrderItemProductDetail;
