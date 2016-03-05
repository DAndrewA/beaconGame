/**
 * Created by matt on 05/03/2016.
 */
var Game = function(gameTime){
  var self = this;

  self.players = [];
  self.startDate = Date.now();
  self.endDate = self.startDate + gameTime;

  self.addPlayer = function (name, points) {
    self.players.push({name: name, points: points});
  };
  self.incrementPoints = function(name, points){
    var playerIndex = -1;
    for (var i = 0; i < self.players.length; i++) {
      if(self.players[i].name === name){
        playerIndex = i;
        break;
      }
    }
    if(playerIndex !== -1){
      self.players[playerIndex].points += points;
    } else {
      self.addPlayer(name, points);
    }
  };
  self.endGame = function(){
    console.log("end of game");
  };
};

var GameController = function(){
  var self = this;

  self.currentGame = new Game();
  self.endGame = function(gameTime){
    self.currentGame.endGame();
    self.currentGame = new Game(gameTime);
  }
};

var gameController = new GameController();

module.exports = gameController;
