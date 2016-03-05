// Setup basic express server
var express = require('express');
var app = express();
var http = require('http');
http.globalAgent.maxSockets = 1000;
var server = http.createServer(app);
var bodyParser = require('body-parser');
var io = require('socket.io')(server);
var port = process.env.PORT || 3000;

server.listen(port, function () {
  console.log('Server listening at port %d', port);
});

// Routing
app.use(express.static(__dirname + '/public'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true, limit: "10000000"}));

// Game

var gameController = require("./data/game.js");
var getGameTime = function(){
  return 1 + Math.random()*10000;
};
var newGame = function () {
  var newGameTime = getGameTime();
  gameController.endGame(newGameTime);
  io.emit('new game', newGameTime);
  setTimeout(newGame, newGameTime);
};
newGame();

var beginRoutes = require("./routes/begin.js");
app.use("/begin", beginRoutes);

var numUsers = 0;

io.on('connection', function (socket) {
  console.log("connection");
  var addedUser = false;

  app.post("/points", function (req, res) {
    console.log("points");
    //console.log(req.body);
    res.send({"success": true});
    socket.broadcast.emit('new points', {
      username: req.body.username,
      points: req.body.newPoints
    });

    gameController.currentGame.incrementPoints(req.body.username, req.body.newPoints);
  });

  // when the client emits 'new message', this listens and executes
  socket.on('new message', function (data) {
    // we tell the client to execute 'new message'
    socket.broadcast.emit('new message', {
      username: socket.username,
      message: data
    });
  });

  // when the client emits 'add user', this listens and executes
  socket.on('add user', function (username) {
    if (addedUser) return;

    // we store the username in the socket session for this client
    socket.username = username;
    ++numUsers;
    addedUser = true;
    socket.emit('login', {
      numUsers: numUsers
    });
    // echo globally (all clients) that a person has connected
    socket.broadcast.emit('user joined', {
      username: socket.username,
      numUsers: numUsers
    });
  });

  // when the client emits 'typing', we broadcast it to others
  socket.on('typing', function () {
    socket.broadcast.emit('typing', {
      username: socket.username
    });
  });

  // when the client emits 'stop typing', we broadcast it to others
  socket.on('stop typing', function () {
    socket.broadcast.emit('stop typing', {
      username: socket.username
    });
  });

  // when the user disconnects.. perform this
  socket.on('disconnect', function () {
    if (addedUser) {
      --numUsers;

      // echo globally that this client has left
      socket.broadcast.emit('user left', {
        username: socket.username,
        numUsers: numUsers
      });
    }
  });
});
