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
