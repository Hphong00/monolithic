import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IShipping } from 'app/shared/model/shipping.model';
import { getEntity, updateEntity, createEntity, reset } from './shipping.reducer';

export const ShippingUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const shippingEntity = useAppSelector(state => state.shipping.entity);
  const loading = useAppSelector(state => state.shipping.loading);
  const updating = useAppSelector(state => state.shipping.updating);
  const updateSuccess = useAppSelector(state => state.shipping.updateSuccess);

  const handleClose = () => {
    navigate('/shipping');
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
      ...shippingEntity,
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
          ...shippingEntity,
          createdAt: convertDateTimeFromServer(shippingEntity.createdAt),
          updatedAt: convertDateTimeFromServer(shippingEntity.updatedAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="webApp.shipping.home.createOrEditLabel" data-cy="ShippingCreateUpdateHeading">
            <Translate contentKey="webApp.shipping.home.createOrEditLabel">Create or edit a Shipping</Translate>
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
                  id="shipping-id"
                  label={translate('webApp.shipping.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField label={translate('webApp.shipping.userId')} id="shipping-userId" name="userId" data-cy="userId" type="text" />
              <ValidatedField
                label={translate('webApp.shipping.firstName')}
                id="shipping-firstName"
                name="firstName"
                data-cy="firstName"
                type="text"
              />
              <ValidatedField
                label={translate('webApp.shipping.lastName')}
                id="shipping-lastName"
                name="lastName"
                data-cy="lastName"
                type="text"
              />
              <ValidatedField label={translate('webApp.shipping.phone')} id="shipping-phone" name="phone" data-cy="phone" type="text" />
              <ValidatedField
                label={translate('webApp.shipping.address')}
                id="shipping-address"
                name="address"
                data-cy="address"
                type="text"
              />
              <ValidatedField label={translate('webApp.shipping.city')} id="shipping-city" name="city" data-cy="city" type="text" />
              <ValidatedField label={translate('webApp.shipping.state')} id="shipping-state" name="state" data-cy="state" type="text" />
              <ValidatedField
                label={translate('webApp.shipping.country')}
                id="shipping-country"
                name="country"
                data-cy="country"
                type="text"
              />
              <ValidatedField
                label={translate('webApp.shipping.postalCode')}
                id="shipping-postalCode"
                name="postalCode"
                data-cy="postalCode"
                type="text"
              />
              <ValidatedField
                label={translate('webApp.shipping.createdAt')}
                id="shipping-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('webApp.shipping.updatedAt')}
                id="shipping-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/shipping" replace color="info">
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

export default ShippingUpdate;
