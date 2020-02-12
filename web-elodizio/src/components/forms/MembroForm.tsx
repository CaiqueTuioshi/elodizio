import React from 'react'
import { Formik, FormikProps, FormikValues } from 'formik'
type Props = {
  onMembroSubmit:(dupla: FormikValues) => void
}

type MembroFormValue = {
  membro: string;
}

const initialValues = {
  membro: ''
}

const MembroForm: React.FC<Props> = (props) => {
  const {onMembroSubmit} = props

  return (
    <Formik
        initialValues={initialValues} 
        onSubmit={onMembroSubmit}
        render={(formProps: FormikProps<MembroFormValue>) => (
          <>
          <input 
            type="text" 
            name='membro' 
            placeholder='Membro' 
            onBlur={value => {              
              formProps.setFieldValue('membro', value.target.value)
              formProps.setFieldTouched('membro', true)
              }
            }
          />
          <button onClick={formProps.submitForm}>Adicionar</button>
          </>
        )}
      />
  )
}

export default MembroForm;