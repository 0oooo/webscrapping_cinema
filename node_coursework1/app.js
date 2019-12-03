var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');

var indexRouter = require('./routes/index');
var cinemaRouter = require('./routes/cinema');
var movieRouter = require('./routes/movie');
var screeningRouter = require('./routes/screening');
var ticketRouter = require('./routes/ticket');

var app = express();

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', indexRouter);
app.use('/cinemas', cinemaRouter);
app.use('/movies', movieRouter);
app.use('/screenings', screeningRouter);
app.use('/tickets', ticketRouter);

module.exports = app;
