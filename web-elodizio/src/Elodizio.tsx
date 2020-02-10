import React, { Component, useEffect, useState } from 'react';
// import elotech from './img/elotech';
import './App.css';
import { buscarMembrosDTO, buscarDuplasDTO, save } from './service/RodizioService';
import { Membro } from './type/Membro';
import {Row, Col, Container, Form} from 'reactstrap'
import Switch from 'react-switch'
import { Dupla } from './type/Dupla';
import {Formik, Field, FormikValues} from 'formik'

type Props = {}

const Elodizio: React.FC<Props> = (props) => {
  const [membros, setMembros] = useState<Membro[]>();
  const [duplas, setDuplas] = useState<Dupla[]>();

useEffect(() => {
  buscarMembrosDTO()
  .then(response => setMembros(response.data))
  .catch(error => console.log('error', error))

  buscarDuplasDTO()
  .then(response => setDuplas(response.data))
  .catch(error => console.log('error', error))
}, [])

const onSubmit = (membro: FormikValues) => {
  console.log(membro);
  
  // save(membro)
  // .then(response => setMembros(response.data))
  // .catch(error => console.log('error', error))
}

  return (
    <Container>
      <Formik 
        initialValues={{ membro: ''}} 
        onSubmit={onSubmit}
        render={formProps => (
          <Form>
            <Field
              name="firstName"
              render={({ field, form, meta }) => (
                <div>
                  <input type="text" {...field} placeholder="First Name" />
                  {meta.touched && meta.error && meta.error}
                  <button onClick={formProps.submitForm}>Adicionar</button>
                </div>
              )}
            />
          </Form>
        )}
      >

      </Formik>
    <Row>
      <Col md={6}>
      {membros && membros?.length > 0 && (
        <>
        <h2>Membros</h2>
        <ul>
          <Col md={6}>
          {membros.map((membro, index) => {
            return (
            <Row>
              <li key={membro.id}>{membro.nome}</li>
                <span></span>
                <Switch onChange={() => {}} checked={!membro.lockado}/>
              <button onClick={() => {}}>X</button>
            </Row>
            )
          })}
          </Col>
        </ul>
        </>
      )}
      </Col>

      <Col md={6}>
      {duplas && duplas?.length > 0 && (
        <>
        <h2>Duplas</h2>
        <ul>
          <Col md={6}>
          {duplas.map((dupla, index) => {
            return (
            <Row>
              <Col sm={2}>
              <li key={index}>{`${dupla.membro1} | ${dupla.membro2}`}</li>
              </Col>
            </Row>
            )
          })}
          </Col>
        </ul>
        </>
      )}
      </Col>
      </Row>
      </Container>
  );
}

export default Elodizio;
