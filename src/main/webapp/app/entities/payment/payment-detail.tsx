import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './payment.reducer';

export const PaymentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const paymentEntity = useAppSelector(state => state.payment.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="paymentDetailsHeading">
          <Translate contentKey="webApp.payment.detail.title">Payment</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="webApp.payment.id">Id</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.id}</dd>
          <dt>
            <span id="userId">
              <Translate contentKey="webApp.payment.userId">User Id</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.userId}</dd>
          <dt>
            <span id="cardNumber">
              <Translate contentKey="webApp.payment.cardNumber">Card Number</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.cardNumber}</dd>
          <dt>
            <span id="cardHolderName">
              <Translate contentKey="webApp.payment.cardHolderName">Card Holder Name</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.cardHolderName}</dd>
          <dt>
            <span id="cardExpirationMonth">
              <Translate contentKey="webApp.payment.cardExpirationMonth">Card Expiration Month</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.cardExpirationMonth}</dd>
          <dt>
            <span id="cardExpirationYear">
              <Translate contentKey="webApp.payment.cardExpirationYear">Card Expiration Year</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.cardExpirationYear}</dd>
          <dt>
            <span id="cardCVV">
              <Translate contentKey="webApp.payment.cardCVV">Card CVV</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.cardCVV}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="webApp.payment.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.createdAt ? <TextFormat value={paymentEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="webApp.payment.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>{paymentEntity.updatedAt ? <TextFormat value={paymentEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/payment" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/payment/${paymentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default PaymentDetail;
