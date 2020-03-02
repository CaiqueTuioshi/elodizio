import axios, {AxiosPromise} from 'axios'
import { Membro } from "../type/Membro"
import { Dupla } from '../type/Dupla'

export const buscarMembrosDTO = (): AxiosPromise<Membro[]> => 
axios.get('http://localhost:8080/rodizio-api/buscar-membros')

export const buscarDuplasDTO = (): AxiosPromise<Dupla[]> => 
axios.get('http://localhost:8080/rodizio-api/buscar-duplas')

export const iniciarDuplas = (dupla: any): AxiosPromise<Dupla[]> => 
axios.post('http://localhost:8080/rodizio-api/iniciar-duplas', dupla)

export const save = (membro: any): AxiosPromise<Membro[]> => 
axios.post('http://localhost:8080/rodizio-api/save', membro)

export const lockarDeslockarMembro = (idMembro: string): AxiosPromise<Membro[]> => 
axios.post(`http://localhost:8080/rodizio-api/lockar-deslockar/${idMembro}`)

export const construirDuplas = (): AxiosPromise<Dupla[]> => 
axios.get('http://localhost:8080/rodizio-api/duplas')

export const removerMembro = (id: string): AxiosPromise<Dupla[]> => 
axios.post(`http://localhost:8080/rodizio-api/remover/${id}`)

export const zerarMembros = (): AxiosPromise<void> => 
axios.post('http://localhost:8080/rodizio-api/zerar-membros')

export const zerarDuplas = (): AxiosPromise<void> => 
axios.post('http://localhost:8080/rodizio-api/zerar-duplas')