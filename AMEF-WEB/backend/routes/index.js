import { Router } from 'express';

import * as metadata from './controllers/metadata'

var contexto = "/servicios"
const metadataRouter = new Router();
metadataRouter.get(contexto+'/evolution', metadata.evolution);
metadataRouter.get(contexto+'/tool', metadata.tool);
metadataRouter.get(contexto+'/months', metadata.months);
metadataRouter.get(contexto+'/month/:month/byprop', metadata.monthByProp);
metadataRouter.get(contexto+'/month/:month/props', metadata.monthProps);
metadataRouter.get(contexto+'/month/:month/domains', metadata.monthDomains); 
metadataRouter.post(contexto+'/configurarLink', metadata.configurarLink);
metadataRouter.post(contexto+'/numeroLineasFile', metadata.getNumeroLineasFile); 
metadataRouter.post(contexto+'/agregarCola', metadata.agregarCola);
metadataRouter.post(contexto+'/guardar', metadata.guardar);
metadataRouter.get(contexto+'/iniciar',metadata.iniciar); 
metadataRouter.get(contexto+'/parar',metadata.parar); 
metadataRouter.get(contexto+'/cargar',metadata.cargar);
metadataRouter.get(contexto+'/cargarRespaldo',metadata.cargarRespaldo);


export default metadataRouter;
