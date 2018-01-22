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
    res.send([{
            "id": "ID",
            "name": "nombre",
            "description": "DESCRIPCION"
        },
        {
            "id": "ID1",
            "name": "nombre1",
            "description": "DESCRIPCION1"
        }
    ]);

    /*
     *Put Phase2 Code here.
     */

};

exports.findById = function (req, res) {

    console.log('ID: ' + req.params.id + ' Wine Request');
    //Modified the res.send line to send a JSON Object with the requested ID. 
    res.send({
        "id": req.params.id,
        "name": "nombre",
        "description": "DESCRIPCION"
    });

    /*
     * The next code is for Phase 2.
     * 
     * Modified this method to return one specific wine from collection.
     * You have to use the method findById which has the next syntaxis:
     *      findById(id, callback(err, result))
     *   
     */

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
