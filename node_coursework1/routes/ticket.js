let express = require('express');
require('../http-status');
let router = express.Router();
let db = require('../database');

const CHANGING_TIME = 17;

/* GET screening by movie id. (GET specific screening) */
router.get('/', function(request, response, next) {
    let page = request.query.page;
    let limit = request.query.limit;
    let cinema = request.query.cinema;
    let day_type = request.query.daytype;
    let time = request.query.screeningtime;
    let sql = "SELECT description, price FROM ticket_type WHERE cinema_id = " + cinema;

    if(day_type === "weekdays" && time > CHANGING_TIME){
        sql += " AND day_type = 'weekends' " ;
    } else {
        sql += " AND day_type = '" + day_type + "' ";
    }

    if(limit !== undefined && page !== undefined ){
        sql += " ORDER BY id LIMIT " + limit + " OFFSET " + --page * limit;
    }

    db.query(sql, function (err, result) {
        if (err){
            response.status(HTTP_STATUS.INTERNAL_SERVER_ERROR);
            response.json({'error': true, 'message': + err});
            // return;
            next();
        }

        response.json({
            count: result.length,
            data: result
        });
    });

});


module.exports = router;