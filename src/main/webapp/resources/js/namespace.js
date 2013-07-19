(function() {
	if (window.namespace) {
		return false;
	} else {
		window.namespace = $namespace;
		window.loadScript = $loadDependency;
		window.addEventListener("load", $afterLoadPage, false);
		$namespace.whenReady = $execute;
	}

	var $_library = {};
	var $_hasLoaded = false;

	function $afterLoadPage($ev) {
		$_hasLoaded = true;
	}

	function $execute(func) {
		if (typeof func === "function") {
			if ($_hasLoaded) {
				func();
			} else {
				window.addEventListener("load", func, false);
			}
		}
	}

	function $createNamespace(namespace, object) {
		var $path, $first, $_namespace, $item;
		if (typeof namespace === "string") {
			$path = namespace.split(".");
			$_namespace = $_library
			for ( var i = 0; i < $path.length; i++) {
				$item = $path[i];
				if (i == $path.length - 1) {
					if ($_namespace[$item]) {
						$_namespace = $_namespace[$item];
						if (!Object.isFrozen($_namespace)) {
							for ( var attr in object) {
								if (!$_namespace[attr]) {
									$_namespace[attr] = object[attr];
								}
							}
						}
					} else {
						$_namespace = $_namespace[$item] = object;
					}
				} else {
					$_namespace = $_namespace[$item] = $_namespace[$item] || {};
				}
				if (i == 0) {
					$namespace[$item] = $_namespace;
				}
			}
		} else {
			$_namespace = object;
		}
		return $_namespace;
	}

	function $loadDependency(source) {
		$execute(function() {
			var script = document.createElement("script");
			script.setAttribute("src", source);
			script.setAttribute("type", "text/javascript");
			document.head.appendChild(script);
		});
	}

	function emptyFunc() {
	}

	function $namespace(namespace, object) {
		var $obj;
		var $params = [];
		var isObjectFunc;
		if (typeof namespace === "string") {
			isObjectFunc = typeof object === "function";
			if (isObjectFunc && object.name === "") {
				$obj = $createNamespace(namespace, {});
			} else {
				$obj = $createNamespace(namespace, object);	
			}
			if ($obj !== object) {
				if (isObjectFunc) {
					$execute(function() {
						object($obj);
					});
				} else if (typeof $obj === "function") {
					$execute(function() {
						$obj(object);
					});
				}
			}
		} else if (typeof namespace === "object"
				&& namespace.constructor === [].constructor) {
			for ( var i = 0; i < namespace.length; i++) {
				$params.push($createNamespace(namespace[i], {}));
			}
			for ( var i = 0; i < object.length - namespace.length; i++) {
				$params.push(emptyFunc);
			}
			$execute(function() {
				object.apply(object, $params);
			});
		}
	}
})()