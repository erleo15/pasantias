import { Router } from 'express';

import * as metadata from './controllers/metadata'


const metadataRouter = new Router();
metadataRouter.get('/evolution', metadata.evolution);
metadataRouter.get('/tool', metadata.tool);
metadataRouter.get('/months', metadata.months);
metadataRouter.get('/month/:month/byprop', metadata.monthByProp);
metadataRouter.get('/month/:month/props', metadata.monthProps);
metadataRouter.get('/month/:month/domains', metadata.monthDomains); 
metadataRouter.post('/configurarLink', metadata.configurarLink);
metadataRouter.post('/numeroLineasFile', metadata.getNumeroLineasFile); 
metadataRouter.post('/agregarCola', metadata.agregarCola); 
export default metadataRouter;
