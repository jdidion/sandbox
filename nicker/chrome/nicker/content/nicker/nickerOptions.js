function nicker_initializeOptions() {
	var nickerPrefs = Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefService).getBranch("nicker.");
	if (nickerPrefs.prefHasUserValue("optDomain")) {
		document.getElementById("nickerDomain").value = nickerPrefs.getCharPref("optDomain");
	}
	if (nickerPrefs.prefHasUserValue("optAdminName")) {
		document.getElementById("nickerAdminName").value = nickerPrefs.getCharPref("optAdminName");
	}
	if (nickerPrefs.prefHasUserValue("optAdminPassword")) {	
		document.getElementById("nickerAdminPassword").value = nickerPrefs.getCharPref("optAdminPassword");
	}
	if (nickerPrefs.prefHasUserValue("optUserNotAdmin")) {
		document.getElementById("nickerUserNotAdmin").checked = nickerPrefs.getBoolPref("optUserNotAdmin");
	}
	if (nickerPrefs.prefHasUserValue("optUserName")) {
		document.getElementById("nickerUserName").value = nickerPrefs.getCharPref("optUserName");
	}
}

function nicker_saveOptions() {
	var nickerPrefs = Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefService).getBranch("nicker.");
	nickerPrefs.setCharPref("optDomain", document.getElementById("nickerDomain").value);
	nickerPrefs.setCharPref("optAdminName", document.getElementById("nickerAdminName").value);
	nickerPrefs.setCharPref("optAdminPassword", document.getElementById("nickerAdminPassword").value);
	nickerPrefs.setBoolPref("optUserNotAdmin", document.getElementById("nickerUserNotAdmin").checked);
	nickerPrefs.setCharPref("optUserName", document.getElementById("nickerUserName").value);
}

function toggle_user_email_disabled() {
	if (document.getElementById("nickerUserNotAdmin").checked) {
		document.getElementById("nickerUserName").removeAttribute('disabled');
	}
	else {
		document.getElementById("nickerUserName").setAttribute('disabled', 'true');
	}
}
