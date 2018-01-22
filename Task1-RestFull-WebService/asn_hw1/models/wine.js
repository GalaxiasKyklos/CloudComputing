/* 
 ***************************************************
 *     APLICACIONES Y SERVICIOS EN LA NUBE         *
 *                   ITESO                         *
 *                                                 * 
 *    Actividad 1: Diseño de un WebService         *
 *    Codigo Base: Alvaro Parres (parres@iteso.mx) * 
 *                                                 * 
 *    Alumno: <COMPLETAR SU NOMBRE>                *
 *    Exp: <Numero_de_Expediente>                  *
 *                                                 *
 ***************************************************
 */

var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var wineSchema = new Schema({
    name: { type: String },
    year: { type: Number },
    grapes: { type: String },
    country: { type: String },
    description: { type: String }
});

module.exports = mongoose.model('Wine', wineSchema);


