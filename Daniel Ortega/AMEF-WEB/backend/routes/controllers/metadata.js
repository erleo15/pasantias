import { pipeline } from 'stream';
import { environment } from '../../environment.ts';

var MongoClient = require('mongodb').MongoClient;
var url = "mongodb://localhost:27017/";

export const evolution = (req, res) => {
    MongoClient.connect(url, function(err, db) {
        if (err) throw err;
        var dbo = db.db(environment.database);
        dbo.collection("stats").aggregate( [
                {$group: { _id: "$date", searched : {$sum: '$total'}, found : {$sum: '$found'}}},
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
    MongoClient.connect(url, function(err, db) {
        if (err) throw err;
        var dbo = db.db(environment.database);
        dbo.collection("stats").distinct("date", function(err, result) {
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
    MongoClient.connect(url, function(err, db) {
        if (err) throw err;
        var dbo = db.db(environment.database);
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
    MongoClient.connect(url, function(err, db) {
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
    MongoClient.connect(url, function(err, db) {
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