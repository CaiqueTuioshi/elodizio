import React from 'react'
import { Formik, FormikProps, FormikValues } from 'formik'
import { Dupla } from '../../type/Dupla'

type Props = {
  onDuplaSubmit:(dupla: FormikValues) => void
}

const duplaInitialValues: Dupla = {
  membro1: '',
  membro2: ''
}

const DuplaForm: React.FC<Props> = (props) => {
  const {onDuplaSubmit} = props

  return (
    <Formik
        initialValues={duplaInitialValues} 
        onSubmit={onDuplaSubmit}
        render={(formProps: FormikProps<Dupla>) => (
          <>
          <input 
            type="text" 
            name='membro1' 
            placeholder='Membro 1' 
            onBlur={value => {              
              formProps.setFieldValue('membro1', value.target.value)
              formProps.setFieldTouched('membro1', true)
              }
            }
          />
          <input 
            type="text" 
            name='membro2' 
            placeholder='Membro 2' 
            onBlur={value => {
              formProps.setFieldValue('membro2', value.target.value)
              formProps.setFieldTouched('membro2', true)
              }
            }
          />
          <button onClick={formProps.submitForm}>Adicionar</button>
          </>
        )}
      />
  )
}

export default DuplaForm;