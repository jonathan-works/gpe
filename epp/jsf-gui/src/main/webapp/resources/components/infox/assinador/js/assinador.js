(function(){
	const passwordPrompt = function(params) {
	    let { mensagem, labelConfirmBtn, labelCancelBtn,
	        source, execute, render, onbegin, oncomplete,
	        callback, passwordInputPlaceholder } = params;
        
	        if (typeof mensagem !== "string") mensagem = "Password:";
	        if (typeof labelConfirmBtn !== "string") labelConfirmBtn = "Submit";
	        if (typeof callback !== "function") callback = function(password){};

	        const submit = function() {
	            callback(input.value);
	            document.body.removeChild(div);
	        };
	        const cancel = function() {
	            document.body.removeChild(div);
	        };

	        const div = document.createElement("div");
	        div.classList.add('d-msg');

	        const header = document.createElement('div');
	        header.classList.add('d-msg-h');
	        div.appendChild(header);

	        const headerText = document.createElement("label");
	        headerText.textContent = mensagem;
	        header.appendChild(headerText);

	        const content = document.createElement('div');
	        content.classList.add('d-msg-c', 'mdl-shadow--4dp');
	        content.style.textAlign='center';
	        div.appendChild(content);

	        const input = document.createElement("input");
	        input.type = "password";
	        input.placeholder = passwordInputPlaceholder || "Digite sua senha aqui";
	        input.style.margin='10px 0';
	        input.addEventListener("keyup", function(e) {
	            if (event.keyCode == 13) submit();
	        }, false);
	        
	        content.appendChild(input);

	        const buttonsDiv = document.createElement("div");
	        buttonsDiv.style.textAlign="center;"
	        content.appendChild(buttonsDiv);
	        
	        const confirmButton = document.createElement("button");
	        confirmButton.classList.add('buttons');
	        confirmButton.textContent = labelConfirmBtn;
	        confirmButton.addEventListener("click", submit, false);
	        buttonsDiv.appendChild(confirmButton);

	        const cancelButton = document.createElement("button");
	        cancelButton.classList.add('buttons');
	        cancelButton.textContent = labelCancelBtn;
	        cancelButton.addEventListener("click", cancel, false);
	        buttonsDiv.appendChild(cancelButton);

	        document.body.appendChild(div);
	        input.focus();
	};
	const assinador = {
		assinarEletronicamente:function(args){
			const params = {... args};
	        const { source, execute, render, onbegin, oncomplete } = params;
	        
			params.callback = function(password) {
			    const payload = password;
			    jsf.ajax.request(source, null, {
			        execute,
			        render,
			        params:payload,
			        "javax.faces.behavior.event":"assinaturaEletronicaClick",
			        onevent:function(event) {
		                if (event.type === 'event') {
		                    switch(event.status) {
		                    case 'begin':
		                        eval(onbegin||';');
		                        break;
		                    case 'complete':
		                        eval(oncomplete||';');
		                        break;
		                    }
		                }
		            }
			    });
			};
            passwordPrompt(params);
		}
	};
    namespace("infox.Assinador", assinador, {
        callback:function (Assinador) {
        }    
    });
})();