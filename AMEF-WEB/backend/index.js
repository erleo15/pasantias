import express from 'express';
import * as bodyParser from 'body-parser';
import cors from 'cors';

import rootRouter from './routes';

const app = express();

app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));


app.use(rootRouter);

const port = 3000;

app.listen(port, () => {
  console.log(`Server running at http://localhost:${port}/`);
});

//HTTPS
/*
const fs = require('fs');
const https = require('https');
const express = require('express');

const PORT = 3000;

const app = express();

app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

app.use(rootRouter);

https.createServer({
  //key: fs.readFileSync('server.key','utf8'),
  key: fs.readFileSync('fema.edutech-project.org/privkey1.pem','utf8'),
  //cert: fs.readFileSync('localhost.crt', 'utf8')
  //cert: fs.readFileSync('server.crt', 'utf8')
  cert: fs.readFileSync('fema.edutech-project.org/cert1.pem', 'utf8')
}, app).listen(PORT, function(){
  console.log("My HTTPS server listening on port " + PORT + "...");
});

app.get('/foo', function(req, res){
  console.log('Hello, I am foo.');
});
*/