import React from 'react'
import { Formik, FormikProps, FormikValues } from 'formik'
import { TextField, Button, Grid } from '@material-ui/core';

// import './styles/MembroForm.css'

type Props = {
  onMembroSubmit: (dupla: FormikValues) => void
}

type MembroFormValue = {
  membro: string;
}

const initialValues = {
  membro: ''
}

const MembroForm: React.FC<Props> = (props) => {
  const { onMembroSubmit } = props

  return (
    <Formik
      initialValues={initialValues}
      onSubmit={onMembroSubmit}
      render={(formProps: FormikProps<MembroFormValue>) => (
        <Grid container spacing={3}>
          <Grid item sm={2} xs={2}>
            <TextField
              type="text"
              name='membro'
              placeholder='Membro'
              onBlur={value => {
                formProps.setFieldValue('membro', value.target.value)
                formProps.setFieldTouched('membro', true)
              }
              }
            />
          </Grid>

          <Grid item sm={2} xs={2}>
            <Button id="btn-adicionar-membro" variant="contained" onClick={formProps.submitForm}>Adicionar</Button>
          </Grid>

        </Grid>
      )}
    />
  )
}

export default MembroForm;