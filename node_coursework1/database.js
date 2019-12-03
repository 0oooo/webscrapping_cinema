//Import the mysql module
var mysql = require('mysql');

//Create a connection object with the user details
var connection = mysql.createPool({
    connectionLimit: 5, //todo double check it is correct
    host: "localhost",
    user: "root",
    password: "branches1?",
    database: "cinema_comparison",
    debug: true
});
module.exports = connection;