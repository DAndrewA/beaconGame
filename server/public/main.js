$(function() {
  var FADE_TIME = 150; // ms
  var TYPING_TIMER_LENGTH = 400; // ms
  var COLORS = [
    '#e21400', '#91580f', '#f8a700', '#f78b00',
    '#58dc00', '#287b00', '#a8f07a', '#4ae8c4',
    '#3b88eb', '#3824aa', '#a700ff', '#d300e7'
  ];

  // Initialize variables
  var $window = $(window);
  var $usernameInput = $('.usernameInput'); // Input for username
  var $messages = $('.messages'); // Messages area
  var $inputMessage = $('.inputMessage'); // Input message input box

  // Prompt for setting a username
  var username;
  var connected = false;
  var typing = false;
  var lastTypingTime;
  var $currentInput = $usernameInput.focus();

  var socket = io();

  var clock;
  var Clock = function(startTime){
    var self = this;
    startTime = Math.ceil(startTime/1000);
    self.flip = $(".countdown").FlipClock(startTime, {
      clockFace: 'Counter'
    });
    self.start = function(){
      setTimeout(function(){
        setInterval(function () {
          self.flip.decrement();
        }, 1000)
      });
    };
    self.setTime = function(time){
      self.flip.setTime(Math.ceil(time/1000));
    }
  };

  function addParticipantsMessage (data) {
    var message = '';
    if (data.numUsers === 1) {
      message += "there's 1 participant";
    } else {
      message += "there are " + data.numUsers + " participants";
    }
    log(message);
  }
  // Sends a chat message
  function sendMessage () {
    var message = $inputMessage.val();
    // Prevent markup from being injected into the message
    message = cleanInput(message);
    // if there is a non-empty message and a socket connection
    if (message && connected) {
      $inputMessage.val('');
      addChatMessage({
        username: username,
        message: message
      });
      // tell server to execute 'new message' and send along one parameter
      socket.emit('new message', message);
    }
  }

  // Log a message
  function log (message, options) {
    var $el = $('<li>').addClass('log').text(message);
    addMessageElement($el, options);
  }

  // Adds the visual chat message to the message list
  function addChatMessage (data, options) {
    // Don't fade the message in if there is an 'X was typing'
    options = options || {};
    options.fade = true;

    var $usernameDiv = $('<span class="username"/>')
      .text(data.username)
      .css('color', getUsernameColor(data.username));
    var $messageBodyDiv = $('<span class="messageBody">')
      .text(data.message);


    var typingClass = data.typing ? 'typing' : '';
    var $messageDiv = $('<li class="message"/>')
      .data('username', data.username)
      .addClass(typingClass)
      .append($usernameDiv, $messageBodyDiv);

    addMessageElement($messageDiv, options);
  }

  // Adds a message element to the messages and scrolls to the bottom
  // el - The element to add as a message
  // options.fade - If the element should fade-in (default = true)
  // options.prepend - If the element should prepend
  //   all other messages (default = false)
  function addMessageElement (el, options) {
    var $el = $(el);

    // Setup default options
    if (!options) {
      options = {};
    }
    if (typeof options.fade === 'undefined') {
      options.fade = true;
    }
    if (typeof options.prepend === 'undefined') {
      options.prepend = false;
    }

    // Apply options
    if (options.fade) {
      $el.hide().fadeIn(FADE_TIME);
    }
    if (options.prepend) {
      $messages.prepend($el);
    } else {
      $messages.append($el);
    }
    $messages[0].scrollTop = $messages[0].scrollHeight;
  }

  // Prevents input from having injected markup
  function cleanInput (input) {
    return $('<div/>').text(input).text();
  }

  // Gets the color of a username through our hash function
  function getUsernameColor (username) {
    // Compute hash code
    var hash = 7;
    for (var i = 0; i < username.length; i++) {
       hash = username.charCodeAt(i) + (hash << 5) - hash;
    }
    // Calculate color
    var index = Math.abs(hash % COLORS.length);
    return COLORS[index];
  }

  // Keyboard events

  $window.keydown(function (event) {
    // Auto-focus the current input when a key is typed
    if (!(event.ctrlKey || event.metaKey || event.altKey)) {
      $currentInput.focus();
    }
    // When the client hits ENTER on their keyboard
    if (event.which === 13) {
      if (username) {
        sendMessage();
        socket.emit('stop typing');
        typing = false;
      } else {
        setUsername();
      }
    }
  });

  $inputMessage.on('input', function() {
    updateTyping();
  });

  // Click events

  // Focus input when clicking on the message input's border
  $inputMessage.click(function () {
    $inputMessage.focus();
  });

  // Socket events

  // Whenever the server emits 'login', log the login message
  socket.on('login', function (data) {
    connected = true;
    // Display the welcome message
    var message = "Welcome to Socket.IO Chat – ";
    log(message, {
      prepend: true
    });
    addParticipantsMessage(data);
  });

  function PlayerList(name){
    var self = this;
    self.players = new List(name, {valueNames:['name','points']});
    self.names = [];
    self.reset = function () {
      self.names.forEach(function (name) {
        self.players.remove('name', name);
      });
    };
    self.addPlayer = function(name, points){
      self.players.add([{name:name, points:points}]);
      self.sort();
      self.names.push(name);
    };
    self.removePlayer = function(name){
      self.players.remove('name', name);
    };
    self.incrementPoints = function(name, incr){
      var players = self.players.get('name', name);

      if(players.length > 0){
        var player = players[0]._values;

        player.points += incr;
        self.players.remove('name', name);
        self.players.add([player]);
      } else {
        self.addPlayer(name, incr);
      }

      self.sort();
    };
    self.sort = function(){
      self.players.sort('points', {'order': 'desc'});
    };
  }

  var playerList;
  $.get("/begin")
      .done(function (data) {
        playerList = new PlayerList('player-list');
        clock = new Clock(data.gameTime);
        clock.start();
        data.players.forEach(function (el) {
          playerList.addPlayer(el.name, el.points);
        });
      });

  socket.on('new points', function (data) {
    connected = true;
    console.log(data);
    var message = data.username + " just got " + data.points;
    log(message);
    console.log(message);
    playerList.incrementPoints(data.username, data.points);
  });

  socket.on('first', function (firstPlayer) {
    console.log("first");
    if(firstPlayer && firstPlayer.length > 0)
      log("The winner is: " + firstPlayer);
    //addChatMessage({username: firstPlayer, fade: true, message: " has won!"});
    //addScoringMessage({rank: "1st", colour: "#D4AF37", text: firstPlayer});
  });
  socket.on('second', function (secondPlayer) {
    //addScoringMessage({rank: "2nd", colour: "#c0c0c0", message: secondPlayer});
    if(secondPlayer && secondPlayer.length > 0)
      log("In second: " + secondPlayer);
    console.log("second");
  });
  socket.on('third', function (thirdPlayer) {
    //addScoringMessage({rank: "3rd", colour: "#CD7F32", message: thirdPlayer});
    if(thirdPlayer && thirdPlayer.length > 0)
      log("In third: " + thirdPlayer);
    console.log("third");
  });

  socket.on('new game', function(gameTime){
    console.log(gameTime);
    console.log("new game reset player list");
    playerList.reset();
    clock.setTime(gameTime);
  });

  // Whenever the server emits 'new message', update the chat body
  socket.on('new message', function (data) {
    addChatMessage(data);
  });
});
