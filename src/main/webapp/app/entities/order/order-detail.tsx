import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './order.reducer';

export const OrderDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const orderEntity = useAppSelector(state => state.order.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="orderDetailsHeading">
          <Translate contentKey="webApp.order.detail.title">Order</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="webApp.order.id">Id</Translate>
            </span>
          </dt>
          <dd>{orderEntity.id}</dd>
          <dt>
            <span id="userId">
              <Translate contentKey="webApp.order.userId">User Id</Translate>
            </span>
          </dt>
          <dd>{orderEntity.userId}</dd>
          <dt>
            <span id="shippingId">
              <Translate contentKey="webApp.order.shippingId">Shipping Id</Translate>
            </span>
          </dt>
          <dd>{orderEntity.shippingId}</dd>
          <dt>
            <span id="paymentId">
              <Translate contentKey="webApp.order.paymentId">Payment Id</Translate>
            </span>
          </dt>
          <dd>{orderEntity.paymentId}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="webApp.order.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{orderEntity.createdAt ? <TextFormat value={orderEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="webApp.order.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>{orderEntity.updatedAt ? <TextFormat value={orderEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/order" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/order/${orderEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default OrderDetail;
