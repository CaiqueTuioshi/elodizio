import React from 'react';
import { Col, Row } from 'reactstrap';
import { Dupla } from '../../type/Dupla';

type Props = {
  duplasOld?: Dupla[];
  duplasNew: Dupla[];
}

const DuplasList: React.FC<Props> = (props) => {
  const {duplasOld, duplasNew} = props;
  
  return (
    <Row>
      {duplasOld && duplasOld?.length > 0 && (
        <Col md={6}>
          <h2>Duplas Anteriores</h2>
          <ul>
            <Col md={6}>
            {duplasOld.map((duplaOld, index) => {
              return (
              <Row>
                <Col sm={2}>
                <li key={index}>{`${duplaOld.membro1} | ${duplaOld.membro2}`}</li>
                </Col>
              </Row>
              )
            })}
            </Col>
          </ul>
        </Col>
      )}

      {duplasNew?.length > 0 && (
        <Col md={6}>
          <h2>Duplas Atuais</h2>
          <ul>
            <Col md={6}>
            {duplasNew.map((duplaNew, index) => {
              return (
              <Row>
                <Col sm={2}>
                <li key={index}>{`${duplaNew.membro1} | ${duplaNew.membro2}`}</li>
                </Col>
              </Row>
              )
            })}
            </Col>
          </ul>
        </Col>
      )}
</Row>
  )
}

export default DuplasList;