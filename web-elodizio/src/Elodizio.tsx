import React, { Component, useEffect, useState } from 'react';
// import elotech from './img/elotech';
import './App.css';
import { buscarMembrosDTO, buscarDuplasDTO, save, iniciarDuplas, removerMembro, lockarDeslockarMembro, construirDuplas, zerarMembros, zerarDuplas } from './service/RodizioService';
import { Membro } from './type/Membro';
import {Row, Col, Container, FormGroup} from 'reactstrap'

import { Dupla } from './type/Dupla';
import {Formik, Form, Field, FormikValues, FormikProps} from 'formik'
import MembroForm from './components/forms/MembroForm';
import DuplaForm from './components/forms/DuplaForm';
import MembrosList from './components/lists/MembrosList';
import DuplasList from './components/lists/DuplasList';
import { Button } from '@material-ui/core';

type Props = {}

const Elodizio: React.FC<Props> = (props) => {
  const [membros, setMembros] = useState<Membro[]>();
  const [duplasOld, setDuplasOld] = useState<Dupla[]>();
  const [duplasNew, setDuplasNew] = useState<Dupla[]>();

useEffect(() => {
  buscarMembrosDTO()
  .then(response => setMembros(response.data))
  .catch(error => console.log('error', error))
}, [duplasNew])

useEffect(() => {
  buscarDuplasDTO()
  .then(response => setDuplasNew(response.data))
  .catch(error => console.log('error', error))
}, [])

const onMembroSubmit = (value: FormikValues) => {
  save(value)
  .then(response => setMembros(response.data))
  .catch(error => console.log('error', error))
}

const onDuplaSubmit = (value: FormikValues) => {
  iniciarDuplas(value)
  .then(response => setDuplasOld(response.data))
  .catch(error => console.log('error', error))
}

const onLockarDeslockar = (idMembro: string) => {
  lockarDeslockarMembro(idMembro)
  .then(response => setMembros(response.data))
  .catch(error => console.log('error', error))
}

const onConstruirDuplas = () => {
  construirDuplas()
  .then(response => {setDuplasOld(duplasNew); setDuplasNew(response.data)})
  .catch(error => console.log('error', error))
}

const onZerarMembros = () => {
  zerarMembros()
  .then(() => setMembros([]))
  .catch(error => console.log('error', error))
}

const onZerarDuplas = () => {
  zerarDuplas()
  .then(() => {setDuplasOld([]); setDuplasNew([])})
  .catch(error => console.log('error', error))
}

const onRemoveMembro = (id: string, index: number) => {
  removerMembro(id)
  .then(() => {
    const updateMembros = [
      ...membros ? membros.slice(0, index) : [],
      ...membros ? membros.slice(index + 1) : []
    ]

    setMembros(updateMembros);
  })
  .catch(error => console.log('error', error))
}

  return (
    <Container>
      <Row>
        <FormGroup>
          <h3>Inserir Membro</h3>
          <MembroForm onMembroSubmit={onMembroSubmit}/>
        </FormGroup>
        
      </Row>
      <Row>
      <FormGroup>
        <h3>Inserir Duplas</h3>
        <DuplaForm onDuplaSubmit={onDuplaSubmit}/>
      </FormGroup>
      </Row>

      <br></br>

      <Row>
        <Button size='large' variant="contained" color='primary' onClick={() => onZerarMembros()}>ZERAR MEMBROS</Button>
        <Button size='large' variant="contained" color='primary' onClick={() => onZerarDuplas()}>ZERAR DUPLAS</Button>
        <Button size='large' variant="contained" color='primary' onClick={() => onConstruirDuplas()}>SORTEAR DUPLAS</Button>
      </Row>
        
      <Row>
        {membros && (
          <MembrosList membros={membros} onRemoveMembro={onRemoveMembro} onLockarDeslockar={onLockarDeslockar}/>
        )}

        {duplasNew && (
          <DuplasList duplasOld={duplasOld} duplasNew={duplasNew}/>
        )}

      </Row>
    </Container>
  );
}

export default Elodizio;
