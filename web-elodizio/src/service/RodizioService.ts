import axios, {AxiosPromise} from 'axios'
import { Membro } from "../type/Membro"
import { Dupla } from '../type/Dupla'

export const buscarMembrosDTO = (): AxiosPromise<Membro[]> => 
axios.get('http://localhost:8080/rodizio-api/buscar-membros')

export const buscarDuplasDTO = (): AxiosPromise<Dupla[]> => 
axios.get('http://localhost:8080/rodizio-api/buscar-duplas')

export const save = (membro: string): AxiosPromise<Membro[]> => 
axios.post('http://localhost:8080/rodizio-api/save', membro)