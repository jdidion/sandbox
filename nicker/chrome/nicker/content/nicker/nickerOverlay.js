// Global variables

var gBundle = Components.classes["@mozilla.org/intl/stringbundle;1"].getService(Components.interfaces.nsIStringBundleService);
var nickerstrings = gBundle.createBundle("chrome://nicker/locale/nicker.properties");
var unablelocateformalert = nickerstrings.GetStringFromName("unablelocateform");
var unablelocateloginalert = nickerstrings.GetStringFromName("unablelocateemail");
var optionsnotset = nickerstrings.GetStringFromName("optionsnotset");
var copypasteemail = nickerstrings.GetStringFromName("copypasteemail");
var erroroccurredalert = nickerstrings.GetStringFromName("erroroccurred");

var formUrl = false;
var form = false;
var nickname = false;
var authToken = false;
var emailElement = false;
var httpRequest;
var httpRequestCallbackFunction;

// Options object

var nickerOptions = new Object();

// Set event to enable/disable Nicker menu items

window.addEventListener("load", _setToggleMenuItemEvent, true);

function _setToggleMenuItemEvent() {
	window.removeEventListener("load", _setToggleMenuItemEvent, true);
	document.getElementById("contentAreaContextMenu").addEventListener("popupshowing", _toggleMenuItems, false);
	// Processing continues in _toggleMenuItems when context menu is accessed.
}

function _toggleMenuItems() {
	if (gContextMenu.onTextInput) {
		nickerOptions.haveForm = true;
		nickerOptions.targetForm = gContextMenu.target.form;
	}
	document.getElementById("nicker").setAttribute('hidden', !gContextMenu.onTextInput);
	document.getElementById("nickerseparator").setAttribute('hidden', !gContextMenu.onTextInput);
}

// Nicker Primary Menu Item

function Nicker() {
	// Execution starts here, this function is specified by nickerOverlay.xul
	formUrl = window._content.document.location.href;
	nickname = _currentDomainNickname(); 
	if (_setOptions()) {
		_getNickname();		
	}
	else {
		alert(optionsnotset);
	}		
}

function _setOptions() {
	var nickerPrefs = Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefService).getBranch("nicker.");
	var domain = false;
	if (nickerPrefs.prefHasUserValue("optDomain")) {
		domain = nickerPrefs.getCharPref("optDomain");
		nickerOptions.domain = domain;
	}
	else {
		return false;
	}
	if (nickerPrefs.prefHasUserValue("optAdminName")) {
		nickerOptions.adminName = nickerPrefs.getCharPref("optAdminName");
		nickerOptions.adminEmail = nickerOptions.adminName + '@' + domain;
	} 
	else {
		return false;
	}
	if (nickerPrefs.prefHasUserValue("optAdminPassword")) {
		nickerOptions.adminPassword = nickerPrefs.getCharPref("optAdminPassword");
	} 
	else {
		return false;
	}
	if (nickerPrefs.prefHasUserValue("optUserNotAdmin") && nickerPrefs.getBoolPref("optUserNotAdmin")) {
		if (nickerPrefs.prefHasUserValue("optUserName")) {
			nickerOptions.userName = nickerPrefs.getCharPref("optUserName");
			nickerOptions.userEmail = nickerOptions.userName + '@' + domain;
		}
		else {
			return false;
		}
	} 
	else {
		nickerOptions.userName = nickerOptions.adminName;
		nickerOptions.userEmail = nickerOptions.adminEmail;
	}
	nickerOptions.haveForm = false;
	nickerOptions.targetForm = null;
	return true;
}

function _currentDomainNickname() {
	var noSubDomain = formUrl.match(/\/\/([^\/\.]+)\.[^\/\.]+\//);
	if (noSubDomain != null) {
		return noSubDomain[1];
	}
	else {
		var subDomain = formUrl.match(/\/\/[^\/]+\.([^\/\.]+)\.[^\/\.]+\//);
		return subDomain[1];
	}
}

// 1. log into admin account
// 2. login callback: fetch nickname
// 3. fetch callback; if exists, populate email field, otherwise create nickname
// 3. create callback: populate email field
function _getNickname() {
	_httpPost('https://www.google.com/accounts/ClientLogin', _loginCallback, 
			  { 'Content-type' : 'application/x-www-form-urlencoded' },
			  'Email=' + escape(nickerOptions.adminEmail) + '&Passwd=' + escape(nickerOptions.adminPassword) +'&accountType=HOSTED&service=apps');
}

// event callbacks

function _loginCallback() {
	authToken = false;
	var rows = httpRequest.responseText.split('\n');
	for (var i = 0; i < rows.length; i++) {
		var fields = rows[i].split('=');
		if (fields[0] == 'Auth') {
		    authToken = fields[1];
			break;	
		}
	}
	if (authToken) {
		_httpGet('https://www.google.com/a/feeds/' + nickerOptions.domain + '/nickname/2.0/' + nickname, _getNicknameCallback,
				 { 'Content-type' : 'application/atom+xml', 'Authorization' : 'GoogleLogin auth=' + authToken } );		
	}
	else {
		alert(erroroccurredalert);
	}
}

function _getNicknameCallback() {
	if (_parseNicknameResponse()) {
		_presentNickname();
	}
	else {
		var errorCode = _parseError();
		if (errorCode == 1301) {
			_httpPost('https://www.google.com/a/feeds/' + nickerOptions.domain + '/nickname/2.0' , _createNicknameCallback,
					  { 'Content-type' : 'application/atom+xml', 'Authorization' : 'GoogleLogin auth=' + authToken },
					  '<?xml version="1.0" encoding="UTF-8"?>\n' +
						'<atom:entry xmlns:atom="http://www.w3.org/2005/Atom" xmlns:apps="http://schemas.google.com/apps/2006">' +
						'<atom:category scheme="http://schemas.google.com/g/2005#kind" term="http://schemas.google.com/apps/2006#nickname"/>' +
						'<apps:nickname name="' + nickname + '"/>' +
						'<apps:login userName="' + nickerOptions.userName +'"/>' +
					  '</atom:entry>');
		}
		else {
			alert(erroroccurredalert + errorcode);
		}
	}
}

function _createNicknameCallback() {
	if (_parseNicknameResponse()) {
		_presentNickname();
	}
	else {
		alert(erroroccurredalert + _parseError());
	}	
}

function _parseNicknameResponse() {
	var domParser = new DOMParser();	
	var response = domParser.parseFromString(httpRequest.responseText, 'text/xml');
	var xpe = new XPathEvaluator();
	return xpe.evaluate('string(/atom:entry/apps:nickname[@name])', response, null, XPathResult.UNORDERED_NODE_SNAPSHOT_TYPE, null);
}

function _parseError() {
	var xpe = new XPathEvaluator();
	var errorCode = xpe.evaluate('number(/AppsForYourDomainErrors/error/[@errorCode])', response, null, XPathResult.UNORDERED_NODE_SNAPSHOT_TYPE, null);
}

function _presentNickname() {
	var email = nickname + '@' + nickerOptions.domain;
	if (_locateForm()) {
		emailElement.value = email;
	} 
	else {
		alert(copypasteemail + email);
	}
}

function _locateForm () {
	var formFound = false;
	if (nickerOptions.haveForm) {
		form = nickerOptions.targetForm;
		formFound = _formHasEmailField(form);
	}
	if (!formFound) {
		var currentWindow = window._content;
		if (!currentWindow.document.forms.length > 0 && currentWindow.frames.length > 0) {
			for (var i = 0; i < currentWindow.frames.length; i++) {
				if (_locateFormInDocument(currentWindow.frames[i].document)) {
					formFound = true;
					break;
				}
			}
		} 
		else {
			formFound = _locateFormInDocument(currentWindow.document);
		}		
	}
	return formFound;
}

function _locateFormInDocument(targetDocument) {
	for (var i = 0; i < targetDocument.forms.length; i++) {
		form = targetDocument.forms[i];
		if (_formHasEmailField(form)) {
			return true;
		}
	}
	return false;
}

function _formHasEmailField() {
	emailElement = false;
	for (var ii = 0; ii < form.elements.length; ii++) {
		var currentElement = form.elements[ii];
		if (currentElement.type.match(/text/i) && currentElement.name.match(/ID|un|name|user|usr|log|email|mail|nick/i)) {
			emailElement = currentElement;			
			return true;
		}
	}
	return false;
}

function _httpGet(url, callbackFunction, headers, content) {
	_http(url, 'GET', callbackFunction, headers, content);
}

function _httpPost(url, callbackFunction, headers, content) {
	_http(url, 'POST', callbackFunction, headers, content);
}
	
function _http(url, method, callbackFunction, headers, content) {
	httpRequestCallbackFunction = callbackFunction;
	httpRequest = new XMLHttpRequest();
	httpRequest.onreadystatechange = _httpExecuteCallback;
	httpRequest.open(method, url, true);
	if (headers) {
		for (var key in headers) {
			httpRequest.setRequestHeader(key, headers[key]);
		}
	}
	httpRequest.send(content);
}

function _httpExecuteCallback() {
    if (httpRequestCallbackFunction != null && httpRequest.readyState == 4) {
		if (httpRequest.status == 200) {
		    httpRequestCallbackFunction();
		    httpRequestCallbackFunction = null;
		} 
		else {
		    alert((erroroccurredalert) + httpRequest.status);		  
	    }
    }
}
