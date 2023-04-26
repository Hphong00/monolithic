import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IPayment } from 'app/shared/model/payment.model';
import { getEntity, updateEntity, createEntity, reset } from './payment.reducer';

export const PaymentUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const paymentEntity = useAppSelector(state => state.payment.entity);
  const loading = useAppSelector(state => state.payment.loading);
  const updating = useAppSelector(state => state.payment.updating);
  const updateSuccess = useAppSelector(state => state.payment.updateSuccess);

  const handleClose = () => {
    navigate('/payment');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...paymentEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          createdAt: displayDefaultDateTime(),
          updatedAt: displayDefaultDateTime(),
        }
      : {
          ...paymentEntity,
          createdAt: convertDateTimeFromServer(paymentEntity.createdAt),
          updatedAt: convertDateTimeFromServer(paymentEntity.updatedAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="webApp.payment.home.createOrEditLabel" data-cy="PaymentCreateUpdateHeading">
            <Translate contentKey="webApp.payment.home.createOrEditLabel">Create or edit a Payment</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="payment-id"
                  label={translate('webApp.payment.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField label={translate('webApp.payment.userId')} id="payment-userId" name="userId" data-cy="userId" type="text" />
              <ValidatedField
                label={translate('webApp.payment.cardNumber')}
                id="payment-cardNumber"
                name="cardNumber"
                data-cy="cardNumber"
                type="text"
              />
              <ValidatedField
                label={translate('webApp.payment.cardHolderName')}
                id="payment-cardHolderName"
                name="cardHolderName"
                data-cy="cardHolderName"
                type="text"
              />
              <ValidatedField
                label={translate('webApp.payment.cardExpirationMonth')}
                id="payment-cardExpirationMonth"
                name="cardExpirationMonth"
                data-cy="cardExpirationMonth"
                type="text"
              />
              <ValidatedField
                label={translate('webApp.payment.cardExpirationYear')}
                id="payment-cardExpirationYear"
                name="cardExpirationYear"
                data-cy="cardExpirationYear"
                type="text"
              />
              <ValidatedField
                label={translate('webApp.payment.cardCVV')}
                id="payment-cardCVV"
                name="cardCVV"
                data-cy="cardCVV"
                type="text"
              />
              <ValidatedField
                label={translate('webApp.payment.createdAt')}
                id="payment-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('webApp.payment.updatedAt')}
                id="payment-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/payment" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default PaymentUpdate;
