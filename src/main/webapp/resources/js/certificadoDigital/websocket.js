/**
 * @author Luciano Sampaio
 * @description The user has clicked on the download button of the JNLP file.
 * 
 */
function userClickedOnDownloadJNPLButton() {
	try {
		// 01 - It creates the object that knows how to handle WebSocket
		// connections.
		var connection = new Infox.CertDig.Connection();
		connection.start();
	} catch (ex) {
		handleException(ex);
	}
}

function handleException(ex) {
	// alert("Error: " + ex.message);
	console.error("Error: " + ex);
}

Infox.CertDig.Connection = function() {
	// By convention, we create a private variable 'that'.
	// This is used to make the object available to the private methods.
	var that = this;
	var setTimeoutId = null;
	var webSocket = null;
	var messages = getById("messages");

	/**
	 * @description It tries to establish a connection with the server.
	 */
	this.start = function() {
		// 01 - It tries (first attempt) to connect.
		startConnectionToServer(1);
	}

	this.close = function() {
		if (null != webSocket) {
			try {
				webSocket.close();
			} catch (ex) {
				handleException(ex)
			}

			webSocket = null;
		}
	}

	/**
	 * @author Luciano Sampaio
	 * @description It tries to establish a connection with the server.
	 * 
	 */
	function startConnectionToServer(nrAttempt) {
		// 01 - If the number of attempt has reached the MAXIMUM amount.
		if (Infox.CertDig.Constant.Connection.MAXIMUM_NUMBER_OF_ATTEMPTS == nrAttempt) {
			// 01.1 - Stop the loop.
			clearTimeout(setTimeoutId);

			// 01.2 - Alert about the problem.
			alertConnectionWasNotEstablished();
		} else {
			// 02 - Start trying to connect.
			openSocket(nrAttempt);
		}
	}

	function openSocket(nrAttempt) {
		try {
			// 01 - It ensures only one connection is open at a time.
			if ((null !== webSocket)
					&& (WebSocket.CLOSED !== webSocket.readyState)) {
				writeResponse("WebSocket is already opened.");
				return;
			}

			// 02 - Create a new instance of the WebSocket.
			webSocket = new WebSocket(Infox.CertDig.Constant.Connection.URL);
			/**
			 * Binds functions to the listeners for the WebSocket.
			 */
			webSocket.onopen = function(event) {
				// 01 - The connection has been successfully established, so
				// stop the timer.
				clearTimeout(setTimeoutId);

				// For reasons I can't determine, onopen gets called twice
				// and the first time event.data is undefined.
				// Leave a comment if you know the answer.
				if (undefined === event.data) {
					return;
				}

				handleResponse(event);
			};

			webSocket.onerror = function(event) {
				handleException("The following error occurred: " + event.data);

				tryToReconnect(nrAttempt);
			};

			webSocket.onmessage = function(event) {
				handleResponse(event);
			};

			webSocket.onclose = function(event) {
				handleException("Connection closed: " + event.data);

				handleResponse(event);
			};
		} catch (ex) {
			handleException(ex);
		}
	}

	function tryToReconnect(nrAttempt) {
		// 01 - The server is not listening yet, so wait for a few seconds and
		// try again.
		setTimeoutId = setTimeout(function() {
			startConnectionToServer(++nrAttempt);
		}, Infox.CertDig.Constant.Connection.WAITING_TIME);

	}

	function handleResponse(event) {
		// 01 - Parse the data into an json object.
		var message = JSON.parse(event.data);
		// type {text, image, ...}
		// cmd {PING, PONG, ...}
		// data { the content of the message }

		switch (message.cmd) {
		case Infox.CertDig.Constant.Command.CONNECTION_OPENED:
			// 01 - Start the Java code to read the smart card.
			sendSignLogin();

			break;
		case Infox.CertDig.Constant.Command.JS_ACTION:
			runJavaScript(message.data);
			break;
		case Infox.CertDig.Constant.Command.CREDENTIALS:
			receivedCredentials(message.data);
			break;
		default:
			writeResponse("Default CMD: " + message.cmd);
			break;
		}

		writeResponse(event.data);
	}

	/**
	 * Send the command to ask the user's credentials to sign his/her login
	 * information.
	 */
	function sendSignLogin() {
		sendMessage(Infox.CertDig.Constant.JsonParameter.TEXT,
				Infox.CertDig.Constant.Command.SIGN_LOGIN, "");
	}

	/**
	 * Send the command that will make the server shutdown.
	 */
	function sendQuit() {
		sendMessage(Infox.CertDig.Constant.JsonParameter.TEXT,
				Infox.CertDig.Constant.Command.QUIT, "");
	}

	function runJavaScript(data) {
		switch (data) {
		case Infox.CertDig.Constant.JSAction.actionPre:
			// show();
			break;
		case Infox.CertDig.Constant.JSAction.actionPos:
			// Hide();
			break;
		case Infox.CertDig.Constant.JSAction.actionPosOk:
			// Now, the server can be shutdown.
			sendQuit();

			// submitForm();
			break;
		case Infox.CertDig.Constant.JSAction.actionPosError:
			// TODO .
			break;
		default:
			writeResponse("runJavaScript: " + data);
			break;
		}

	}

	function receivedCredentials(data) {
		// 01 - Get the reference to the form fields;
		var certChain = getById(Infox.CertDig.Constant.JsonParameter.CERT_CHAIN);
		var signature = getById(Infox.CertDig.Constant.JsonParameter.SIGNATURE);

		// 02 - Parse the json message.
		var credentials = JSON.parse(data);

		// 03 - Set the value of the form fields;
		setValue(certChain, credentials.certChain);
		setValue(signature, credentials.signature);
	}

	function setValue(element, value) {
		if (null != element) {
			element.value = value;
		}
	}

	function sendMessage(dataType, command, dataValue) {
		var type = Infox.CertDig.Constant.JsonParameter.TYPE;
		var cmd = Infox.CertDig.Constant.JsonParameter.COMMAND;
		var data = Infox.CertDig.Constant.JsonParameter.DATA;

		try {
			var json = JSON.stringify({
				type : dataType,
				cmd : command,
				data : dataValue || ""
			});

			webSocket.send(json);
		} catch (ex) {
			handleException(ex);
		}

		writeResponse(dataValue);
	}

	function writeResponse(text) {
		messages.innerText += "\r\n" + text;
	}

	function alertConnectionWasNotEstablished() {
		alert("alertConnectionWasNotEstablished");
	}

	function getById(id) {
		return document.getElementById(id);
	}

};