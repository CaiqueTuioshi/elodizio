import React from 'react';
import { Col, Row } from 'reactstrap';
import {Switch, Button} from '@material-ui/core'
import { Membro } from '../../type/Membro';

type Props = {
    membros: Membro[];
    onRemoveMembro: (id: string, index: number) => void;
    onLockarDeslockar: (id: string) => void;
}

const MembroList: React.FC<Props> = (props) => {
  const {membros, onRemoveMembro, onLockarDeslockar} = props;
  
  return (
    <Col md={6}>
      {membros && membros?.length > 0 && (
        <>
        <h2>Membros</h2>
        {/* <ul> */}
          <Col md={6}>
          {membros.map((membro, index) => {
            return (
            <Row>
              <text>{membro.nome}</text>
              <label className="switch"></label>
              <Switch size='small' onChange={() => onLockarDeslockar(membro.id)} checked={!membro.lockado} color="primary"/>
              <Button size='small' variant="contained" onClick={() => onRemoveMembro(membro.id, index)} color='secondary'>X</Button>
            </Row>
            )
          })}
          </Col>
        </>
      )}
    </Col>
  )
}

export default MembroList;