var express = require("express");
var router = express.Router();
var gameController = require("../data/game.js");

router.get("/", function (req, res) {
  res.send(gameController.currentGame.toJson());
});

module.exports = router;
