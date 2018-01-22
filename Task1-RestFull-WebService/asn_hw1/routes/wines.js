/* 
 ***************************************************
 *     APLICACIONES Y SERVICIOS EN LA NUBE         *
 *                   ITESO                         *
 *                                                 * 
 *    Actividad 1: Diseño de un WebService         *
 *    Codigo Base: Alvaro Parres (parres@iteso.mx) * 
 *                                                 * 
 *    Alumno: Saúl Enrique Ponce Razo              *
 *    Exp: is699399                                *
 *                                                 *
 ***************************************************
 *                                                 *
 * Instrucciones: Complete el codigo basado en     * 
 * las indicaciones descritas en el documento      *
 *                                                 *
 ***************************************************
 */

var Wine = require('../models/wine');

//Phase 1
exports.findAll = function (req, res) {

    console.log('All Wines Request');
    //Modified the res.send code to return two JSON Objects 
    // res.send([{
    //         "id": "ID",
    //         "name": "nombre",
    //         "description": "DESCRIPCION"
    //     },
    //     {
    //         "id": "ID1",
    //         "name": "nombre1",
    //         "description": "DESCRIPCION1"
    //     }
    // ]);

    /*
     *Put Phase2 Code here.
     */
    Wine.find((err, wines) => {
        if (err) {
            res.send(500, err.message)
        }
        res.status(200).jsonp(wines)
    })

};

exports.findById = function (req, res) {

    console.log('ID: ' + req.params.id + ' Wine Request');
    //Modified the res.send line to send a JSON Object with the requested ID. 
    // res.send({
    //     "id": req.params.id,
    //     "name": "nombre",
    //     "description": "DESCRIPCION"
    // });

    /*
     * The next code is for Phase 2.
     * 
     * Modified this method to return one specific wine from collection.
     * You have to use the method findById which has the next syntaxis:
     *      findById(id, callback(err, result))
     *   
     */
    Wine.findById(req.params.id, (err, wine) => {
        if (err) {
            res.send(500, err.message)
        }
        res.status(200).jsonp(wine)
    })
};

/*
 * The next code is for Phase 2.
 *
 *  Create the methods:
 *    addWine
 *    deleteWine
 *    updateWine
 *
 *  Some hints about this tree method are in HomeWork document.
 */

exports.addWine = (req, res) => {
    let newWine = new Wine({
        name: req.body.name,
        year: req.body.year,
        grapes: req.body.grapes,
        country: req.body.country,
        description: req.body.description
    })

    newWine.save((err, newElement) => {
        if (err) {
            res.status(500).send(err.message)
        }
        res.status(201).jsonp(newElement)
    })
}

exports.deleteWine = (req, res) => {
    Wine.findById(req.params.id, (err, wine) => {
        if (err) {
            res.send(500, err.message)
        }
        wine.remove((err) => {
            if (err) {
                res.status(500).send(err.message)
            }
            res.status(200).jsonp(wine)
        })
    })
}

exports.updateWine = (req, res) => {
    Wine.findById(req.params.id, (err, wine) => {
        if (err) {
            res.send(500, err.message)
        }

        wine.name = req.body.name,
        wine.year = req.body.year,
        wine.grapes = req.body.grapes,
        wine.country = req.body.country,
        wine.description = req.body.description

        wine.save((err) => {
            if (err) {
                res.status(500).send(err.message)
            }
            res.status(200).jsonp(wine)
        })
    })
}
