import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IOrderItemProduct } from 'app/shared/model/order-item-product.model';
import { getEntity, updateEntity, createEntity, reset } from './order-item-product.reducer';

export const OrderItemProductUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const orderItemProductEntity = useAppSelector(state => state.orderItemProduct.entity);
  const loading = useAppSelector(state => state.orderItemProduct.loading);
  const updating = useAppSelector(state => state.orderItemProduct.updating);
  const updateSuccess = useAppSelector(state => state.orderItemProduct.updateSuccess);

  const handleClose = () => {
    navigate('/order-item-product');
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
    const entity = {
      ...orderItemProductEntity,
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
      ? {}
      : {
          ...orderItemProductEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="webApp.orderItemProduct.home.createOrEditLabel" data-cy="OrderItemProductCreateUpdateHeading">
            <Translate contentKey="webApp.orderItemProduct.home.createOrEditLabel">Create or edit a OrderItemProduct</Translate>
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
                  id="order-item-product-id"
                  label={translate('webApp.orderItemProduct.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('webApp.orderItemProduct.orderItemId')}
                id="order-item-product-orderItemId"
                name="orderItemId"
                data-cy="orderItemId"
                type="text"
              />
              <ValidatedField
                label={translate('webApp.orderItemProduct.productId')}
                id="order-item-product-productId"
                name="productId"
                data-cy="productId"
                type="text"
              />
              <ValidatedField
                label={translate('webApp.orderItemProduct.quantity')}
                id="order-item-product-quantity"
                name="quantity"
                data-cy="quantity"
                type="text"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/order-item-product" replace color="info">
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

export default OrderItemProductUpdate;
