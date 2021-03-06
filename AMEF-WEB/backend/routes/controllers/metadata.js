
import { environment } from '../../environment.ts';

var MongoClient = require('mongodb').MongoClient;
var url = "mongodb://localhost:27017/";
var numero= []

export const evolution = (req, res) => {
    MongoClient.connect(url,{useNewUrlParser: true, useUnifiedTopology: true}, function(err, db) {
        if (err) throw err;
        var dbo = db.db(environment.database);
        console.log(`probando evolution`);
        dbo.collection("status").aggregate( [ //cambio en todas las instancias stats --> status
                {$group: { _id: "$total"/*$date a $total*/ , searched : {$sum: '$ready'/*$total a $ready*/}, found : {$sum: '$found'}}},
                {$project: { date : '$_id', _id : 0, searched :1, found:1}},
                {$sort : {date : 1}}
            ])
        .toArray(
            function(err, result) {
                if (err) {
                    res.status(500).send();
                    throw err;
                }
                res.status(200).send(result);
                db.close();
            }
        );
    });
};

export const tool = (req, res) => { 
    MongoClient.connect(url,{useNewUrlParser: true, useUnifiedTopology: true}, function(err, db) {
        if (err) throw err;
        var dbo = db.db(environment.database); 
        dbo.collection("status").aggregate( [ //cambio en todas las instancias stats --> status
                {$group: { _id: "$total"/*$date a $total*/ , searched : {$sum: '$ready'/*$total a $ready*/}, found : {$sum: '$found'}}},
                {$project: { date : '$_id', _id : 0, searched :1, found:1}},
                {$sort : {date : 1}}
            ])
        .toArray(
            function(err, result) {
                if (err) {
                    res.status(500).send();
                    throw err;
                }
                res.status(200).send(result);
                db.close();
            }
        );
    });
};


export const months = (req, res) => {
    MongoClient.connect(url,{useNewUrlParser: true, useUnifiedTopology: true}, function(err, db) {
        if (err) throw err;
        var dbo = db.db(environment.database);
        dbo.collection("status").distinct("total"/*date a total */, function(err, result) {
            if (err) {
                res.status(500).send();
                throw err;
            }
            res.status(200).send(result);
            db.close();
        });
    });
};

export const monthProps = (req, res) => {
    MongoClient.connect(url,{useNewUrlParser: true, useUnifiedTopology: true}, function(err, db) { // se implemento la propiedad {useNewUrlParser: true, useUnifiedTopology: true}
        if (err) throw err;                                                                         //debido a la antiguedad del cliente de conexion a la base de datos
        var dbo = db.db(environment.database);
        console.log(req.params.month);
        dbo.collection("pages").aggregate( [
            {$match: {date: req.params.month}},
            {$unwind: '$items'},
            {$unwind: '$items.properties'},
            {$group: {_id: '$items.properties.name', total: {$sum: 1}}},
            {$project: {key : '$_id' , total : 1, _id : 0}},
            {$sort: {property: -1}}])
        .toArray(
            function(err, result) {
                if (err) {
                    res.status(500).send();
                    throw err;
                }
                res.status(200).send(result);
                db.close();
            }
        );
    });
};

export const monthByProp = (req, res) => {
    MongoClient.connect(url,{useNewUrlParser: true, useUnifiedTopology: true}, function(err, db) {
        if (err) throw err;
        var dbo = db.db(environment.database);
        dbo.collection("pages").aggregate( [
            {$match: {date: req.params.month}},
            {$unwind: '$items'},
            {$unwind: '$items.properties'},
            {$group: {_id: '$items.properties.name', value: {$push:"$items.properties.value"}}},
            {$unwind: '$value'},
            {$group: { _id: {name:"$_id",value:{$substr: ['$value', 0, {$indexOfBytes: ["$value", "/"]}]}}, total: {$sum:1}}},
            {$sort: {total: -1}},
            {$group: { _id: '$_id.name', values : {$push: {key: '$_id.value', total: '$total'}}}},
            {$project: {property: '$_id', values: 1, _id:0}},
            {$sort: {property: -1}}
        ])
        .toArray(
            function(err, result) {
                if (err) {
                    res.status(500).send();
                    throw err;
                }
                res.status(200).send(result);
                db.close();
            }
        );
    });
};

export const monthDomains = (req, res) => {
   
    MongoClient.connect(url,{useNewUrlParser: true, useUnifiedTopology: true}, function(err, db) {
        if (err) throw err;
        var dbo = db.db(environment.database);
        dbo.collection("pages").aggregate( [
            {$match: {date: req.params.month}},
            {$group: {_id: "$domain", total : {$sum :1}}}, 
            {$project: {_id: 0,key:"$_id", total:1}},
            {$sort: {"total" : -1}}
        ])
        .toArray(
            function(err, result) {
                if (err) {
                    res.status(500).send();
                    throw err;
                }
                res.status(200).send(result);
                db.close();
            }
        );
    });
};


 

export const configurarLink = (req, res) => {
    var aux = "https://commoncrawl.s3.amazonaws.com/";
    console.log(req.body); 
    var linkFile = req.body.linkFile
    
    var terminal = require('child_process').spawn('bash');  
    terminal.stdout.on('data', function (data) { 
        console.log('stdout: ' + data); 
    });

    terminal.on('exit', function (code) { 
        console.log('child process exited with code ' + code); 
        if(code==0){
            res.status(200).send({mensaje : "OK"});
        }else{
            res.status(200).send({mensaje : "Fallo interno del servidor"});

        }
    }); 
    
    setTimeout( 
        function() { 
            console.log('Sending stdin to terminal'); 
            var comando = "rm -rf warc.paths.* && wget "+linkFile+" && gzip -d -f warc.paths.gz";
            console.log(comando)
            terminal.stdin.write(comando);
            console.log('Ending terminal session'); 
            terminal.stdin.end(); 
            
        },1000
    );
    
 
}

export const getNumeroLineasFile = (req, res) => {
   var fs = require('fs')
    var contents = fs.readFileSync("warc.paths");
    var lines = contents.toString().split('\n').length-1;
    console.log(lines);
    
        res.status(200).send({lineas : lines});
    
}

export const agregarCola = (req,res)=>{
    console.log(req.body);
    var linkFile = req.body.linkFile
    var numero1 = req.body.numero.toString().split('-')[0]
    var numero2 = req.body.numero.toString().split('-')[1] 
    
    var terminal = require('child_process').spawn('bash');  
    terminal.stdout.on('data', function (data) { 
        console.log('stdout: ' + data); 
    });

    terminal.on('exit', function (code) { 
        console.log('child process exited with code ' + code); 
        if(code==0){
            res.status(200).send({mensaje : "OK"});
        }else{
            res.status(200).send({mensaje : "Fallo interno del servidor"});

        }
    }); 
    
    setTimeout( 
        function() { 
            console.log('Sending stdin to terminal'); 
            var comando = `rm -rf warc.paths.* && wget ${linkFile} && gzip -d -f warc.paths.gz && sed -n ${numero1},${numero2}p warc.paths > warc.paths.bak && rm -rf warc.paths && mv warc.paths.bak warc.paths && cd ../../AMEF && ./bin/master clearqueue && ./bin/master queue -f ../AMEF-WEB/backend/warc.paths  && cd ../AMEF-WEB/backend/`;
            console.log(comando)
            terminal.stdin.write(comando);
            console.log('Ending terminal session'); 
            terminal.stdin.end();  
        }, 1000
    );
}


export const iniciar = (req,res)=>{
    console.log(req.body);
    
    var terminal = require('child_process').spawn('bash');  
    terminal.stdout.on('data', function (data) { 
        console.log('stdout: ' + data); 
    });

    terminal.on('exit', function (code) { 
        console.log('child process exited with code ' + code); 
        
    }); 
    
    setTimeout( 
        function() { 
            console.log('Sending stdin to terminal'); 
            var comando = "cd ../../AMEF && ./bin/master run"
            console.log(comando)
            terminal.stdin.write(comando);
            console.log('Ending terminal session'); 
            terminal.stdin.end();  
            res.status(200).send({mensaje: "inicio correctamente"})
        }, 1000
    );
}
 


export const parar = (req,res)=>{
    console.log(req.body); 
    var terminal = require('child_process').spawn('bash');  
    terminal.stdout.on('data', function (data) { 
        console.log('stdout: ' + data); 
    });

    terminal.on('exit', function (code) { 
        console.log('child process exited with code ' + code); 
        if(code==0){
            res.status(200).send({mensaje : "OK"});
        }else{
            res.status(200).send({mensaje : "Fallo interno del servidor"});

        }
    }); 
    
    setTimeout( 
        function() { 
            console.log('Sending stdin to terminal'); 
            var comando = `killall java`;
            console.log(comando)
            terminal.stdin.write(comando);
            console.log('Ending terminal session'); 
            //res.status(200).send({mensaje : "OK"});
            terminal.stdin.end();  
        }, 1000
    );
}
 


export const cargar = (req,res)=>{
    console.log(req.body); 
    var fs = require('fs')
    var contents = fs.readFileSync("../../AMEF/src/main/resources/search.txt");
    var lines = contents.toString();
    console.log(lines);

    res.status(200).send({lineas : lines});
     
}

export const cargarRespaldo = (req,res)=>{
    console.log(req.body); 
    var fs = require('fs')
    var contents = fs.readFileSync("../../AMEF/src/main/resources/search.txt.bak");
    var lines = contents.toString();
    console.log(lines);

    res.status(200).send({lineas : lines});
     
}
 

export const guardar = (req,res)=>{
    console.log(req.body); 
    var lineas = req.body.lineas
    var fs = require('fs')
    //var contents = fs.readFileSync("../../AMEF/src/main/resources/search.txt.bak");
    fs.writeFile("../../AMEF/src/main/resources/search.txt", lineas, function (err) {
        // la funcion es la que maneja lo que sucede despues de termine el evento
        if (err) {
           // res.status(200).send({mensaje: "No se guardo el contenido"});
            return console.log(err);
        }
        // las funciones de javascript en nodejs son asincronicas
        // por lo tanto lo que se quiera hacer debe hacerse dentro de la funcion que maneja el evento
        // si uno declara una variable arriba de la funcion, la manipula dentro y la quiere usar
        // despues afuera, se corre el riezgo de que nunca se realice la manipulacion.
        console.log("The file was saved!");
       // res.status(200).send({mensaje: "OK"});
    });

    

    var terminal = require('child_process').spawn('bash');  
    terminal.stdout.on('data', function (data) { 
        console.log('stdout: ' + data); 
    });

    terminal.on('exit', function (code) { 
        console.log('child process exited with code ' + code); 
        if(code==0){
            res.status(200).send({mensaje : "Se guardo correctamente"});
        }else{
            res.status(200).send({mensaje : "Fallo interno del servidor"});

        }
    }); 
    
    setTimeout( 
        function() { 
            console.log('Sending stdin to terminal'); 
            var comando = `cd ../../AMEF && rm -rf target && mvn package && cd ../AMEF-WEB/backend`;
            console.log(comando)
            terminal.stdin.write(comando);
            console.log('Ending terminal session'); 
            //res.status(200).send({mensaje : "OK"});
            terminal.stdin.end();  
        }, 1000
    );
}
 