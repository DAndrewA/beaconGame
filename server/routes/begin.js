var express = require("express");
var router = express.Router();
var gameController = require("../data/game.js");

router.get("/", function (req, res) {
  res.send({
    "gameTime": gameController.currentGame.endDate - Date.now(),
    "players": gameController.currentGame.players
  });
});

module.exports = router;
