import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './cart.reducer';

export const CartDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const cartEntity = useAppSelector(state => state.cart.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="cartDetailsHeading">
          <Translate contentKey="webApp.cart.detail.title">Cart</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="webApp.cart.id">Id</Translate>
            </span>
          </dt>
          <dd>{cartEntity.id}</dd>
          <dt>
            <span id="userId">
              <Translate contentKey="webApp.cart.userId">User Id</Translate>
            </span>
          </dt>
          <dd>{cartEntity.userId}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="webApp.cart.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{cartEntity.createdAt ? <TextFormat value={cartEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="webApp.cart.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>{cartEntity.updatedAt ? <TextFormat value={cartEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/cart" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/cart/${cartEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CartDetail;
