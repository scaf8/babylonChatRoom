'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;
var username = null;
var offensiveMessage = "<offensive message deleted>";

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = document.querySelector('#name').value.trim();

    if(username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}


function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/chat.addUser", {}, username);

    connectingElement.classList.add('hidden');
}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        var chatMessage = {
            content: messageInput.value,
            type: 'CHAT'
        };
        var headers = {
            username: username
        };
        
        stompClient.send("/app/chat.sendMessage", headers, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function renderMessages(messages) {
  var messageArea = document.getElementById('messageArea');

  messages.forEach(function(message) {
    var messageElement = document.createElement('li');

    if (message.type === 'JOIN') {
      messageElement.classList.add('event-message');
    } else if (message.type === 'LEAVE') {
      messageElement.classList.add('event-message');
    } else {
      messageElement.classList.add('chat-message');

      var avatarElement = document.createElement('i');
      var avatarText = document.createTextNode(message.sender.username[0]);
      avatarElement.appendChild(avatarText);
      avatarElement.style['background-color'] = getAvatarColor(message.sender.username);

      messageElement.appendChild(avatarElement);

      var usernameElement = document.createElement('span');
      var usernameText = document.createTextNode(message.sender.username);
      usernameElement.appendChild(usernameText);
      messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
    var messageText = null;
    // controlla se il messaggio è offensivo
    if(message.type === 'STRIKE') {
		messageText = document.createTextNode(offensiveMessage);
  	}
  	else {
		messageText = document.createTextNode(message.content);
	}
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    var timestampElement = document.createElement('span');
    var timestamp = new Date(message.timestamp);
    var formattedTimestamp = timestamp.toLocaleString();
    var timestampText = document.createTextNode(formattedTimestamp);
    timestampElement.appendChild(timestampText);
    messageElement.appendChild(timestampElement);

    messageArea.appendChild(messageElement);
  });

  messageArea.scrollTop = messageArea.scrollHeight;
}

// Ottieni i messaggi dal server
fetch('/rest/messages')
  .then(response => response.json())
  .then(data => {
    // Passa i messaggi alla funzione renderMessages
    renderMessages(data);
  })
  .catch(error => {
    console.error('Errore durante il recupero dei messaggi:', error);
  });


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender.username[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender.username);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender.username);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
    
    var messageText = null;
    // controlla se il messaggio è offensivo
    if(message.type === 'STRIKE') {
		messageText = document.createTextNode(offensiveMessage);
		// controlla se l'utente corrente ne è l'autore
		if(username === message.sender.username) {
			// Effettua una chiamata AJAX al server per ottenere il numero di strikes dell'utente
  			$.get("/getUserStrikes", { username: username }, function (strikes) {
    		if (strikes === 4) {
    			$('#strikeModal').modal('show');
    		}
    		// se l'utente ha superato il limite, ricarica la pagina per farlo uscire
    		if (strikes >= 5) {
				window.location.reload(true);
			}
  			});
		}
  	}
  	else {
		messageText = document.createTextNode(message.content);
	}
    
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

	var timestampElement = document.createElement('span');
    var timestamp = new Date(message.timestamp);
    var formattedTimestamp = timestamp.toLocaleString();
    var timestampText = document.createTextNode(formattedTimestamp);
    timestampElement.appendChild(timestampText);
    messageElement.appendChild(timestampElement);
	
    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}


function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)