import React, { useEffect, useState } from 'react';
// import elotech from './img/elotech';
import './App.css';
import { buscarMembrosDTO, buscarDuplasDTO, save, iniciarDuplas, removerMembro, lockarDeslockarMembro, construirDuplas, zerarMembros, zerarDuplas } from './service/RodizioService';
import { Membro } from './type/Membro';
import { FormGroup } from 'reactstrap'

import { Dupla } from './type/Dupla';
import { FormikValues } from 'formik'
import MembroForm from './components/forms/MembroForm';
import DuplaForm from './components/forms/DuplaForm';
import MembrosList from './components/lists/MembrosList';
import DuplasList from './components/lists/DuplasList';
import { Button, Container, Grid } from '@material-ui/core';

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
      .then(response => { setDuplasOld(duplasNew); setDuplasNew(response.data) })
      .catch(error => console.log('error', error))
  }

  const onZerarMembros = () => {
    zerarMembros()
      .then(() => setMembros([]))
      .catch(error => console.log('error', error))
  }

  const onZerarDuplas = () => {
    zerarDuplas()
      .then(() => { setDuplasOld([]); setDuplasNew([]) })
      .catch(error => console.log('error', error))
  }

  const onRemoveMembro = (id: string, index: number) => {
    removerMembro(id)
      .then(response => {
        const updateMembros = [
          ...membros ? membros.slice(0, index) : [],
          ...membros ? membros.slice(index + 1) : []
        ]

        setMembros(updateMembros);
        setDuplasNew(response.data);
      })
      .catch(error => console.log('error', error))
  }

  return (
    <Container maxWidth="lg">
      <Grid container spacing={3}>
        <Grid item xs={12}>
          <FormGroup>
            <h3>Inserir Membro</h3>
            <MembroForm onMembroSubmit={onMembroSubmit} />
          </FormGroup>
        </Grid>

        <Grid id="inserir-duplas" item xs={12}>
          <FormGroup>
            <h3>Inserir Duplas</h3>
            <DuplaForm onDuplaSubmit={onDuplaSubmit} />
          </FormGroup>
        </Grid>

        <Grid container spacing={3}>
          <Grid item >
            <Button size='large' variant="contained" color='primary' onClick={() => onZerarMembros()}>ZERAR MEMBROS</Button>
          </Grid>

          <Grid item>
            <Button size='large' variant="contained" color='primary' onClick={() => onZerarDuplas()}>ZERAR DUPLAS</Button>
          </Grid>

          <Grid item >
            <Button size='large' variant="contained" color='primary' onClick={() => onConstruirDuplas()}>SORTEAR DUPLAS</Button>
          </Grid>
        </Grid>

        <Grid item xs={6}>
          {membros && (
            <MembrosList membros={membros} onRemoveMembro={onRemoveMembro} onLockarDeslockar={onLockarDeslockar} />
          )}

          {duplasNew && (
            <DuplasList duplasOld={duplasOld} duplasNew={duplasNew} />
          )}

        </Grid>
      </Grid>
    </Container>
  );
}

export default Elodizio;
