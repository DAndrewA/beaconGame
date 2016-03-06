/**
 * Created by matt on 05/03/2016.
 */

var NUM_BEACONS = 4;
var NUM_PERIODS = 6;

var randomBeaconMultiplier = function(){
  return Math.floor(Math.random()*150) - 50;
};

var Period = function(num, startDate, periodLength){
  var self = this;

  self.num = num;
  self.startDate = startDate;
  self.endDate = self.startDate + periodLength;

  self.beaconPointMultipliers = new Array(NUM_BEACONS);
  for (var i = 0; i < self.beaconPointMultipliers.length; i++)
    self.beaconPointMultipliers[i] = randomBeaconMultiplier();
};
var Game = function(gameTime){
  var self = this;

  self.players = [];
  self.startDate = Date.now();
  self.endDate = self.startDate + gameTime;
  self.gameLength = self.endDate - self.startDate;
  self.numPeriods = NUM_PERIODS;
  self.periodLength = self.gameLength / self.numPeriods;

  self.periods = new Array(NUM_PERIODS);
  for (var i = 0; i < self.periods.length; i++)
    self.periods[i] = new Period(i, self.startDate+i*self.periodLength, self.periodLength);

  self.toJson = function () {
    return {
      players: self.players,
      startDate: self.startDate,
      endDate: self.endDate,
      gameLength: self.gameLength,
      numPeriods: self.numPeriods,
      periodLength: self.periodLength,
      periods: self.periods
    };
  };

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

  self.sortPlayers = function(){
    self.currentGame.players.sort(function (a, b) {
      return b-a;
    });
  };

  self.endGame = function(gameTime){
    self.currentGame.endGame();
    self.currentGame = new Game(gameTime);
  }
};

var gameController = new GameController();

module.exports = gameController;
