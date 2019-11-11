jsf.ajax.addOnEvent(function(data) {
    if (data.status == "success") {
        var viewState = getViewState(data.responseXML);

        for (var i = 0; i < document.forms.length; i++) {
            if (!hasViewState(document.forms[i])) {
                createViewState(document.forms[i], viewState);
            }
        }
    }
});

function getViewState(responseXML) {
    var updates = responseXML.getElementsByTagName("update");

    for (var i = 0; i < updates.length; i++) {
        if (updates[i].getAttribute("id") == "javax.faces.ViewState") {
            return updates[i].firstChild.nodeValue;
        }
    }

    return null;
}

function hasViewState(form) {
    for (var i = 0; i < form.elements.length; i++) {
        if (form.elements[i].name == "javax.faces.ViewState") {
            return true;
        }
    }

    return false;
}

function createViewState(form, viewState) {
    var hidden;
    
    try {
        hidden = document.createElement("<input name='javax.faces.ViewState'>"); // IE6-8.
    } catch(e) {
        hidden = document.createElement("input");
        hidden.setAttribute("name", "javax.faces.ViewState");
    }

    hidden.setAttribute("type", "hidden");
    hidden.setAttribute("value", viewState);
    hidden.setAttribute("autocomplete", "off");
    form.appendChild(hidden);
}