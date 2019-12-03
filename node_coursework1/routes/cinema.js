var express = require('express');
require('../http-status');
var router = express.Router();
var db = require('../database');

/* GET cinema listing. (GET All cinemas) */
router.get('/', function(request, response, next) {
  var page = request.query.page;
  var limit = request.query.limit;

  var sql = "SELECT * FROM cinema ";

  if(limit !== undefined && page !== undefined ){
    sql += "ORDER BY id LIMIT " + limit + " OFFSET " + --page * limit;
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

/* GET cinema by id. (GET specific cinema) */
router.get('/:id', function(request, response, next) {
  var id = request.params.id;
  var sql = "SELECT * FROM cinema WHERE id = " + id;

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
