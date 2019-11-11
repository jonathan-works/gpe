/**
 * @author Luciano Sampaio
 * @description It holds all constants of the application. Hard-Coded values
 *              should be avoided at all costs.
 * 
 */

// The global name space.
var Infox = Infox || {};
Infox.CertDig = Infox.CertDig || {};
Infox.CertDig.Constant = Infox.CertDig.Constant || {};

Infox.CertDig.Constant.Connection = {
	URL : "ws://localhost:8888/server/connectionhandler",
	// The maximum number of connections attempt.
	MAXIMUM_NUMBER_OF_ATTEMPTS : 20,
	// The amount of time to wait until a new connection attempt is made.
	WAITING_TIME : 3000
};

Infox.CertDig.Constant.JsonParameter = {
	// type {text, image, ...}
	// cmd {PING, PONG, ...}
	// data { the content of the message }
	TYPE : "type",
	COMMAND : "cmd",
	DATA : "data",

	TEXT : "text"
};

Infox.CertDig.Constant.Command = {
	CONNECTION_OPENED : "CONNECTION_OPENED",
	SIGN_LOGIN : "SIGN_LOGIN",
	SIGN_DOCUMENT : "SIGN_DOCUMENT",
	JS_ACTION : "JS_ACTION",
	CREDENTIALS : "CREDENTIALS",
	QUIT : "QUIT",
};

Infox.CertDig.Constant.JSAction = {
	actionPre : "actionPre",
	actionPos : "actionPos",
	actionPosOk : "actionPosOk",
	actionPosError : "actionPosError"
};