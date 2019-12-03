var express = require('express');
require('../http-status');
var router = express.Router();
var db = require('../database');

/* GET movie listing. (GET All movies) */
router.get('/', function(request, response, next) {
  var page = request.query.page;
  var limit = request.query.limit;

  var sql = "SELECT * FROM movie ";

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

/* GET movie by id. (GET specific movie) */
router.get('/:id', function(request, response, next) {
  var id = request.params.id;
  var sql = "SELECT * FROM movie WHERE id = " + id;

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

/* GET movie by name. (GET specific movie) */
router.get('/:name', function(request, response, next) {
  var name = request.params.name;
  var sql = "SELECT * FROM movie WHERE name = " + name;

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
