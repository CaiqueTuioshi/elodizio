import React from 'react'
import { Formik, FormikProps, FormikValues } from 'formik'
import { Dupla } from '../../type/Dupla'
import { TextField, Button, Grid } from '@material-ui/core';

type Props = {
  onDuplaSubmit: (dupla: FormikValues) => void
}

const duplaInitialValues: Dupla = {
  membro1: '',
  membro2: ''
}

const DuplaForm: React.FC<Props> = ({ onDuplaSubmit }) => {

  return (
    <Formik
      initialValues={duplaInitialValues}
      onSubmit={onDuplaSubmit}
      render={(formProps: FormikProps<Dupla>) => (
        <Grid container spacing={3}>
          <Grid item>
            <TextField
              type="text"
              name='membro1'
              placeholder='Membro 1'
              onBlur={value => {
                formProps.setFieldValue('membro1', value.target.value)
                formProps.setFieldTouched('membro1', true)
              }
              }
            />
          </Grid>

          <Grid item>
            <TextField
              type="text"
              name='membro2'
              placeholder='Membro 2'
              onBlur={value => {
                formProps.setFieldValue('membro2', value.target.value)
                formProps.setFieldTouched('membro2', true)
              }
              }
            />
          </Grid>


          <Grid item>
            <Button variant="contained" onClick={formProps.submitForm}>Adicionar</Button>
          </Grid>

        </Grid>
      )}
    />
  )
}

export default DuplaForm;