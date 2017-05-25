import React, {PropTypes, Component} from 'react';
import {Col, Row} from 'react-bootstrap';
import './ListaEmpresas.scss';

export default class ListaEmpresas extends Component  {

  render() {
    return (
      <div className="ListaEmpresas">
        {
          this.props.empresas.map(empresa =>
            <div className="single-empresa"
                  onClick={e => this.props.selectEmpresa(empresa)}>
              <Row>
                <Col lg={12} md={12} sm={12} xs={12}>
                  <h1 className="nombre-empresa">
                    {empresa.nombre}
                  </h1>
                </Col>
              </Row>
              <Row>
                <Col lg={6} md={6} sm={6} xs={6} className="show">
                  <h3 className="sub-info center">
                    {empresa.totalPeriodos}
                    <br />
                    períodos
                  </h3>
                </Col>
                <Col lg={6} md={6} sm={6} xs={6} className="show">
                  <h3 className="sub-info center">
                      {empresa.totalCuentas}
                      <br />
                     cuentas
                  </h3>
                </Col>
              </Row>
            </div>
          )
        }
      </div>
    )
  }
}
