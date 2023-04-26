import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './shipping.reducer';

export const ShippingDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const shippingEntity = useAppSelector(state => state.shipping.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="shippingDetailsHeading">
          <Translate contentKey="webApp.shipping.detail.title">Shipping</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="webApp.shipping.id">Id</Translate>
            </span>
          </dt>
          <dd>{shippingEntity.id}</dd>
          <dt>
            <span id="userId">
              <Translate contentKey="webApp.shipping.userId">User Id</Translate>
            </span>
          </dt>
          <dd>{shippingEntity.userId}</dd>
          <dt>
            <span id="firstName">
              <Translate contentKey="webApp.shipping.firstName">First Name</Translate>
            </span>
          </dt>
          <dd>{shippingEntity.firstName}</dd>
          <dt>
            <span id="lastName">
              <Translate contentKey="webApp.shipping.lastName">Last Name</Translate>
            </span>
          </dt>
          <dd>{shippingEntity.lastName}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="webApp.shipping.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{shippingEntity.phone}</dd>
          <dt>
            <span id="address">
              <Translate contentKey="webApp.shipping.address">Address</Translate>
            </span>
          </dt>
          <dd>{shippingEntity.address}</dd>
          <dt>
            <span id="city">
              <Translate contentKey="webApp.shipping.city">City</Translate>
            </span>
          </dt>
          <dd>{shippingEntity.city}</dd>
          <dt>
            <span id="state">
              <Translate contentKey="webApp.shipping.state">State</Translate>
            </span>
          </dt>
          <dd>{shippingEntity.state}</dd>
          <dt>
            <span id="country">
              <Translate contentKey="webApp.shipping.country">Country</Translate>
            </span>
          </dt>
          <dd>{shippingEntity.country}</dd>
          <dt>
            <span id="postalCode">
              <Translate contentKey="webApp.shipping.postalCode">Postal Code</Translate>
            </span>
          </dt>
          <dd>{shippingEntity.postalCode}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="webApp.shipping.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{shippingEntity.createdAt ? <TextFormat value={shippingEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="webApp.shipping.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>{shippingEntity.updatedAt ? <TextFormat value={shippingEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/shipping" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/shipping/${shippingEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ShippingDetail;
