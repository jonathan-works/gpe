 (function () {
	function Select(args) {
	      var $parent = document.createElement("div");
	      var $element = $parent.appendChild(document.createElement("select"));
	      var $prechange = args.beforeOnChange || function(event){return true;};
	      var $onchange = args.onChange || function(event){};
	      var $postchange = args.afterOnChange || function(event){};
	      var $onclick = args.onClick || function(event) {};
	      
	      function $addElementsToSelect(elements) {
			for (var i=0; i<elements.length; i++) {
				var $item = elements[i];
				var $option = document.createElement("option");
				$option.value = $item.value;
				$option.innerHTML = $item.label;
				$element.appendChild($option);
			}
	      }
	      
	      $element.onchange = function(event) {
		      if ($prechange(event)) {
			      $onchange(event);
		      }
		      $postchange(event);
	      };
	      
	      if (args.options) {
	    	  $addElementsToSelect(args.options);
	      }
	      
	      if (args.variables) {
	    	  //$addElementsToSelect(args.variables);
	    	  for (var i=0; i<args.variables.length; i++) {
					var $item = args.variables[i];
					var $option = document.createElement("option");
					$option.value = $item;
					$option.innerHTML = $item;
					$element.appendChild($option);
				}
	      }
	      
	      return $parent;
	}
	
	function Image(args) {
		var $image = document.createElement("img");
		$image.src = args.src;
		$image.onclick = args.onclick || function () {};
		$image.title = args.title;
		for(var row in args.style) {
		    $image.style[row] = args.style[row];
		}
		return $image;
	}
	
	function Span(args) {
		var $element = document.createElement("span");
		$element.innerHTML = args.label;
		
		if (args.display) {
			$element.style.display = args.display;
		}
		$element.style.padding = "0px 5px 0px 5px";
		$element.onmouseover = args.onmouseover || function(event) {};
		$element.onmouseout = args.onmouseout || function(event) {};
		$element.onclick = args.onclick || function(event) {};
	
		return $element;
	}

	function InputNumber(args) {
		var $number = ".";
		var $message = args.message || "Insira um valor numérico";
		while (true) {
			$number = window.prompt($message, "0");
			$number = $number.replace(".", "").replace(",", ".");
			$number = parseFloat($number);
			if ((!isNaN($number)) && (isFinite($number))) {
				break;
			}
			$message = args.errorMessage || "Insira um valor numérico válido!!!";
		}
		return $number;
	}

	function AritOperSelect(args) {
		args.label = args.label || {};
		args.options = [
			{value:"", label:args.label.null || "Escolha uma operação aritmética."},
			{value:"+", label:args.label.plus || "Soma"},
			{value:"-", label:args.label.minus || "Subtração"},
			{value:"*", label:args.label.mult || "Multiplicação"},
			{value:"/", label:args.label.div || "Divisão"},
			{value:"",label:""},
			{value:"num", label:args.label.number || "Valor Numérico"}
		];
		return Select(args);
	}

	function ConclusionSelect(args) {
		args.label = args.label || {};
		args.options = [
		      {value:"", label:args.label.null || "Selecione uma conclusão"},
		      {value:"if", label:args.label.condition || "Nova condição"}
		];
		return Select(args);
	}

	function BooleanSelect(args) {
		args.label = args.label || {};
		args.options = [
			{value:"", label:args.label.null || "Escolha uma operação lógica."},
			{value:"true", label:args.label.true || "Verdadeiro"},
			{value:"false", label:args.label.false || "Falso"},
			{value:"||", label:args.label.or || "Disjunção"},
			{value:"&&", label:args.label.and || "Conjunção"},
			{value:"not", label:args.label.not || "Negação"},
			{value:"", label:""},
			{value:"==", label:args.label.equals || "Igual a"},
			{value:"!=", label:args.label.notEqual || "Diferente de"},
			{value:">=", label:args.label.gte || "Maior ou igual a"},
			{value:">", label:args.label.gt || "Maior que"},
			{value:"<=", label:args.label.lte || "Menor ou igual a"},
			{value:"<", label:args.label.lt || "Menor que"}
		];
		return Select(args);
	}

	defineObject("br.com.infox.util.ExpressionWizard",
		function(args) {
			if (this.ExpressionWizard && this.ExpressionWizard===br.com.infox.util.ExpressionWizard) {
				return;
			}
			/* Variables */
			// private 
			var $booleanVariableList = args.variables.boolean || [];
			var $numberVariableList = args.variables.number || [];
			var $transitionList = args.variables.transition || [];
			var $messages = args.messages || {};
			var $expressionDiv;
			var $inputId;
			var $clear = false;
			var $imageSrc = "";
			
			//public 
			
			/* EVENTS */
			// private 
			function onMouseOverSpan(event) {
				var $parent = event.currentTarget.parentNode;
				$parent.style.backgroundColor = "#D4F2D6";
				$parent.style.padding = "5px 5px 5px 5px";
				$parent.style.margin = "-5px 0px 0px -5px";
			}
			
			function onMouseExitSpan(event) {
				var $parent = event.currentTarget.parentNode;
				$parent.style.backgroundColor=$parent.style.padding=$parent.style.margin=$parent.style.cursor="";
			}
			// public 
			
			/* METHODS */
			
			function $removeChildrenOf(element) {
			      while(element.childNodes.length > 0) {
				      element.removeChild( element.firstChild );
			      }
			}
			
			function $resetElement(element) {
				element.style.backgroundColor="";
				element.style.cursor="";
				$removeChildrenOf( element );
				return element;
			}
			
			function createSpan(args) {
				args.onmouseover = onMouseOverSpan;
				args.onmouseout = onMouseExitSpan;
				return Span(args);
			}
			
			function setTextArea(text) {
				if ($expressionDiv.getElementsByTagName("select").length == 0) {
					var $inputDiv = document.getElementById($inputId);
					$inputDiv.textContent="#"+"{"+text+"}";
					if ($inputDiv["onchange"]) {
						$inputDiv["onchange"].call( $inputDiv );
					}
				}
			}
			
			function aritOperSelect() {
				function mouseClick(event) {
					$resetElement(event.currentTarget.parentNode).appendChild( aritOperSelect() );
				}
				var $selectLabels = $messages.select || {};
				return AritOperSelect({
				  label:$selectLabels.arithmetic || false,
				  variables : $numberVariableList,
				  onChange : function(event) {
					var sValue = event.currentTarget.value;
					var pNode = event.currentTarget.parentNode;
					if (sValue != "") {
						$removeChildrenOf( pNode );
						if (sValue == "num") {
							var $nmInput = $messages.numberInput || {};
							pNode.appendChild( createSpan({
								onclick:mouseClick,
								label: InputNumber({
									message:$nmInput.label || false,
									errorMessage:$nmInput.error || false})
							}));
						} else if ((sValue == "+") || (sValue == "-")
								|| (sValue == "/") || (sValue == "*")) {
							pNode.appendChild( createSpan({label:"(", onclick:mouseClick}) );
							pNode.appendChild( aritOperSelect() );
							pNode.appendChild( createSpan({label: sValue, onclick:mouseClick }) );
							pNode.appendChild( aritOperSelect() );
							pNode.appendChild( createSpan({label:")", onclick:mouseClick }) );
						} else {
							pNode.appendChild( createSpan({label:sValue, onclick:mouseClick }) );
						}
						setTextArea($expressionDiv.textContent);
					}
				  }
				});
			}
			
			function booleanSelect() {
				function mouseClick(event) {
					$resetElement(event.currentTarget.parentNode).appendChild( booleanSelect() );
				}
				var $selectLabels = $messages.select || {};
				return BooleanSelect({
				  label:$selectLabels.boolean || false,
				  variables : $booleanVariableList,
				  onChange : function(event) {
					var sValue = event.currentTarget.value;
					var pNode = event.currentTarget.parentNode;
					if (sValue != "") {
						$removeChildrenOf( pNode );
						if ((sValue == "||") || (sValue == "&&")) {
							pNode.appendChild( createSpan({label:"(", onclick:mouseClick}));
							pNode.appendChild( booleanSelect() );
							pNode.appendChild( createSpan({label:sValue, onclick:mouseClick}) );
							pNode.appendChild( booleanSelect() );
							pNode.appendChild( createSpan({label:")", onclick:mouseClick}));
						} else if (sValue == "not") {
							pNode.appendChild( createSpan({label:"!", onclick:mouseClick}) );
							pNode.appendChild( booleanSelect() );
						} else if ((sValue == "==") || (sValue == "!=")
								|| (sValue == ">=") || (sValue == ">")
								|| (sValue == "<=") || (sValue == "<")) {
							pNode.appendChild( createSpan({label:"(", onclick:mouseClick}) );
							pNode.appendChild( aritOperSelect() );
							pNode.appendChild( createSpan({label: sValue, onclick:mouseClick }) );
							pNode.appendChild( aritOperSelect() );
							pNode.appendChild( createSpan({label:")", onclick:mouseClick}) );
						} else {
							pNode.appendChild( createSpan({label:sValue, onclick:mouseClick}) );
						}
						
						setTextArea($expressionDiv.textContent);
					}
				  }
				});
			}
			
			function conclusionSelect() {
				function mouseClick(event) {
					$resetElement(event.currentTarget.parentNode).appendChild( conclusionSelect() );
				}
				var $selectLabels = $messages.select || {};
				return ConclusionSelect({
					label:$selectLabels.conclusion || false,
					variables : $transitionList,
					onChange : function(event) {
						var sValue = event.currentTarget.value;
						var pNode = event.currentTarget.parentNode;
						if (sValue != "") {
							$removeChildrenOf( pNode );
							if (sValue == "if") {
								pNode.appendChild( createSpan({label:"(", onclick:mouseClick}) );
								_expression(pNode);
								pNode.appendChild( createSpan({label:")", onclick:mouseClick}) );
							} else {
								pNode.appendChild( createSpan({label:sValue, onclick:mouseClick}) );
							}
							setTextArea($expressionDiv.textContent);
						}
					}
				});
			}
			
			function _expression(parent) {
				function mouseClick(event) {
					$resetElement($expressionDiv).appendChild( addImageWiz() );
					$clear = false;
				}
				parent.appendChild( booleanSelect() );
				parent.appendChild( createSpan({label:"?", onclick:mouseClick}) );
				parent.appendChild( conclusionSelect() );
				parent.appendChild( createSpan({label:":", onclick:mouseClick}) );
				parent.appendChild( conclusionSelect() );
				return parent;
			}
			
			function addImageWiz() {
				var $imageMsg = $messages.image || {};
				return Image({
					src:$imageSrc,
					title:$imageMsg.title || "Wizard de Expressões",
					style:{
						cursor:"pointer"
					},
					onclick: function() {
						$removeChildrenOf( $expressionDiv );
						if ($clear) {
							$expressionDiv.appendChild( addImageWiz() );
							$clear = false;
						} else {
							$clear = true;
							return _expression($expressionDiv);	
						}
					}
				});
			}
			
			$expressionDiv = document.getElementById(args.id);
			$inputId = args.inputId;
			$imageSrc = args.imagePath || "";
			$removeChildrenOf( $expressionDiv );
			$expressionDiv.appendChild( addImageWiz() );
		}
	);
 })()