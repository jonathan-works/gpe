(function webpackUniversalModuleDefinition(root, factory) {
	if(typeof exports === 'object' && typeof module === 'object')
		module.exports = factory();
	else if(typeof define === 'function' && define.amd)
		define([], factory);
	else if(typeof exports === 'object')
		exports["react-components"] = factory();
	else
		root["react-components"] = factory();
})(this, function() {
return /******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};

/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {

/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId])
/******/ 			return installedModules[moduleId].exports;

/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			exports: {},
/******/ 			id: moduleId,
/******/ 			loaded: false
/******/ 		};

/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);

/******/ 		// Flag the module as loaded
/******/ 		module.loaded = true;

/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}


/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;

/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;

/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";

/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(0);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	module.exports = {
	    NavigationMenu: __webpack_require__(1),
	    TreeCategorias: __webpack_require__(8),
	    Spinner: __webpack_require__(35)
	};

/***/ },
/* 1 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var UnorderedMenu = __webpack_require__(2);
	var MenuConstants = __webpack_require__(6);

	var NavMenu = React.createClass({
	  displayName: 'NavMenu',

	  getDefaultProps: function getDefaultProps() {
	    return {
	      "level": 0,
	      "showChildren": false,
	      "labelFilter": ""
	    };
	  },
	  render: function render() {
	    return React.DOM.nav({
	      className: MenuConstants.CSS_CLASSES.NAV_MENU
	    }, React.createElement(UnorderedMenu, Object.assign({}, this.props)));
	  }
	});

	var MenuContent = React.createClass({
	  displayName: 'MenuContent',

	  renderContent: function renderContent(container) {
	    var content = document.getElementById(this.props.selector);
	    if (content) {
	      container.appendChild(content);
	    } else {
	      var nodeList = document.querySelectorAll(this.props.selector);
	      for (var i = 0, l = nodeList.length; i < l; i++) {
	        container.appendChild(nodeList.item(i));
	      }
	    }
	  },
	  render: function render() {
	    return React.DOM.main({
	      ref: this.renderContent,
	      "data-title": this.props.title,
	      className: MenuConstants.CSS_CLASSES.MENU_CONTENT
	    });
	  }
	});

	var Header = React.createClass({
	  displayName: 'Header',
	  render: function render() {
	    return React.DOM.div({
	      className: MenuConstants.CSS_CLASSES.HEADER
	    }, React.createElement(NavMenu, this.props));
	  }
	});

	var Drawer = React.createClass({
	  displayName: 'Drawer',
	  getInitialState: function getInitialState() {
	    return {};
	  },
	  updateRefs: function updateRefs(ref) {
	    this.setState({ concreteObject: ref });
	  },
	  updateHeight: function updateHeight() {},
	  render: function render() {
	    return React.DOM.div({
	      className: MenuConstants.CSS_CLASSES.DRAWER,
	      ref: this.updateRefs
	    }, React.createElement(NavMenu, this.props));
	  }
	});

	var NavigationMenu = React.createClass({
	  displayName: 'NavigationMenu',

	  render: function render() {
	    var contentProps = Object.assign({}, this.props.content);
	    contentProps.title = document.head.querySelector("title").textContent;
	    return React.DOM.div({
	      className: MenuConstants.CSS_CLASSES.FIXED_HEAD_DRAWER
	    }, React.createElement(MenuContent, contentProps), React.createElement(Header, this.props.topMenu), React.createElement(Drawer, this.props.navigationMenu));
	  }
	});

	module.exports = NavigationMenu;

/***/ },
/* 2 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var MenuItem = __webpack_require__(3);
	var MenuFilter = __webpack_require__(7);
	var MenuConstants = __webpack_require__(6);

	var hasChildWithContent = function hasChildWithContent(menuItem, content) {
	  var result = false;
	  var childCount = (menuItem.items || []).length;
	  if (childCount === 0 && new RegExp((content || '').toLowerCase()).test(menuItem.label.toLowerCase())) {
	    return true;
	  }
	  for (var i = 0; i < childCount && !result; i++) {
	    result = hasChildWithContent(menuItem.items[i], content);
	  }
	  return result;
	};

	var BaseMenuMixin = {
	  handleSelect: function handleSelect(label) {
	    this.unorderedMenu.addEventListener('mouseleave', this.handleMouseLeave);
	    this.unorderedMenu.addEventListener('mouseenter', this.handleMouseEnter);
	    var state = Object.assign({}, this.state);
	    if (state.selected === label) {
	      state.selected = '';
	    } else {
	      state.selected = label || '';
	    }
	    this.setState(state);
	  },
	  handleMouseLeave: function handleMouseLeave(event) {
	    var _this = this;

	    this.state.hideTimer = setTimeout(function () {
	      _this.handleSelect();
	      clearTimeout(_this.state.hideTimer);
	    }, 1000);
	  },
	  handleMouseEnter: function handleMouseEnter(event) {
	    clearTimeout(this.state.hideTimer);
	  },
	  getFilteredItems: function getFilteredItems(labelFilter) {
	    var _this2 = this;

	    var level = this.props.level || 0;
	    return (this.props.items || []).filter(function (itm) {
	      return hasChildWithContent(itm, labelFilter);
	    }).map(function (itm) {
	      var resultItem = Object.assign({}, itm);
	      resultItem.key = resultItem.label;
	      resultItem.level = level;
	      resultItem.labelFilter = labelFilter || '';
	      resultItem.onSelect = _this2.handleSelect;
	      resultItem.selected = _this2.state.selected === resultItem.label || resultItem.labelFilter.trim() !== '';
	      if ((itm.items || []).length > 0) {
	        resultItem.submenu = React.createElement(UnorderedMenu, {
	          "items": resultItem.items,
	          "level": level + 1,
	          "showFilter": resultItem.showFilter,
	          "labelFilter": labelFilter,
	          'searchPlaceholder': _this2.props.searchPlaceholder
	        });
	      }
	      return React.createElement(MenuItem, resultItem);
	    });
	  }
	};

	var FilterMenu = React.createClass(Object.assign(BaseMenuMixin, {
	  getInitialState: function getInitialState() {
	    return { 'selected': '', 'labelFilter': '', 'filterId': Date.now.toString(36) };
	  },
	  render: function render() {
	    var _this3 = this;

	    return React.DOM.ul({
	      className: MenuConstants.CSS_CLASSES.MENU,
	      style: this.props.style,
	      ref: function ref(_ref) {
	        return _this3.unorderedMenu = _ref;
	      }
	    }, React.createElement(MenuFilter, {
	      key: this.state.filterId,
	      labelFilterPlaceholder: this.props.searchPlaceholder,
	      labelFilter: this.state.labelFilter,
	      onFilter: this.handleLabelFilter
	    }), this.getFilteredItems(this.state.labelFilter));
	  },
	  handleLabelFilter: function handleLabelFilter(labelFilter) {
	    var state = Object.assign({}, this.state);
	    state.labelFilter = labelFilter;
	    this.setState(state);
	  }
	}));

	var NoFilterMenu = React.createClass(Object.assign(BaseMenuMixin, {
	  render: function render() {
	    var _this4 = this;

	    return React.DOM.ul({
	      className: MenuConstants.CSS_CLASSES.MENU,
	      style: this.props.style,
	      ref: function ref(_ref2) {
	        return _this4.unorderedMenu = _ref2;
	      }
	    }, this.getFilteredItems(this.props.labelFilter));
	  }
	}));

	var UnorderedMenu = React.createClass(Object.assign(BaseMenuMixin, {
	  render: function render() {
	    var menuProps = Object.assign({}, this.props);
	    menuProps.style = {
	      // 'maxHeight':(innerHeight - 13*parseInt(0+/\d*/.exec(getComputedStyle(document.body)['font-size'])[0]))
	    };
	    if (menuProps.showFilter || menuProps.level === 1) {
	      return React.createElement(FilterMenu, Object.assign({}, menuProps));
	    }
	    menuProps.onMouseLeave = this.handleMouseLeave;
	    menuProps.onMouseEnter = this.handleMouseEnter;
	    return React.createElement(NoFilterMenu, Object.assign({}, menuProps));
	  }
	}));
	module.exports = UnorderedMenu;

/***/ },
/* 3 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var MenuLink = __webpack_require__(4);
	var MenuItem = React.createClass({
	  displayName: 'MenuItem',


	  handleClick: function handleClick(event) {
	    this.props.onSelect(this.props.label);
	  },

	  render: function render() {
	    var classes = ['ifx-menu-itm'];
	    if (this.props.selected) {
	      classes.push('ifx-menu-itm-sel');
	    }

	    var props = Object.assign({}, this.props);

	    var submenu = void 0;

	    if ((props.items || []).length > 0) {
	      classes.push('ifx-menu-itm-has-children');
	      props.onClick = this.handleClick;
	      submenu = this.props.submenu;
	    }

	    return React.DOM.li({
	      className: classes.join(' ')
	    }, React.createElement(MenuLink, props), submenu);
	  }
	});
	module.exports = MenuItem;

/***/ },
/* 4 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var MenuText = __webpack_require__(5);
	var MenuLink = React.createClass({
	  displayName: 'MenuLink',

	  getInitialState: function getInitialState() {
	    return {
	      selected: false
	    };
	  },
	  handleClick: function handleClick(event) {
	    this.setState({ selected: true });
	    (this.props.onClick || function () {}).apply(this, [event, this.props.url || this.props.value]);
	  },
	  render: function render() {
	    var linkClasses = ['ifx-menu-itm-lnk'];
	    if (this.state.selected) {
	      linkClasses.push('ifx-menu-itm-lnk-sel');
	    }
	    return React.DOM.a({
	      className: linkClasses.join(' '),
	      href: this.props.url,
	      title: this.props.label,
	      onClick: this.handleClick
	    }, React.createElement(MenuText, this.props));
	  }
	});
	module.exports = MenuLink;

/***/ },
/* 5 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var MenuConstants = __webpack_require__(6);
	var LeftIconText = React.createClass({
	    displayName: 'LeftIconText',

	    render: function render() {
	        var labelProperties = {
	            className: MenuConstants.CSS_CLASSES.ITEM_LABEL
	        };
	        if (this.props.icon) {
	            return React.DOM.span(labelProperties, React.DOM.img({
	                className: MenuConstants.CSS_CLASSES.ITEM_LABEL_ICON,
	                title: this.props.label,
	                src: this.props.icon
	            }), this.props.hideLabel ? undefined : React.DOM.span({ 'className': MenuConstants.CSS_CLASSES.ITEM_LABEL_TEXT }, this.props.label));
	        } else {
	            return React.DOM.span(labelProperties, this.props.hideLabel ? undefined : React.DOM.span({ 'className': MenuConstants.CSS_CLASSES.ITEM_LABEL_TEXT }, this.props.label));
	        }
	    }
	});
	var RightIconText = React.createClass({
	    displayName: 'RightIconText',

	    render: function render() {
	        var labelProperties = {
	            className: MenuConstants.CSS_CLASSES.ITEM_LABEL
	        };
	        if (this.props.icon) {
	            return React.DOM.span(labelProperties, this.props.hideLabel ? undefined : React.DOM.span({ 'className': MenuConstants.CSS_CLASSES.ITEM_LABEL_TEXT }, this.props.label), React.DOM.img({
	                className: MenuConstants.CSS_CLASSES.ITEM_LABEL_ICON,
	                title: this.props.label,
	                src: this.props.icon
	            }));
	        } else {
	            return React.DOM.span(labelProperties, this.props.hideLabel ? undefined : React.DOM.span({ 'className': MenuConstants.CSS_CLASSES.ITEM_LABEL_TEXT }, this.props.label));
	        }
	    }
	});

	var MenuText = React.createClass({
	    displayName: 'MenuText',

	    render: function render() {
	        if (/left/.test((this.props['icon-align'] || 'left').toLowerCase())) {
	            return React.createElement(LeftIconText, this.props);
	        } else {
	            return React.createElement(RightIconText, this.props);
	        }
	    }
	});
	module.exports = MenuText;

/***/ },
/* 6 */
/***/ function(module, exports) {

	'use strict';

	var menu = 'ifx-menu';

	module.exports = {
	    CSS_CLASSES: {
	        ITEM_LABEL: menu + '-itm-lbl',
	        ITEM_LABEL_ICON: menu + '-itm-lbl-icon',
	        ITEM_LABEL_TEXT: menu + '-itm-lbl-text',
	        MENU: menu + '',
	        NAV_MENU: menu + '-nav',
	        PAGE_TITLE: 'ifx-page-title',
	        MENU_CONTENT: 'ifx-menu-content',
	        DRAWER: 'ifx-navigation-menu',
	        HEADER: 'ifx-top-menu',
	        FIXED_HEAD_DRAWER: 'ifx-menu-container'
	    }
	};

/***/ },
/* 7 */
/***/ function(module, exports) {

	'use strict';

	var MenuFilter = React.createClass({
	    displayName: 'MenuFilter',

	    handleChange: function handleChange(event) {
	        this.props.onFilter(event.target.value);
	    },
	    render: function render() {
	        return React.DOM.input({
	            value: this.props.labelFilter,
	            placeholder: this.props.labelFilterPlaceholder || 'Your query here...',
	            className: 'ifx-menu-filter',
	            onChange: this.handleChange,
	            autoFocus: true
	        });
	    }
	});
	module.exports = MenuFilter;

/***/ },
/* 8 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var Constants = __webpack_require__(9);
	var TreeCategoriasStore = __webpack_require__(10);
	var TreeCategoriasActions = __webpack_require__(26);
	var ConfigFacade = __webpack_require__(34);
	var Spinner = __webpack_require__(35);

	var Groups = __webpack_require__(36);

	var TreeCategorias = React.createClass({
	  'displayName': 'TreeCategorias',
	  propTypes: {
	    orientation: React.PropTypes.oneOf(['horizontal', 'vertical']),
	    groupToolBar: React.PropTypes.arrayOf(React.PropTypes.shape({
	      icon: React.PropTypes.string,
	      title: React.PropTypes.string.isRequired,
	      onSelect: React.PropTypes.func.isRequired
	    })),
	    itemToolBar: React.PropTypes.arrayOf(React.PropTypes.shape({
	      icon: React.PropTypes.string,
	      title: React.PropTypes.string.isRequired,
	      onSelect: React.PropTypes.func.isRequired
	    })),
	    config: React.PropTypes.object.isRequired
	  },
	  getInitialState: function getInitialState() {
	    return TreeCategoriasStore.getState();
	  },
	  componentDidMount: function componentDidMount() {
	    ConfigFacade.config = this.props.config;
	    TreeCategoriasStore.listen(this.onChange);
	    TreeCategoriasActions.fetchCategorias();
	  },
	  componentWillUnmount: function componentWillUnmount() {
	    TreeCategoriasStore.unlisten(this.onChange);
	  },
	  onChange: function onChange(state) {
	    this.setState(state);
	  },
	  handleRenderRef: function handleRenderRef(domReference) {
	    var _this = this;

	    domReference.parentNode.refresh = function () {
	      _this.setState(Object.assign({}, _this.state, { loading: true }));
	      TreeCategoriasActions.fetchCategorias();
	    };
	  },
	  render: function render() {
	    var props = Object.assign({}, this.state);
	    props.groupToolBar = this.props.groupToolBar;
	    props.itemToolBar = this.props.itemToolBar;
	    var treeClasses = [Constants.CSS.GROUPED_TREE];
	    if ((this.props.orientation || 'vertical') === 'horizontal') {
	      treeClasses.push(Constants.CSS.GROUPED_TREE_HORIZONTAL);
	    } else {
	      treeClasses.push(Constants.CSS.GROUPED_TREE_VERTICAL);
	    }
	    var children = props.groupItems.map(function (groupItem) {
	      var obj = Object.assign({}, groupItem);
	      obj.key = [props.codigo || '', obj.group.codigo].join(':');
	      obj.folded = props.folded;
	      obj.path = props.path || Constants.PATH_SEPARATOR;
	      obj.groupToolBar = props.groupToolBar;
	      obj.itemToolBar = props.itemToolBar;
	      return React.createElement(Groups, obj);
	    });
	    var isLoading = this.state.loading;
	    if (isLoading) {
	      treeClasses.push(Constants.CSS.GROUPED_TREE_IS_LOADING);
	    } else if (children === null || children.length === 0) {
	      children = this.props.config.emptyMessage || '';
	    }
	    return React.createElement(
	      'section',
	      { className: treeClasses.join(' '), ref: this.handleRenderRef },
	      React.createElement(Spinner, { active: isLoading }),
	      children
	    );
	  }
	});
	module.exports = TreeCategorias;

/***/ },
/* 9 */
/***/ function(module, exports) {

	'use strict';

	var TreeConstants = {
	  PATH_SEPARATOR: '/',
	  CSS: {
	    ITEM: 'grpd-tree-itm',
	    ITEM_HAS_CHILDREN: 'grpd-tree-itm-has-children',
	    ITEM_CONTENT: 'grpd-tree-itm-cont',
	    ITEM_LABEL: 'grpd-tree-itm-lbl',
	    ITEM_SELECTED: 'grpd-tree-itm-sel',
	    LIST: 'grpd-tree-lst',
	    LIST_CONTENT: 'grpd-tree-lst-cont',
	    LIST_FOLDED: 'grpd-tree-lst-folded',
	    GROUP: 'grpd-tree-grp',
	    GROUP_ROTATED: 'grpd-tree-grp-rot',
	    GROUP_LABEL: 'grpd-tree-grp-lbl',
	    GROUP_CONTENT: 'grpd-tree-grp-cont',
	    GROUPED_TREE: 'grpd-tree',
	    GROUPED_TREE_IS_LOADING: 'grpd-tree-is-loading',
	    GROUPED_TREE_HORIZONTAL: 'grpd-tree-h',
	    GROUPED_TREE_VERTICAL: 'grpd-tree-v'
	  }
	};
	module.exports = TreeConstants;

/***/ },
/* 10 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var alt = __webpack_require__(11);
	var TreeCategoriasActions = __webpack_require__(26);

	function GroupItemProxy(categoria, items, stateHolder) {
	  return {
	    get group() {
	      return stateHolder._categoria.get(categoria.codigo);
	    },
	    get items() {
	      return items.map(function (item) {
	        return stateHolder._item.get(item.codigo);
	      });
	    }
	  };
	}

	function TreeCategoriasStateHolder() {}
	TreeCategoriasStateHolder.prototype = {
	  registerCategoria: function registerCategoria(categoria) {
	    var _this = this;

	    var _categoria = {
	      codigo: categoria.codigo,
	      descricao: categoria.descricao
	    };
	    (categoria.itens || []).forEach(function (item) {
	      return _this.registerItem(item);
	    });
	    this._categoria.set(_categoria.codigo, _categoria);
	    return _categoria;
	  },
	  registerItem: function registerItem(item) {
	    var _item = {
	      codigo: item.codigo,
	      descricao: item.descricao
	    };
	    this._item.set(_item.codigo, _item);
	    TreeCategoriasActions.fetchChildrenForItem(item);
	    return _item;
	  },
	  registerGroups: function registerGroups(categorias, itemPai) {
	    var _this2 = this;

	    var groups = categorias.map(function (categoria) {
	      _this2.registerCategoria(categoria);
	      return new GroupItemProxy(categoria, categoria.itens, _this2);
	    });
	    var codigoPai = (itemPai || {}).codigo || '';
	    if (codigoPai !== '') {
	      var pai = this._item.get(codigoPai);
	      pai.groupItems = groups;
	      this._item.set(codigoPai, pai);
	    }
	    this._group.set(codigoPai, groups);
	    return groups;
	  },
	  registerInitialState: function registerInitialState(groupItems) {
	    this._categoria = new Map();
	    this._item = new Map();
	    this._group = new Map();
	    this.registerGroups(groupItems);
	    return this.getState();
	  },
	  getState: function getState(state) {
	    return {
	      groupItems: this._group.get('')
	    };
	  }
	};

	var TreeCategoriasStoreBase = {
	  displayName: 'TreeCategoriasStore',
	  _stateHolder: new TreeCategoriasStateHolder(),
	  _promises: [],
	  _timeout: undefined,
	  bindListeners: {
	    handleFetchCategoriasSuccess: TreeCategoriasActions.FETCH_CATEGORIAS_SUCCESS,
	    handleFetchChildrenForItemSuccess: TreeCategoriasActions.FETCH_CHILDREN_FOR_ITEM_SUCCESS,
	    handleFetchCategoriasFail: TreeCategoriasActions.FETCH_CATEGORIAS_FAIL,
	    handleFetchChildrenForItemFail: TreeCategoriasActions.FETCH_CHILDREN_FOR_ITEM_FAIL
	  },
	  state: {
	    groupItems: [],
	    loading: true
	  },
	  _updateState: function _updateState() {
	    var _this3 = this;

	    clearTimeout(this._timeout);
	    this._timeout = setTimeout(function () {
	      return _this3.setState(Object.assign({}, _this3._stateHolder.getState(), { loading: false }));
	    }, 500);
	  },
	  handleFetchChildrenForItemSuccess: function handleFetchChildrenForItemSuccess(args) {
	    var categorias = args[0];
	    var itemPai = args[1];
	    this._stateHolder.registerGroups(categorias, itemPai);
	    this._updateState();
	    this.preventDefault();
	  },
	  handleFetchChildrenForItemFail: function handleFetchChildrenForItemFail(fail) {
	    console.log(fail);
	  },
	  handleFetchCategoriasSuccess: function handleFetchCategoriasSuccess(categorias) {
	    var state = this._stateHolder.registerInitialState(categorias);
	    state.groupItems.forEach(function (group) {
	      return group.items.forEach(function (item) {
	        return TreeCategoriasActions.fetchChildrenForItem(item);
	      });
	    });
	    this._updateState();
	    this.preventDefault();
	  },
	  handleFetchCategoriasFail: function handleFetchCategoriasFail(fail) {
	    console.log(fail);
	  },
	  publicMethods: {}
	};

	var TreeCategoriasStore = alt.createStore(TreeCategoriasStoreBase, TreeCategoriasStoreBase.displayName);
	module.exports = TreeCategoriasStore;

/***/ },
/* 11 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var Alt = __webpack_require__(12);
	module.exports = new Alt();

/***/ },
/* 12 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, "__esModule", {
	  value: true
	});

	var _flux = __webpack_require__(13);

	var _StateFunctions = __webpack_require__(17);

	var StateFunctions = _interopRequireWildcard(_StateFunctions);

	var _functions = __webpack_require__(18);

	var fn = _interopRequireWildcard(_functions);

	var _store = __webpack_require__(19);

	var store = _interopRequireWildcard(_store);

	var _AltUtils = __webpack_require__(20);

	var utils = _interopRequireWildcard(_AltUtils);

	var _actions = __webpack_require__(24);

	var _actions2 = _interopRequireDefault(_actions);

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

	function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) newObj[key] = obj[key]; } } newObj['default'] = obj; return newObj; } }

	function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

	function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

	function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } } /* global window */


	var Alt = function () {
	  function Alt() {
	    var config = arguments.length <= 0 || arguments[0] === undefined ? {} : arguments[0];

	    _classCallCheck(this, Alt);

	    this.config = config;
	    this.serialize = config.serialize || JSON.stringify;
	    this.deserialize = config.deserialize || JSON.parse;
	    this.dispatcher = config.dispatcher || new _flux.Dispatcher();
	    this.batchingFunction = config.batchingFunction || function (callback) {
	      return callback();
	    };
	    this.actions = { global: {} };
	    this.stores = {};
	    this.storeTransforms = config.storeTransforms || [];
	    this.trapAsync = false;
	    this._actionsRegistry = {};
	    this._initSnapshot = {};
	    this._lastSnapshot = {};
	  }

	  Alt.prototype.dispatch = function () {
	    function dispatch(action, data, details) {
	      var _this = this;

	      this.batchingFunction(function () {
	        var id = Math.random().toString(18).substr(2, 16);

	        // support straight dispatching of FSA-style actions
	        if (action.hasOwnProperty('type') && action.hasOwnProperty('payload')) {
	          var fsaDetails = {
	            id: action.type,
	            namespace: action.type,
	            name: action.type
	          };
	          return _this.dispatcher.dispatch(utils.fsa(id, action.type, action.payload, fsaDetails));
	        }

	        if (action.id && action.dispatch) {
	          return utils.dispatch(id, action, data, _this);
	        }

	        return _this.dispatcher.dispatch(utils.fsa(id, action, data, details));
	      });
	    }

	    return dispatch;
	  }();

	  Alt.prototype.createUnsavedStore = function () {
	    function createUnsavedStore(StoreModel) {
	      var key = StoreModel.displayName || '';
	      store.createStoreConfig(this.config, StoreModel);
	      var Store = store.transformStore(this.storeTransforms, StoreModel);

	      for (var _len = arguments.length, args = Array(_len > 1 ? _len - 1 : 0), _key = 1; _key < _len; _key++) {
	        args[_key - 1] = arguments[_key];
	      }

	      return fn.isFunction(Store) ? store.createStoreFromClass.apply(store, [this, Store, key].concat(args)) : store.createStoreFromObject(this, Store, key);
	    }

	    return createUnsavedStore;
	  }();

	  Alt.prototype.createStore = function () {
	    function createStore(StoreModel, iden) {
	      var key = iden || StoreModel.displayName || StoreModel.name || '';
	      store.createStoreConfig(this.config, StoreModel);
	      var Store = store.transformStore(this.storeTransforms, StoreModel);

	      /* istanbul ignore next */
	      if (false) delete this.stores[key];

	      if (this.stores[key] || !key) {
	        if (this.stores[key]) {
	          utils.warn('A store named ' + String(key) + ' already exists, double check your store ' + 'names or pass in your own custom identifier for each store');
	        } else {
	          utils.warn('Store name was not specified');
	        }

	        key = utils.uid(this.stores, key);
	      }

	      for (var _len2 = arguments.length, args = Array(_len2 > 2 ? _len2 - 2 : 0), _key2 = 2; _key2 < _len2; _key2++) {
	        args[_key2 - 2] = arguments[_key2];
	      }

	      var storeInstance = fn.isFunction(Store) ? store.createStoreFromClass.apply(store, [this, Store, key].concat(args)) : store.createStoreFromObject(this, Store, key);

	      this.stores[key] = storeInstance;
	      StateFunctions.saveInitialSnapshot(this, key);

	      return storeInstance;
	    }

	    return createStore;
	  }();

	  Alt.prototype.generateActions = function () {
	    function generateActions() {
	      var actions = { name: 'global' };

	      for (var _len3 = arguments.length, actionNames = Array(_len3), _key3 = 0; _key3 < _len3; _key3++) {
	        actionNames[_key3] = arguments[_key3];
	      }

	      return this.createActions(actionNames.reduce(function (obj, action) {
	        obj[action] = utils.dispatchIdentity;
	        return obj;
	      }, actions));
	    }

	    return generateActions;
	  }();

	  Alt.prototype.createAction = function () {
	    function createAction(name, implementation, obj) {
	      return (0, _actions2.default)(this, 'global', name, implementation, obj);
	    }

	    return createAction;
	  }();

	  Alt.prototype.createActions = function () {
	    function createActions(ActionsClass) {
	      var _this3 = this;

	      var exportObj = arguments.length <= 1 || arguments[1] === undefined ? {} : arguments[1];

	      var actions = {};
	      var key = utils.uid(this._actionsRegistry, ActionsClass.displayName || ActionsClass.name || 'Unknown');

	      if (fn.isFunction(ActionsClass)) {
	        fn.assign(actions, utils.getPrototypeChain(ActionsClass));

	        var ActionsGenerator = function (_ActionsClass) {
	          _inherits(ActionsGenerator, _ActionsClass);

	          function ActionsGenerator() {
	            _classCallCheck(this, ActionsGenerator);

	            for (var _len5 = arguments.length, args = Array(_len5), _key5 = 0; _key5 < _len5; _key5++) {
	              args[_key5] = arguments[_key5];
	            }

	            return _possibleConstructorReturn(this, _ActionsClass.call.apply(_ActionsClass, [this].concat(args)));
	          }

	          ActionsGenerator.prototype.generateActions = function () {
	            function generateActions() {
	              for (var _len6 = arguments.length, actionNames = Array(_len6), _key6 = 0; _key6 < _len6; _key6++) {
	                actionNames[_key6] = arguments[_key6];
	              }

	              actionNames.forEach(function (actionName) {
	                actions[actionName] = utils.dispatchIdentity;
	              });
	            }

	            return generateActions;
	          }();

	          return ActionsGenerator;
	        }(ActionsClass);

	        for (var _len4 = arguments.length, argsForConstructor = Array(_len4 > 2 ? _len4 - 2 : 0), _key4 = 2; _key4 < _len4; _key4++) {
	          argsForConstructor[_key4 - 2] = arguments[_key4];
	        }

	        fn.assign(actions, new (Function.prototype.bind.apply(ActionsGenerator, [null].concat(argsForConstructor)))());
	      } else {
	        fn.assign(actions, ActionsClass);
	      }

	      this.actions[key] = this.actions[key] || {};

	      fn.eachObject(function (actionName, action) {
	        if (!fn.isFunction(action)) {
	          exportObj[actionName] = action;
	          return;
	        }

	        // create the action
	        exportObj[actionName] = (0, _actions2.default)(_this3, key, actionName, action, exportObj);

	        // generate a constant
	        var constant = utils.formatAsConstant(actionName);
	        exportObj[constant] = exportObj[actionName].id;
	      }, [actions]);

	      return exportObj;
	    }

	    return createActions;
	  }();

	  Alt.prototype.takeSnapshot = function () {
	    function takeSnapshot() {
	      for (var _len7 = arguments.length, storeNames = Array(_len7), _key7 = 0; _key7 < _len7; _key7++) {
	        storeNames[_key7] = arguments[_key7];
	      }

	      var state = StateFunctions.snapshot(this, storeNames);
	      fn.assign(this._lastSnapshot, state);
	      return this.serialize(state);
	    }

	    return takeSnapshot;
	  }();

	  Alt.prototype.rollback = function () {
	    function rollback() {
	      StateFunctions.setAppState(this, this.serialize(this._lastSnapshot), function (storeInst) {
	        storeInst.lifecycle('rollback');
	        storeInst.emitChange();
	      });
	    }

	    return rollback;
	  }();

	  Alt.prototype.recycle = function () {
	    function recycle() {
	      for (var _len8 = arguments.length, storeNames = Array(_len8), _key8 = 0; _key8 < _len8; _key8++) {
	        storeNames[_key8] = arguments[_key8];
	      }

	      var initialSnapshot = storeNames.length ? StateFunctions.filterSnapshots(this, this._initSnapshot, storeNames) : this._initSnapshot;

	      StateFunctions.setAppState(this, this.serialize(initialSnapshot), function (storeInst) {
	        storeInst.lifecycle('init');
	        storeInst.emitChange();
	      });
	    }

	    return recycle;
	  }();

	  Alt.prototype.flush = function () {
	    function flush() {
	      var state = this.serialize(StateFunctions.snapshot(this));
	      this.recycle();
	      return state;
	    }

	    return flush;
	  }();

	  Alt.prototype.bootstrap = function () {
	    function bootstrap(data) {
	      StateFunctions.setAppState(this, data, function (storeInst, state) {
	        storeInst.lifecycle('bootstrap', state);
	        storeInst.emitChange();
	      });
	    }

	    return bootstrap;
	  }();

	  Alt.prototype.prepare = function () {
	    function prepare(storeInst, payload) {
	      var data = {};
	      if (!storeInst.displayName) {
	        throw new ReferenceError('Store provided does not have a name');
	      }
	      data[storeInst.displayName] = payload;
	      return this.serialize(data);
	    }

	    return prepare;
	  }();

	  // Instance type methods for injecting alt into your application as context

	  Alt.prototype.addActions = function () {
	    function addActions(name, ActionsClass) {
	      for (var _len9 = arguments.length, args = Array(_len9 > 2 ? _len9 - 2 : 0), _key9 = 2; _key9 < _len9; _key9++) {
	        args[_key9 - 2] = arguments[_key9];
	      }

	      this.actions[name] = Array.isArray(ActionsClass) ? this.generateActions.apply(this, ActionsClass) : this.createActions.apply(this, [ActionsClass].concat(args));
	    }

	    return addActions;
	  }();

	  Alt.prototype.addStore = function () {
	    function addStore(name, StoreModel) {
	      for (var _len10 = arguments.length, args = Array(_len10 > 2 ? _len10 - 2 : 0), _key10 = 2; _key10 < _len10; _key10++) {
	        args[_key10 - 2] = arguments[_key10];
	      }

	      this.createStore.apply(this, [StoreModel, name].concat(args));
	    }

	    return addStore;
	  }();

	  Alt.prototype.getActions = function () {
	    function getActions(name) {
	      return this.actions[name];
	    }

	    return getActions;
	  }();

	  Alt.prototype.getStore = function () {
	    function getStore(name) {
	      return this.stores[name];
	    }

	    return getStore;
	  }();

	  Alt.debug = function () {
	    function debug(name, alt, win) {
	      var key = 'alt.js.org';
	      var context = win;
	      if (!context && typeof window !== 'undefined') {
	        context = window;
	      }
	      if (typeof context !== 'undefined') {
	        context[key] = context[key] || [];
	        context[key].push({ name: name, alt: alt });
	      }
	      return alt;
	    }

	    return debug;
	  }();

	  return Alt;
	}();

	exports.default = Alt;
	module.exports = exports['default'];

/***/ },
/* 13 */
/***/ function(module, exports, __webpack_require__) {

	/**
	 * Copyright (c) 2014-2015, Facebook, Inc.
	 * All rights reserved.
	 *
	 * This source code is licensed under the BSD-style license found in the
	 * LICENSE file in the root directory of this source tree. An additional grant
	 * of patent rights can be found in the PATENTS file in the same directory.
	 */

	module.exports.Dispatcher = __webpack_require__(14);


/***/ },
/* 14 */
/***/ function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(process) {/**
	 * Copyright (c) 2014-2015, Facebook, Inc.
	 * All rights reserved.
	 *
	 * This source code is licensed under the BSD-style license found in the
	 * LICENSE file in the root directory of this source tree. An additional grant
	 * of patent rights can be found in the PATENTS file in the same directory.
	 *
	 * @providesModule Dispatcher
	 * 
	 * @preventMunge
	 */

	'use strict';

	exports.__esModule = true;

	function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError('Cannot call a class as a function'); } }

	var invariant = __webpack_require__(16);

	var _prefix = 'ID_';

	/**
	 * Dispatcher is used to broadcast payloads to registered callbacks. This is
	 * different from generic pub-sub systems in two ways:
	 *
	 *   1) Callbacks are not subscribed to particular events. Every payload is
	 *      dispatched to every registered callback.
	 *   2) Callbacks can be deferred in whole or part until other callbacks have
	 *      been executed.
	 *
	 * For example, consider this hypothetical flight destination form, which
	 * selects a default city when a country is selected:
	 *
	 *   var flightDispatcher = new Dispatcher();
	 *
	 *   // Keeps track of which country is selected
	 *   var CountryStore = {country: null};
	 *
	 *   // Keeps track of which city is selected
	 *   var CityStore = {city: null};
	 *
	 *   // Keeps track of the base flight price of the selected city
	 *   var FlightPriceStore = {price: null}
	 *
	 * When a user changes the selected city, we dispatch the payload:
	 *
	 *   flightDispatcher.dispatch({
	 *     actionType: 'city-update',
	 *     selectedCity: 'paris'
	 *   });
	 *
	 * This payload is digested by `CityStore`:
	 *
	 *   flightDispatcher.register(function(payload) {
	 *     if (payload.actionType === 'city-update') {
	 *       CityStore.city = payload.selectedCity;
	 *     }
	 *   });
	 *
	 * When the user selects a country, we dispatch the payload:
	 *
	 *   flightDispatcher.dispatch({
	 *     actionType: 'country-update',
	 *     selectedCountry: 'australia'
	 *   });
	 *
	 * This payload is digested by both stores:
	 *
	 *   CountryStore.dispatchToken = flightDispatcher.register(function(payload) {
	 *     if (payload.actionType === 'country-update') {
	 *       CountryStore.country = payload.selectedCountry;
	 *     }
	 *   });
	 *
	 * When the callback to update `CountryStore` is registered, we save a reference
	 * to the returned token. Using this token with `waitFor()`, we can guarantee
	 * that `CountryStore` is updated before the callback that updates `CityStore`
	 * needs to query its data.
	 *
	 *   CityStore.dispatchToken = flightDispatcher.register(function(payload) {
	 *     if (payload.actionType === 'country-update') {
	 *       // `CountryStore.country` may not be updated.
	 *       flightDispatcher.waitFor([CountryStore.dispatchToken]);
	 *       // `CountryStore.country` is now guaranteed to be updated.
	 *
	 *       // Select the default city for the new country
	 *       CityStore.city = getDefaultCityForCountry(CountryStore.country);
	 *     }
	 *   });
	 *
	 * The usage of `waitFor()` can be chained, for example:
	 *
	 *   FlightPriceStore.dispatchToken =
	 *     flightDispatcher.register(function(payload) {
	 *       switch (payload.actionType) {
	 *         case 'country-update':
	 *         case 'city-update':
	 *           flightDispatcher.waitFor([CityStore.dispatchToken]);
	 *           FlightPriceStore.price =
	 *             getFlightPriceStore(CountryStore.country, CityStore.city);
	 *           break;
	 *     }
	 *   });
	 *
	 * The `country-update` payload will be guaranteed to invoke the stores'
	 * registered callbacks in order: `CountryStore`, `CityStore`, then
	 * `FlightPriceStore`.
	 */

	var Dispatcher = (function () {
	  function Dispatcher() {
	    _classCallCheck(this, Dispatcher);

	    this._callbacks = {};
	    this._isDispatching = false;
	    this._isHandled = {};
	    this._isPending = {};
	    this._lastID = 1;
	  }

	  /**
	   * Registers a callback to be invoked with every dispatched payload. Returns
	   * a token that can be used with `waitFor()`.
	   */

	  Dispatcher.prototype.register = function register(callback) {
	    var id = _prefix + this._lastID++;
	    this._callbacks[id] = callback;
	    return id;
	  };

	  /**
	   * Removes a callback based on its token.
	   */

	  Dispatcher.prototype.unregister = function unregister(id) {
	    !this._callbacks[id] ? process.env.NODE_ENV !== 'production' ? invariant(false, 'Dispatcher.unregister(...): `%s` does not map to a registered callback.', id) : invariant(false) : undefined;
	    delete this._callbacks[id];
	  };

	  /**
	   * Waits for the callbacks specified to be invoked before continuing execution
	   * of the current callback. This method should only be used by a callback in
	   * response to a dispatched payload.
	   */

	  Dispatcher.prototype.waitFor = function waitFor(ids) {
	    !this._isDispatching ? process.env.NODE_ENV !== 'production' ? invariant(false, 'Dispatcher.waitFor(...): Must be invoked while dispatching.') : invariant(false) : undefined;
	    for (var ii = 0; ii < ids.length; ii++) {
	      var id = ids[ii];
	      if (this._isPending[id]) {
	        !this._isHandled[id] ? process.env.NODE_ENV !== 'production' ? invariant(false, 'Dispatcher.waitFor(...): Circular dependency detected while ' + 'waiting for `%s`.', id) : invariant(false) : undefined;
	        continue;
	      }
	      !this._callbacks[id] ? process.env.NODE_ENV !== 'production' ? invariant(false, 'Dispatcher.waitFor(...): `%s` does not map to a registered callback.', id) : invariant(false) : undefined;
	      this._invokeCallback(id);
	    }
	  };

	  /**
	   * Dispatches a payload to all registered callbacks.
	   */

	  Dispatcher.prototype.dispatch = function dispatch(payload) {
	    !!this._isDispatching ? process.env.NODE_ENV !== 'production' ? invariant(false, 'Dispatch.dispatch(...): Cannot dispatch in the middle of a dispatch.') : invariant(false) : undefined;
	    this._startDispatching(payload);
	    try {
	      for (var id in this._callbacks) {
	        if (this._isPending[id]) {
	          continue;
	        }
	        this._invokeCallback(id);
	      }
	    } finally {
	      this._stopDispatching();
	    }
	  };

	  /**
	   * Is this Dispatcher currently dispatching.
	   */

	  Dispatcher.prototype.isDispatching = function isDispatching() {
	    return this._isDispatching;
	  };

	  /**
	   * Call the callback stored with the given id. Also do some internal
	   * bookkeeping.
	   *
	   * @internal
	   */

	  Dispatcher.prototype._invokeCallback = function _invokeCallback(id) {
	    this._isPending[id] = true;
	    this._callbacks[id](this._pendingPayload);
	    this._isHandled[id] = true;
	  };

	  /**
	   * Set up bookkeeping needed when dispatching.
	   *
	   * @internal
	   */

	  Dispatcher.prototype._startDispatching = function _startDispatching(payload) {
	    for (var id in this._callbacks) {
	      this._isPending[id] = false;
	      this._isHandled[id] = false;
	    }
	    this._pendingPayload = payload;
	    this._isDispatching = true;
	  };

	  /**
	   * Clear bookkeeping used for dispatching.
	   *
	   * @internal
	   */

	  Dispatcher.prototype._stopDispatching = function _stopDispatching() {
	    delete this._pendingPayload;
	    this._isDispatching = false;
	  };

	  return Dispatcher;
	})();

	module.exports = Dispatcher;
	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(15)))

/***/ },
/* 15 */
/***/ function(module, exports) {

	// shim for using process in browser

	var process = module.exports = {};
	var queue = [];
	var draining = false;
	var currentQueue;
	var queueIndex = -1;

	function cleanUpNextTick() {
	    if (!draining || !currentQueue) {
	        return;
	    }
	    draining = false;
	    if (currentQueue.length) {
	        queue = currentQueue.concat(queue);
	    } else {
	        queueIndex = -1;
	    }
	    if (queue.length) {
	        drainQueue();
	    }
	}

	function drainQueue() {
	    if (draining) {
	        return;
	    }
	    var timeout = setTimeout(cleanUpNextTick);
	    draining = true;

	    var len = queue.length;
	    while(len) {
	        currentQueue = queue;
	        queue = [];
	        while (++queueIndex < len) {
	            if (currentQueue) {
	                currentQueue[queueIndex].run();
	            }
	        }
	        queueIndex = -1;
	        len = queue.length;
	    }
	    currentQueue = null;
	    draining = false;
	    clearTimeout(timeout);
	}

	process.nextTick = function (fun) {
	    var args = new Array(arguments.length - 1);
	    if (arguments.length > 1) {
	        for (var i = 1; i < arguments.length; i++) {
	            args[i - 1] = arguments[i];
	        }
	    }
	    queue.push(new Item(fun, args));
	    if (queue.length === 1 && !draining) {
	        setTimeout(drainQueue, 0);
	    }
	};

	// v8 likes predictible objects
	function Item(fun, array) {
	    this.fun = fun;
	    this.array = array;
	}
	Item.prototype.run = function () {
	    this.fun.apply(null, this.array);
	};
	process.title = 'browser';
	process.browser = true;
	process.env = {};
	process.argv = [];
	process.version = ''; // empty string to avoid regexp issues
	process.versions = {};

	function noop() {}

	process.on = noop;
	process.addListener = noop;
	process.once = noop;
	process.off = noop;
	process.removeListener = noop;
	process.removeAllListeners = noop;
	process.emit = noop;

	process.binding = function (name) {
	    throw new Error('process.binding is not supported');
	};

	process.cwd = function () { return '/' };
	process.chdir = function (dir) {
	    throw new Error('process.chdir is not supported');
	};
	process.umask = function() { return 0; };


/***/ },
/* 16 */
/***/ function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(process) {/**
	 * Copyright 2013-2015, Facebook, Inc.
	 * All rights reserved.
	 *
	 * This source code is licensed under the BSD-style license found in the
	 * LICENSE file in the root directory of this source tree. An additional grant
	 * of patent rights can be found in the PATENTS file in the same directory.
	 *
	 * @providesModule invariant
	 */

	"use strict";

	/**
	 * Use invariant() to assert state which your program assumes to be true.
	 *
	 * Provide sprintf-style format (only %s is supported) and arguments
	 * to provide information about what broke and what you were
	 * expecting.
	 *
	 * The invariant message will be stripped in production, but the invariant
	 * will remain to ensure logic does not differ in production.
	 */

	var invariant = function (condition, format, a, b, c, d, e, f) {
	  if (process.env.NODE_ENV !== 'production') {
	    if (format === undefined) {
	      throw new Error('invariant requires an error message argument');
	    }
	  }

	  if (!condition) {
	    var error;
	    if (format === undefined) {
	      error = new Error('Minified exception occurred; use the non-minified dev environment ' + 'for the full error message and additional helpful warnings.');
	    } else {
	      var args = [a, b, c, d, e, f];
	      var argIndex = 0;
	      error = new Error('Invariant Violation: ' + format.replace(/%s/g, function () {
	        return args[argIndex++];
	      }));
	    }

	    error.framesToPop = 1; // we don't care about invariant's own frame
	    throw error;
	  }
	};

	module.exports = invariant;
	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(15)))

/***/ },
/* 17 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, "__esModule", {
	  value: true
	});
	exports.setAppState = setAppState;
	exports.snapshot = snapshot;
	exports.saveInitialSnapshot = saveInitialSnapshot;
	exports.filterSnapshots = filterSnapshots;

	var _functions = __webpack_require__(18);

	var fn = _interopRequireWildcard(_functions);

	function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) newObj[key] = obj[key]; } } newObj['default'] = obj; return newObj; } }

	function setAppState(instance, data, onStore) {
	  var obj = instance.deserialize(data);
	  fn.eachObject(function (key, value) {
	    var store = instance.stores[key];
	    if (store) {
	      (function () {
	        var config = store.StoreModel.config;

	        var state = store.state;
	        if (config.onDeserialize) obj[key] = config.onDeserialize(value) || value;
	        if (fn.isMutableObject(state)) {
	          fn.eachObject(function (k) {
	            return delete state[k];
	          }, [state]);
	          fn.assign(state, obj[key]);
	        } else {
	          store.state = obj[key];
	        }
	        onStore(store, store.state);
	      })();
	    }
	  }, [obj]);
	}

	function snapshot(instance) {
	  var storeNames = arguments.length <= 1 || arguments[1] === undefined ? [] : arguments[1];

	  var stores = storeNames.length ? storeNames : Object.keys(instance.stores);
	  return stores.reduce(function (obj, storeHandle) {
	    var storeName = storeHandle.displayName || storeHandle;
	    var store = instance.stores[storeName];
	    var config = store.StoreModel.config;

	    store.lifecycle('snapshot');
	    var customSnapshot = config.onSerialize && config.onSerialize(store.state);
	    obj[storeName] = customSnapshot ? customSnapshot : store.getState();
	    return obj;
	  }, {});
	}

	function saveInitialSnapshot(instance, key) {
	  var state = instance.deserialize(instance.serialize(instance.stores[key].state));
	  instance._initSnapshot[key] = state;
	  instance._lastSnapshot[key] = state;
	}

	function filterSnapshots(instance, state, stores) {
	  return stores.reduce(function (obj, store) {
	    var storeName = store.displayName || store;
	    if (!state[storeName]) {
	      throw new ReferenceError(String(storeName) + ' is not a valid store');
	    }
	    obj[storeName] = state[storeName];
	    return obj;
	  }, {});
	}

/***/ },
/* 18 */
/***/ function(module, exports) {

	'use strict';

	Object.defineProperty(exports, "__esModule", {
	  value: true
	});
	exports.isMutableObject = isMutableObject;
	exports.eachObject = eachObject;
	exports.assign = assign;
	var isFunction = exports.isFunction = function isFunction(x) {
	  return typeof x === 'function';
	};

	function isMutableObject(target) {
	  var Ctor = target.constructor;

	  return !!target && Object.prototype.toString.call(target) === '[object Object]' && isFunction(Ctor) && !Object.isFrozen(target) && (Ctor instanceof Ctor || target.type === 'AltStore');
	}

	function eachObject(f, o) {
	  o.forEach(function (from) {
	    Object.keys(Object(from)).forEach(function (key) {
	      f(key, from[key]);
	    });
	  });
	}

	function assign(target) {
	  for (var _len = arguments.length, source = Array(_len > 1 ? _len - 1 : 0), _key = 1; _key < _len; _key++) {
	    source[_key - 1] = arguments[_key];
	  }

	  eachObject(function (key, value) {
	    return target[key] = value;
	  }, source);
	  return target;
	}

/***/ },
/* 19 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, "__esModule", {
	  value: true
	});
	exports.createStoreConfig = createStoreConfig;
	exports.transformStore = transformStore;
	exports.createStoreFromObject = createStoreFromObject;
	exports.createStoreFromClass = createStoreFromClass;

	var _AltUtils = __webpack_require__(20);

	var utils = _interopRequireWildcard(_AltUtils);

	var _functions = __webpack_require__(18);

	var fn = _interopRequireWildcard(_functions);

	var _AltStore = __webpack_require__(21);

	var _AltStore2 = _interopRequireDefault(_AltStore);

	var _StoreMixin = __webpack_require__(23);

	var _StoreMixin2 = _interopRequireDefault(_StoreMixin);

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

	function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) newObj[key] = obj[key]; } } newObj['default'] = obj; return newObj; } }

	function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

	function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

	function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

	function doSetState(store, storeInstance, state) {
	  if (!state) {
	    return;
	  }

	  var config = storeInstance.StoreModel.config;


	  var nextState = fn.isFunction(state) ? state(storeInstance.state) : state;

	  storeInstance.state = config.setState.call(store, storeInstance.state, nextState);

	  if (!store.alt.dispatcher.isDispatching()) {
	    store.emitChange();
	  }
	}

	function createPrototype(proto, alt, key, extras) {
	  return fn.assign(proto, _StoreMixin2.default, {
	    displayName: key,
	    alt: alt,
	    dispatcher: alt.dispatcher,
	    preventDefault: function () {
	      function preventDefault() {
	        this.getInstance().preventDefault = true;
	      }

	      return preventDefault;
	    }(),

	    boundListeners: [],
	    lifecycleEvents: {},
	    actionListeners: {},
	    publicMethods: {},
	    handlesOwnErrors: false
	  }, extras);
	}

	function createStoreConfig(globalConfig, StoreModel) {
	  StoreModel.config = fn.assign({
	    getState: function () {
	      function getState(state) {
	        if (Array.isArray(state)) {
	          return state.slice();
	        } else if (fn.isMutableObject(state)) {
	          return fn.assign({}, state);
	        }

	        return state;
	      }

	      return getState;
	    }(),
	    setState: function () {
	      function setState(currentState, nextState) {
	        if (fn.isMutableObject(nextState)) {
	          return fn.assign(currentState, nextState);
	        }
	        return nextState;
	      }

	      return setState;
	    }()
	  }, globalConfig, StoreModel.config);
	}

	function transformStore(transforms, StoreModel) {
	  return transforms.reduce(function (Store, transform) {
	    return transform(Store);
	  }, StoreModel);
	}

	function createStoreFromObject(alt, StoreModel, key) {
	  var storeInstance = void 0;

	  var StoreProto = createPrototype({}, alt, key, fn.assign({
	    getInstance: function () {
	      function getInstance() {
	        return storeInstance;
	      }

	      return getInstance;
	    }(),
	    setState: function () {
	      function setState(nextState) {
	        doSetState(this, storeInstance, nextState);
	      }

	      return setState;
	    }()
	  }, StoreModel));

	  // bind the store listeners
	  /* istanbul ignore else */
	  if (StoreProto.bindListeners) {
	    _StoreMixin2.default.bindListeners.call(StoreProto, StoreProto.bindListeners);
	  }
	  /* istanbul ignore else */
	  if (StoreProto.observe) {
	    _StoreMixin2.default.bindListeners.call(StoreProto, StoreProto.observe(alt));
	  }

	  // bind the lifecycle events
	  /* istanbul ignore else */
	  if (StoreProto.lifecycle) {
	    fn.eachObject(function (eventName, event) {
	      _StoreMixin2.default.on.call(StoreProto, eventName, event);
	    }, [StoreProto.lifecycle]);
	  }

	  // create the instance and fn.assign the public methods to the instance
	  storeInstance = fn.assign(new _AltStore2.default(alt, StoreProto, StoreProto.state !== undefined ? StoreProto.state : {}, StoreModel), StoreProto.publicMethods, {
	    displayName: key,
	    config: StoreModel.config
	  });

	  return storeInstance;
	}

	function createStoreFromClass(alt, StoreModel, key) {
	  var storeInstance = void 0;
	  var config = StoreModel.config;

	  // Creating a class here so we don't overload the provided store's
	  // prototype with the mixin behaviour and I'm extending from StoreModel
	  // so we can inherit any extensions from the provided store.

	  var Store = function (_StoreModel) {
	    _inherits(Store, _StoreModel);

	    function Store() {
	      _classCallCheck(this, Store);

	      for (var _len2 = arguments.length, args = Array(_len2), _key2 = 0; _key2 < _len2; _key2++) {
	        args[_key2] = arguments[_key2];
	      }

	      return _possibleConstructorReturn(this, _StoreModel.call.apply(_StoreModel, [this].concat(args)));
	    }

	    return Store;
	  }(StoreModel);

	  createPrototype(Store.prototype, alt, key, {
	    type: 'AltStore',
	    getInstance: function () {
	      function getInstance() {
	        return storeInstance;
	      }

	      return getInstance;
	    }(),
	    setState: function () {
	      function setState(nextState) {
	        doSetState(this, storeInstance, nextState);
	      }

	      return setState;
	    }()
	  });

	  for (var _len = arguments.length, argsForClass = Array(_len > 3 ? _len - 3 : 0), _key = 3; _key < _len; _key++) {
	    argsForClass[_key - 3] = arguments[_key];
	  }

	  var store = new (Function.prototype.bind.apply(Store, [null].concat(argsForClass)))();

	  /* istanbul ignore next */
	  if (config.bindListeners) store.bindListeners(config.bindListeners);
	  /* istanbul ignore next */
	  if (config.datasource) store.registerAsync(config.datasource);

	  storeInstance = fn.assign(new _AltStore2.default(alt, store, store.state !== undefined ? store.state : store, StoreModel), utils.getInternalMethods(StoreModel), config.publicMethods, { displayName: key });

	  return storeInstance;
	}

/***/ },
/* 20 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

	Object.defineProperty(exports, "__esModule", {
	  value: true
	});
	exports.getInternalMethods = getInternalMethods;
	exports.getPrototypeChain = getPrototypeChain;
	exports.warn = warn;
	exports.uid = uid;
	exports.formatAsConstant = formatAsConstant;
	exports.dispatchIdentity = dispatchIdentity;
	exports.fsa = fsa;
	exports.dispatch = dispatch;

	var _functions = __webpack_require__(18);

	var fn = _interopRequireWildcard(_functions);

	function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) newObj[key] = obj[key]; } } newObj['default'] = obj; return newObj; } }

	/*eslint-disable*/
	var builtIns = Object.getOwnPropertyNames(NoopClass);
	var builtInProto = Object.getOwnPropertyNames(NoopClass.prototype);
	/*eslint-enable*/

	function getInternalMethods(Obj, isProto) {
	  var excluded = isProto ? builtInProto : builtIns;
	  var obj = isProto ? Obj.prototype : Obj;
	  return Object.getOwnPropertyNames(obj).reduce(function (value, m) {
	    if (excluded.indexOf(m) !== -1) {
	      return value;
	    }

	    value[m] = obj[m];
	    return value;
	  }, {});
	}

	function getPrototypeChain(Obj) {
	  var methods = arguments.length <= 1 || arguments[1] === undefined ? {} : arguments[1];

	  return Obj === Function.prototype ? methods : getPrototypeChain(Object.getPrototypeOf(Obj), fn.assign(methods, getInternalMethods(Obj, true)));
	}

	function warn(msg) {
	  /* istanbul ignore else */
	  /*eslint-disable*/
	  if (typeof console !== 'undefined') {
	    console.warn(new ReferenceError(msg));
	  }
	  /*eslint-enable*/
	}

	function uid(container, name) {
	  var count = 0;
	  var key = name;
	  while (Object.hasOwnProperty.call(container, key)) {
	    key = name + String(++count);
	  }
	  return key;
	}

	function formatAsConstant(name) {
	  return name.replace(/[a-z]([A-Z])/g, function (i) {
	    return String(i[0]) + '_' + String(i[1].toLowerCase());
	  }).toUpperCase();
	}

	function dispatchIdentity(x) {
	  if (x === undefined) return null;

	  for (var _len = arguments.length, a = Array(_len > 1 ? _len - 1 : 0), _key = 1; _key < _len; _key++) {
	    a[_key - 1] = arguments[_key];
	  }

	  return a.length ? [x].concat(a) : x;
	}

	function fsa(id, type, payload, details) {
	  return {
	    type: type,
	    payload: payload,
	    meta: _extends({
	      dispatchId: id
	    }, details),

	    id: id,
	    action: type,
	    data: payload,
	    details: details
	  };
	}

	function dispatch(id, actionObj, payload, alt) {
	  var data = actionObj.dispatch(payload);
	  if (data === undefined) return null;

	  var type = actionObj.id;
	  var namespace = type;
	  var name = type;
	  var details = { id: type, namespace: namespace, name: name };

	  var dispatchLater = function dispatchLater(x) {
	    return alt.dispatch(type, x, details);
	  };

	  if (fn.isFunction(data)) return data(dispatchLater, alt);

	  // XXX standardize this
	  return alt.dispatcher.dispatch(fsa(id, type, data, details));
	}

	/* istanbul ignore next */
	function NoopClass() {}

/***/ },
/* 21 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, "__esModule", {
	  value: true
	});

	var _functions = __webpack_require__(18);

	var fn = _interopRequireWildcard(_functions);

	var _transmitter = __webpack_require__(22);

	var _transmitter2 = _interopRequireDefault(_transmitter);

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

	function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) newObj[key] = obj[key]; } } newObj['default'] = obj; return newObj; } }

	function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

	var AltStore = function () {
	  function AltStore(alt, model, state, StoreModel) {
	    var _this = this;

	    _classCallCheck(this, AltStore);

	    var lifecycleEvents = model.lifecycleEvents;
	    this.transmitter = (0, _transmitter2.default)();
	    this.lifecycle = function (event, x) {
	      if (lifecycleEvents[event]) lifecycleEvents[event].push(x);
	    };
	    this.state = state;

	    this.alt = alt;
	    this.preventDefault = false;
	    this.displayName = model.displayName;
	    this.boundListeners = model.boundListeners;
	    this.StoreModel = StoreModel;
	    this.reduce = model.reduce || function (x) {
	      return x;
	    };

	    var output = model.output || function (x) {
	      return x;
	    };

	    this.emitChange = function () {
	      return _this.transmitter.push(output(_this.state));
	    };

	    var handleDispatch = function handleDispatch(f, payload) {
	      try {
	        return f();
	      } catch (e) {
	        if (model.handlesOwnErrors) {
	          _this.lifecycle('error', {
	            error: e,
	            payload: payload,
	            state: _this.state
	          });
	          return false;
	        }

	        throw e;
	      }
	    };

	    fn.assign(this, model.publicMethods);

	    // Register dispatcher
	    this.dispatchToken = alt.dispatcher.register(function (payload) {
	      _this.preventDefault = false;

	      _this.lifecycle('beforeEach', {
	        payload: payload,
	        state: _this.state
	      });

	      var actionHandlers = model.actionListeners[payload.action];

	      if (actionHandlers || model.otherwise) {
	        var result = void 0;

	        if (actionHandlers) {
	          result = handleDispatch(function () {
	            return actionHandlers.filter(Boolean).every(function (handler) {
	              return handler.call(model, payload.data, payload.action) !== false;
	            });
	          }, payload);
	        } else {
	          result = handleDispatch(function () {
	            return model.otherwise(payload.data, payload.action);
	          }, payload);
	        }

	        if (result !== false && !_this.preventDefault) _this.emitChange();
	      }

	      if (model.reduce) {
	        handleDispatch(function () {
	          var value = model.reduce(_this.state, payload);
	          if (value !== undefined) _this.state = value;
	        }, payload);
	        if (!_this.preventDefault) _this.emitChange();
	      }

	      _this.lifecycle('afterEach', {
	        payload: payload,
	        state: _this.state
	      });
	    });

	    this.lifecycle('init');
	  }

	  AltStore.prototype.listen = function () {
	    function listen(cb) {
	      var _this2 = this;

	      if (!fn.isFunction(cb)) throw new TypeError('listen expects a function');
	      this.transmitter.subscribe(cb);
	      return function () {
	        return _this2.unlisten(cb);
	      };
	    }

	    return listen;
	  }();

	  AltStore.prototype.unlisten = function () {
	    function unlisten(cb) {
	      this.lifecycle('unlisten');
	      this.transmitter.unsubscribe(cb);
	    }

	    return unlisten;
	  }();

	  AltStore.prototype.getState = function () {
	    function getState() {
	      return this.StoreModel.config.getState.call(this, this.state);
	    }

	    return getState;
	  }();

	  return AltStore;
	}();

	exports.default = AltStore;
	module.exports = exports['default'];

/***/ },
/* 22 */
/***/ function(module, exports) {

	'use strict';

	function transmitter() {
	  var subscriptions = [];
	  var pushing = false;
	  var toUnsubscribe = [];

	  var unsubscribe = function unsubscribe(onChange) {
	    if (pushing) {
	      toUnsubscribe.push(onChange);
	      return;
	    }
	    var id = subscriptions.indexOf(onChange);
	    if (id >= 0) subscriptions.splice(id, 1);
	  };

	  var subscribe = function subscribe(onChange) {
	    subscriptions.push(onChange);
	    var dispose = function dispose() {
	      return unsubscribe(onChange);
	    };
	    return { dispose: dispose };
	  };

	  var push = function push(value) {
	    if (pushing) throw new Error('Cannot push while pushing');
	    pushing = true;
	    try {
	      subscriptions.forEach(function (subscription) {
	        return subscription(value);
	      });
	    } finally {
	      pushing = false;
	      toUnsubscribe = toUnsubscribe.filter(unsubscribe);
	    }
	  };

	  return { subscribe: subscribe, push: push, unsubscribe: unsubscribe, subscriptions: subscriptions };
	}

	module.exports = transmitter;



/***/ },
/* 23 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, "__esModule", {
	  value: true
	});

	var _transmitter = __webpack_require__(22);

	var _transmitter2 = _interopRequireDefault(_transmitter);

	var _functions = __webpack_require__(18);

	var fn = _interopRequireWildcard(_functions);

	function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) newObj[key] = obj[key]; } } newObj['default'] = obj; return newObj; } }

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

	var StoreMixin = {
	  waitFor: function () {
	    function waitFor() {
	      for (var _len = arguments.length, sources = Array(_len), _key = 0; _key < _len; _key++) {
	        sources[_key] = arguments[_key];
	      }

	      if (!sources.length) {
	        throw new ReferenceError('Dispatch tokens not provided');
	      }

	      var sourcesArray = sources;
	      if (sources.length === 1) {
	        sourcesArray = Array.isArray(sources[0]) ? sources[0] : sources;
	      }

	      var tokens = sourcesArray.map(function (source) {
	        return source.dispatchToken || source;
	      });

	      this.dispatcher.waitFor(tokens);
	    }

	    return waitFor;
	  }(),
	  exportAsync: function () {
	    function exportAsync(asyncMethods) {
	      this.registerAsync(asyncMethods);
	    }

	    return exportAsync;
	  }(),
	  registerAsync: function () {
	    function registerAsync(asyncDef) {
	      var _this = this;

	      var loadCounter = 0;

	      var asyncMethods = fn.isFunction(asyncDef) ? asyncDef(this.alt) : asyncDef;

	      var toExport = Object.keys(asyncMethods).reduce(function (publicMethods, methodName) {
	        var desc = asyncMethods[methodName];
	        var spec = fn.isFunction(desc) ? desc(_this) : desc;

	        var validHandlers = ['success', 'error', 'loading'];
	        validHandlers.forEach(function (handler) {
	          if (spec[handler] && !spec[handler].id) {
	            throw new Error(String(handler) + ' handler must be an action function');
	          }
	        });

	        publicMethods[methodName] = function () {
	          for (var _len2 = arguments.length, args = Array(_len2), _key2 = 0; _key2 < _len2; _key2++) {
	            args[_key2] = arguments[_key2];
	          }

	          var state = _this.getInstance().getState();
	          var value = spec.local && spec.local.apply(spec, [state].concat(args));
	          var shouldFetch = spec.shouldFetch ? spec.shouldFetch.apply(spec, [state].concat(args))
	          /*eslint-disable*/
	          : value == null;
	          /*eslint-enable*/
	          var intercept = spec.interceptResponse || function (x) {
	            return x;
	          };

	          var makeActionHandler = function () {
	            function makeActionHandler(action, isError) {
	              return function (x) {
	                var fire = function () {
	                  function fire() {
	                    loadCounter -= 1;
	                    action(intercept(x, action, args));
	                    if (isError) throw x;
	                  }

	                  return fire;
	                }();
	                return _this.alt.trapAsync ? function () {
	                  return fire();
	                } : fire();
	              };
	            }

	            return makeActionHandler;
	          }();

	          // if we don't have it in cache then fetch it
	          if (shouldFetch) {
	            loadCounter += 1;
	            /* istanbul ignore else */
	            if (spec.loading) spec.loading(intercept(null, spec.loading, args));
	            return spec.remote.apply(spec, [state].concat(args)).then(makeActionHandler(spec.success), makeActionHandler(spec.error, 1));
	          }

	          // otherwise emit the change now
	          _this.emitChange();
	          return value;
	        };

	        return publicMethods;
	      }, {});

	      this.exportPublicMethods(toExport);
	      this.exportPublicMethods({
	        isLoading: function () {
	          function isLoading() {
	            return loadCounter > 0;
	          }

	          return isLoading;
	        }()
	      });
	    }

	    return registerAsync;
	  }(),
	  exportPublicMethods: function () {
	    function exportPublicMethods(methods) {
	      var _this2 = this;

	      fn.eachObject(function (methodName, value) {
	        if (!fn.isFunction(value)) {
	          throw new TypeError('exportPublicMethods expects a function');
	        }

	        _this2.publicMethods[methodName] = value;
	      }, [methods]);
	    }

	    return exportPublicMethods;
	  }(),
	  emitChange: function () {
	    function emitChange() {
	      this.getInstance().emitChange();
	    }

	    return emitChange;
	  }(),
	  on: function () {
	    function on(lifecycleEvent, handler) {
	      if (lifecycleEvent === 'error') this.handlesOwnErrors = true;
	      var bus = this.lifecycleEvents[lifecycleEvent] || (0, _transmitter2.default)();
	      this.lifecycleEvents[lifecycleEvent] = bus;
	      return bus.subscribe(handler.bind(this));
	    }

	    return on;
	  }(),
	  bindAction: function () {
	    function bindAction(symbol, handler) {
	      if (!symbol) {
	        throw new ReferenceError('Invalid action reference passed in');
	      }
	      if (!fn.isFunction(handler)) {
	        throw new TypeError('bindAction expects a function');
	      }

	      // You can pass in the constant or the function itself
	      var key = symbol.id ? symbol.id : symbol;
	      this.actionListeners[key] = this.actionListeners[key] || [];
	      this.actionListeners[key].push(handler.bind(this));
	      this.boundListeners.push(key);
	    }

	    return bindAction;
	  }(),
	  bindActions: function () {
	    function bindActions(actions) {
	      var _this3 = this;

	      fn.eachObject(function (action, symbol) {
	        var matchFirstCharacter = /./;
	        var assumedEventHandler = action.replace(matchFirstCharacter, function (x) {
	          return 'on' + String(x[0].toUpperCase());
	        });

	        if (_this3[action] && _this3[assumedEventHandler]) {
	          // If you have both action and onAction
	          throw new ReferenceError('You have multiple action handlers bound to an action: ' + (String(action) + ' and ' + String(assumedEventHandler)));
	        }

	        var handler = _this3[action] || _this3[assumedEventHandler];
	        if (handler) {
	          _this3.bindAction(symbol, handler);
	        }
	      }, [actions]);
	    }

	    return bindActions;
	  }(),
	  bindListeners: function () {
	    function bindListeners(obj) {
	      var _this4 = this;

	      fn.eachObject(function (methodName, symbol) {
	        var listener = _this4[methodName];

	        if (!listener) {
	          throw new ReferenceError(String(methodName) + ' defined but does not exist in ' + String(_this4.displayName));
	        }

	        if (Array.isArray(symbol)) {
	          symbol.forEach(function (action) {
	            _this4.bindAction(action, listener);
	          });
	        } else {
	          _this4.bindAction(symbol, listener);
	        }
	      }, [obj]);
	    }

	    return bindListeners;
	  }()
	};

	exports.default = StoreMixin;
	module.exports = exports['default'];

/***/ },
/* 24 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	Object.defineProperty(exports, "__esModule", {
	  value: true
	});
	exports['default'] = makeAction;

	var _functions = __webpack_require__(18);

	var fn = _interopRequireWildcard(_functions);

	var _AltUtils = __webpack_require__(20);

	var utils = _interopRequireWildcard(_AltUtils);

	var _isPromise = __webpack_require__(25);

	var _isPromise2 = _interopRequireDefault(_isPromise);

	function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

	function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) newObj[key] = obj[key]; } } newObj['default'] = obj; return newObj; } }

	function makeAction(alt, namespace, name, implementation, obj) {
	  var id = utils.uid(alt._actionsRegistry, String(namespace) + '.' + String(name));
	  alt._actionsRegistry[id] = 1;

	  var data = { id: id, namespace: namespace, name: name };

	  var dispatch = function dispatch(payload) {
	    return alt.dispatch(id, payload, data);
	  };

	  // the action itself
	  var action = function action() {
	    for (var _len = arguments.length, args = Array(_len), _key = 0; _key < _len; _key++) {
	      args[_key] = arguments[_key];
	    }

	    var invocationResult = implementation.apply(obj, args);
	    var actionResult = invocationResult;

	    // async functions that return promises should not be dispatched
	    if (invocationResult !== undefined && !(0, _isPromise2.default)(invocationResult)) {
	      if (fn.isFunction(invocationResult)) {
	        // inner function result should be returned as an action result
	        actionResult = invocationResult(dispatch, alt);
	      } else {
	        dispatch(invocationResult);
	      }
	    }

	    if (invocationResult === undefined) {
	      utils.warn('An action was called but nothing was dispatched');
	    }

	    return actionResult;
	  };
	  action.defer = function () {
	    for (var _len2 = arguments.length, args = Array(_len2), _key2 = 0; _key2 < _len2; _key2++) {
	      args[_key2] = arguments[_key2];
	    }

	    return setTimeout(function () {
	      return action.apply(null, args);
	    });
	  };
	  action.id = id;
	  action.data = data;

	  // ensure each reference is unique in the namespace
	  var container = alt.actions[namespace];
	  var namespaceId = utils.uid(container, name);
	  container[namespaceId] = action;

	  // generate a constant
	  var constant = utils.formatAsConstant(namespaceId);
	  container[constant] = id;

	  return action;
	}
	module.exports = exports['default'];

/***/ },
/* 25 */
/***/ function(module, exports) {

	module.exports = isPromise;

	function isPromise(obj) {
	  return !!obj && (typeof obj === 'object' || typeof obj === 'function') && typeof obj.then === 'function';
	}


/***/ },
/* 26 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var alt = __webpack_require__(11);
	var CategoriaSource = __webpack_require__(27);

	var TreeCategoriasActionBase = function TreeCategoriasActionBase() {
	  this.generateActions('fetchChildrenForItemSuccess', 'fetchChildrenForItemFail', 'fetchCategoriasSuccess', 'fetchCategoriasFail');
	};
	TreeCategoriasActionBase.prototype = {
	  displayName: 'TreeCategoriasAction',
	  fetchCategorias: function fetchCategorias() {
	    return CategoriaSource.getAll().then(this.fetchCategoriasSuccess).catch(this.fetchCategoriasFail);
	  },
	  fetchChildrenForItem: function fetchChildrenForItem(item) {
	    var _this = this;

	    return CategoriaSource.getWithParent(item).then(function (categorias) {
	      return _this.fetchChildrenForItemSuccess.apply(_this, [categorias, item]);
	    }).catch(this.fetchChildrenForItemFail);
	  }
	};

	var TreeCategoriasAction = alt.createActions(TreeCategoriasActionBase, TreeCategoriasActionBase.displayName);
	module.exports = TreeCategoriasAction;

/***/ },
/* 27 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var request = __webpack_require__(28);
	var ConfigFacade = __webpack_require__(34);

	module.exports = {
	  getAll: function getAll() {
	    return new Promise(function (res, rej) {
	      var getRequest = request.get(ConfigFacade.config.url + '/' + ConfigFacade.config.groupDelimiter).set(ConfigFacade.config.headers || {});
	      var queries = ConfigFacade.config.queries || [];
	      queries.forEach(function (query) {
	        return getRequest.query(query);
	      });

	      getRequest.end(function (err, response) {
	        if (err) {
	          console.dir(err);
	          rej(err);
	          return;
	        }
	        if (response.status === 200) {
	          res(JSON.parse(response.xhr.responseText));
	        } else {
	          console.dir(response);
	          rej(response.xhr.responseText);
	        }
	      });
	    });
	  },
	  get: function get(codigo) {
	    return new Promise(function (res, rej) {
	      var getRequest = request.get(ConfigFacade.config.url + '/' + ConfigFacade.config.groupDelimiter + '/' + codigo).set(ConfigFacade.config.headers || {});
	      var queries = ConfigFacade.config.queries || [];
	      queries.forEach(function (query) {
	        return getRequest.query(query);
	      });
	      getRequest.end(function (err, response) {
	        if (err) {
	          rej(err);
	          return;
	        }
	        if (response.status === 200) {
	          res(JSON.parse(response.xhr.responseText));
	        } else {
	          rej(response.xhr.responseText);
	        }
	      });
	    });
	  },
	  getWithParent: function getWithParent(item) {
	    return new Promise(function (res, rej) {
	      var getRequest = request.get(ConfigFacade.config.url + '/' + ConfigFacade.config.itemDelimiter + '/' + item.codigo + '/' + ConfigFacade.config.groupDelimiter).set(ConfigFacade.config.headers || {});

	      var queries = ConfigFacade.config.queries || [];
	      queries.forEach(function (query) {
	        return getRequest.query(query);
	      });
	      getRequest.end(function (err, response) {
	        if (err) {
	          console.dir(err);
	          rej(err);
	          return;
	        }
	        if (response.status === 200) {
	          res(JSON.parse(response.xhr.responseText));
	        } else {
	          console.dir(response);
	          rej(response.xhr.responseText);
	        }
	      });
	    });
	  }
	};

/***/ },
/* 28 */
/***/ function(module, exports, __webpack_require__) {

	/**
	 * Module dependencies.
	 */

	var Emitter = __webpack_require__(29);
	var reduce = __webpack_require__(30);
	var requestBase = __webpack_require__(31);
	var isObject = __webpack_require__(32);

	/**
	 * Root reference for iframes.
	 */

	var root;
	if (typeof window !== 'undefined') { // Browser window
	  root = window;
	} else if (typeof self !== 'undefined') { // Web Worker
	  root = self;
	} else { // Other environments
	  root = this;
	}

	/**
	 * Noop.
	 */

	function noop(){};

	/**
	 * Check if `obj` is a host object,
	 * we don't want to serialize these :)
	 *
	 * TODO: future proof, move to compoent land
	 *
	 * @param {Object} obj
	 * @return {Boolean}
	 * @api private
	 */

	function isHost(obj) {
	  var str = {}.toString.call(obj);

	  switch (str) {
	    case '[object File]':
	    case '[object Blob]':
	    case '[object FormData]':
	      return true;
	    default:
	      return false;
	  }
	}

	/**
	 * Expose `request`.
	 */

	var request = module.exports = __webpack_require__(33).bind(null, Request);

	/**
	 * Determine XHR.
	 */

	request.getXHR = function () {
	  if (root.XMLHttpRequest
	      && (!root.location || 'file:' != root.location.protocol
	          || !root.ActiveXObject)) {
	    return new XMLHttpRequest;
	  } else {
	    try { return new ActiveXObject('Microsoft.XMLHTTP'); } catch(e) {}
	    try { return new ActiveXObject('Msxml2.XMLHTTP.6.0'); } catch(e) {}
	    try { return new ActiveXObject('Msxml2.XMLHTTP.3.0'); } catch(e) {}
	    try { return new ActiveXObject('Msxml2.XMLHTTP'); } catch(e) {}
	  }
	  return false;
	};

	/**
	 * Removes leading and trailing whitespace, added to support IE.
	 *
	 * @param {String} s
	 * @return {String}
	 * @api private
	 */

	var trim = ''.trim
	  ? function(s) { return s.trim(); }
	  : function(s) { return s.replace(/(^\s*|\s*$)/g, ''); };

	/**
	 * Serialize the given `obj`.
	 *
	 * @param {Object} obj
	 * @return {String}
	 * @api private
	 */

	function serialize(obj) {
	  if (!isObject(obj)) return obj;
	  var pairs = [];
	  for (var key in obj) {
	    if (null != obj[key]) {
	      pushEncodedKeyValuePair(pairs, key, obj[key]);
	        }
	      }
	  return pairs.join('&');
	}

	/**
	 * Helps 'serialize' with serializing arrays.
	 * Mutates the pairs array.
	 *
	 * @param {Array} pairs
	 * @param {String} key
	 * @param {Mixed} val
	 */

	function pushEncodedKeyValuePair(pairs, key, val) {
	  if (Array.isArray(val)) {
	    return val.forEach(function(v) {
	      pushEncodedKeyValuePair(pairs, key, v);
	    });
	  }
	  pairs.push(encodeURIComponent(key)
	    + '=' + encodeURIComponent(val));
	}

	/**
	 * Expose serialization method.
	 */

	 request.serializeObject = serialize;

	 /**
	  * Parse the given x-www-form-urlencoded `str`.
	  *
	  * @param {String} str
	  * @return {Object}
	  * @api private
	  */

	function parseString(str) {
	  var obj = {};
	  var pairs = str.split('&');
	  var parts;
	  var pair;

	  for (var i = 0, len = pairs.length; i < len; ++i) {
	    pair = pairs[i];
	    parts = pair.split('=');
	    obj[decodeURIComponent(parts[0])] = decodeURIComponent(parts[1]);
	  }

	  return obj;
	}

	/**
	 * Expose parser.
	 */

	request.parseString = parseString;

	/**
	 * Default MIME type map.
	 *
	 *     superagent.types.xml = 'application/xml';
	 *
	 */

	request.types = {
	  html: 'text/html',
	  json: 'application/json',
	  xml: 'application/xml',
	  urlencoded: 'application/x-www-form-urlencoded',
	  'form': 'application/x-www-form-urlencoded',
	  'form-data': 'application/x-www-form-urlencoded'
	};

	/**
	 * Default serialization map.
	 *
	 *     superagent.serialize['application/xml'] = function(obj){
	 *       return 'generated xml here';
	 *     };
	 *
	 */

	 request.serialize = {
	   'application/x-www-form-urlencoded': serialize,
	   'application/json': JSON.stringify
	 };

	 /**
	  * Default parsers.
	  *
	  *     superagent.parse['application/xml'] = function(str){
	  *       return { object parsed from str };
	  *     };
	  *
	  */

	request.parse = {
	  'application/x-www-form-urlencoded': parseString,
	  'application/json': JSON.parse
	};

	/**
	 * Parse the given header `str` into
	 * an object containing the mapped fields.
	 *
	 * @param {String} str
	 * @return {Object}
	 * @api private
	 */

	function parseHeader(str) {
	  var lines = str.split(/\r?\n/);
	  var fields = {};
	  var index;
	  var line;
	  var field;
	  var val;

	  lines.pop(); // trailing CRLF

	  for (var i = 0, len = lines.length; i < len; ++i) {
	    line = lines[i];
	    index = line.indexOf(':');
	    field = line.slice(0, index).toLowerCase();
	    val = trim(line.slice(index + 1));
	    fields[field] = val;
	  }

	  return fields;
	}

	/**
	 * Check if `mime` is json or has +json structured syntax suffix.
	 *
	 * @param {String} mime
	 * @return {Boolean}
	 * @api private
	 */

	function isJSON(mime) {
	  return /[\/+]json\b/.test(mime);
	}

	/**
	 * Return the mime type for the given `str`.
	 *
	 * @param {String} str
	 * @return {String}
	 * @api private
	 */

	function type(str){
	  return str.split(/ *; */).shift();
	};

	/**
	 * Return header field parameters.
	 *
	 * @param {String} str
	 * @return {Object}
	 * @api private
	 */

	function params(str){
	  return reduce(str.split(/ *; */), function(obj, str){
	    var parts = str.split(/ *= */)
	      , key = parts.shift()
	      , val = parts.shift();

	    if (key && val) obj[key] = val;
	    return obj;
	  }, {});
	};

	/**
	 * Initialize a new `Response` with the given `xhr`.
	 *
	 *  - set flags (.ok, .error, etc)
	 *  - parse header
	 *
	 * Examples:
	 *
	 *  Aliasing `superagent` as `request` is nice:
	 *
	 *      request = superagent;
	 *
	 *  We can use the promise-like API, or pass callbacks:
	 *
	 *      request.get('/').end(function(res){});
	 *      request.get('/', function(res){});
	 *
	 *  Sending data can be chained:
	 *
	 *      request
	 *        .post('/user')
	 *        .send({ name: 'tj' })
	 *        .end(function(res){});
	 *
	 *  Or passed to `.send()`:
	 *
	 *      request
	 *        .post('/user')
	 *        .send({ name: 'tj' }, function(res){});
	 *
	 *  Or passed to `.post()`:
	 *
	 *      request
	 *        .post('/user', { name: 'tj' })
	 *        .end(function(res){});
	 *
	 * Or further reduced to a single call for simple cases:
	 *
	 *      request
	 *        .post('/user', { name: 'tj' }, function(res){});
	 *
	 * @param {XMLHTTPRequest} xhr
	 * @param {Object} options
	 * @api private
	 */

	function Response(req, options) {
	  options = options || {};
	  this.req = req;
	  this.xhr = this.req.xhr;
	  // responseText is accessible only if responseType is '' or 'text' and on older browsers
	  this.text = ((this.req.method !='HEAD' && (this.xhr.responseType === '' || this.xhr.responseType === 'text')) || typeof this.xhr.responseType === 'undefined')
	     ? this.xhr.responseText
	     : null;
	  this.statusText = this.req.xhr.statusText;
	  this.setStatusProperties(this.xhr.status);
	  this.header = this.headers = parseHeader(this.xhr.getAllResponseHeaders());
	  // getAllResponseHeaders sometimes falsely returns "" for CORS requests, but
	  // getResponseHeader still works. so we get content-type even if getting
	  // other headers fails.
	  this.header['content-type'] = this.xhr.getResponseHeader('content-type');
	  this.setHeaderProperties(this.header);
	  this.body = this.req.method != 'HEAD'
	    ? this.parseBody(this.text ? this.text : this.xhr.response)
	    : null;
	}

	/**
	 * Get case-insensitive `field` value.
	 *
	 * @param {String} field
	 * @return {String}
	 * @api public
	 */

	Response.prototype.get = function(field){
	  return this.header[field.toLowerCase()];
	};

	/**
	 * Set header related properties:
	 *
	 *   - `.type` the content type without params
	 *
	 * A response of "Content-Type: text/plain; charset=utf-8"
	 * will provide you with a `.type` of "text/plain".
	 *
	 * @param {Object} header
	 * @api private
	 */

	Response.prototype.setHeaderProperties = function(header){
	  // content-type
	  var ct = this.header['content-type'] || '';
	  this.type = type(ct);

	  // params
	  var obj = params(ct);
	  for (var key in obj) this[key] = obj[key];
	};

	/**
	 * Parse the given body `str`.
	 *
	 * Used for auto-parsing of bodies. Parsers
	 * are defined on the `superagent.parse` object.
	 *
	 * @param {String} str
	 * @return {Mixed}
	 * @api private
	 */

	Response.prototype.parseBody = function(str){
	  var parse = request.parse[this.type];
	  if (!parse && isJSON(this.type)) {
	    parse = request.parse['application/json'];
	  }
	  return parse && str && (str.length || str instanceof Object)
	    ? parse(str)
	    : null;
	};

	/**
	 * Set flags such as `.ok` based on `status`.
	 *
	 * For example a 2xx response will give you a `.ok` of __true__
	 * whereas 5xx will be __false__ and `.error` will be __true__. The
	 * `.clientError` and `.serverError` are also available to be more
	 * specific, and `.statusType` is the class of error ranging from 1..5
	 * sometimes useful for mapping respond colors etc.
	 *
	 * "sugar" properties are also defined for common cases. Currently providing:
	 *
	 *   - .noContent
	 *   - .badRequest
	 *   - .unauthorized
	 *   - .notAcceptable
	 *   - .notFound
	 *
	 * @param {Number} status
	 * @api private
	 */

	Response.prototype.setStatusProperties = function(status){
	  // handle IE9 bug: http://stackoverflow.com/questions/10046972/msie-returns-status-code-of-1223-for-ajax-request
	  if (status === 1223) {
	    status = 204;
	  }

	  var type = status / 100 | 0;

	  // status / class
	  this.status = this.statusCode = status;
	  this.statusType = type;

	  // basics
	  this.info = 1 == type;
	  this.ok = 2 == type;
	  this.clientError = 4 == type;
	  this.serverError = 5 == type;
	  this.error = (4 == type || 5 == type)
	    ? this.toError()
	    : false;

	  // sugar
	  this.accepted = 202 == status;
	  this.noContent = 204 == status;
	  this.badRequest = 400 == status;
	  this.unauthorized = 401 == status;
	  this.notAcceptable = 406 == status;
	  this.notFound = 404 == status;
	  this.forbidden = 403 == status;
	};

	/**
	 * Return an `Error` representative of this response.
	 *
	 * @return {Error}
	 * @api public
	 */

	Response.prototype.toError = function(){
	  var req = this.req;
	  var method = req.method;
	  var url = req.url;

	  var msg = 'cannot ' + method + ' ' + url + ' (' + this.status + ')';
	  var err = new Error(msg);
	  err.status = this.status;
	  err.method = method;
	  err.url = url;

	  return err;
	};

	/**
	 * Expose `Response`.
	 */

	request.Response = Response;

	/**
	 * Initialize a new `Request` with the given `method` and `url`.
	 *
	 * @param {String} method
	 * @param {String} url
	 * @api public
	 */

	function Request(method, url) {
	  var self = this;
	  this._query = this._query || [];
	  this.method = method;
	  this.url = url;
	  this.header = {}; // preserves header name case
	  this._header = {}; // coerces header names to lowercase
	  this.on('end', function(){
	    var err = null;
	    var res = null;

	    try {
	      res = new Response(self);
	    } catch(e) {
	      err = new Error('Parser is unable to parse the response');
	      err.parse = true;
	      err.original = e;
	      // issue #675: return the raw response if the response parsing fails
	      err.rawResponse = self.xhr && self.xhr.responseText ? self.xhr.responseText : null;
	      // issue #876: return the http status code if the response parsing fails
	      err.statusCode = self.xhr && self.xhr.status ? self.xhr.status : null;
	      return self.callback(err);
	    }

	    self.emit('response', res);

	    if (err) {
	      return self.callback(err, res);
	    }

	    if (res.status >= 200 && res.status < 300) {
	      return self.callback(err, res);
	    }

	    var new_err = new Error(res.statusText || 'Unsuccessful HTTP response');
	    new_err.original = err;
	    new_err.response = res;
	    new_err.status = res.status;

	    self.callback(new_err, res);
	  });
	}

	/**
	 * Mixin `Emitter` and `requestBase`.
	 */

	Emitter(Request.prototype);
	for (var key in requestBase) {
	  Request.prototype[key] = requestBase[key];
	}

	/**
	 * Abort the request, and clear potential timeout.
	 *
	 * @return {Request}
	 * @api public
	 */

	Request.prototype.abort = function(){
	  if (this.aborted) return;
	  this.aborted = true;
	  this.xhr.abort();
	  this.clearTimeout();
	  this.emit('abort');
	  return this;
	};

	/**
	 * Set Content-Type to `type`, mapping values from `request.types`.
	 *
	 * Examples:
	 *
	 *      superagent.types.xml = 'application/xml';
	 *
	 *      request.post('/')
	 *        .type('xml')
	 *        .send(xmlstring)
	 *        .end(callback);
	 *
	 *      request.post('/')
	 *        .type('application/xml')
	 *        .send(xmlstring)
	 *        .end(callback);
	 *
	 * @param {String} type
	 * @return {Request} for chaining
	 * @api public
	 */

	Request.prototype.type = function(type){
	  this.set('Content-Type', request.types[type] || type);
	  return this;
	};

	/**
	 * Set responseType to `val`. Presently valid responseTypes are 'blob' and 
	 * 'arraybuffer'.
	 *
	 * Examples:
	 *
	 *      req.get('/')
	 *        .responseType('blob')
	 *        .end(callback);
	 *
	 * @param {String} val
	 * @return {Request} for chaining
	 * @api public
	 */

	Request.prototype.responseType = function(val){
	  this._responseType = val;
	  return this;
	};

	/**
	 * Set Accept to `type`, mapping values from `request.types`.
	 *
	 * Examples:
	 *
	 *      superagent.types.json = 'application/json';
	 *
	 *      request.get('/agent')
	 *        .accept('json')
	 *        .end(callback);
	 *
	 *      request.get('/agent')
	 *        .accept('application/json')
	 *        .end(callback);
	 *
	 * @param {String} accept
	 * @return {Request} for chaining
	 * @api public
	 */

	Request.prototype.accept = function(type){
	  this.set('Accept', request.types[type] || type);
	  return this;
	};

	/**
	 * Set Authorization field value with `user` and `pass`.
	 *
	 * @param {String} user
	 * @param {String} pass
	 * @param {Object} options with 'type' property 'auto' or 'basic' (default 'basic')
	 * @return {Request} for chaining
	 * @api public
	 */

	Request.prototype.auth = function(user, pass, options){
	  if (!options) {
	    options = {
	      type: 'basic'
	    }
	  }

	  switch (options.type) {
	    case 'basic':
	      var str = btoa(user + ':' + pass);
	      this.set('Authorization', 'Basic ' + str);
	    break;

	    case 'auto':
	      this.username = user;
	      this.password = pass;
	    break;
	  }
	  return this;
	};

	/**
	* Add query-string `val`.
	*
	* Examples:
	*
	*   request.get('/shoes')
	*     .query('size=10')
	*     .query({ color: 'blue' })
	*
	* @param {Object|String} val
	* @return {Request} for chaining
	* @api public
	*/

	Request.prototype.query = function(val){
	  if ('string' != typeof val) val = serialize(val);
	  if (val) this._query.push(val);
	  return this;
	};

	/**
	 * Queue the given `file` as an attachment to the specified `field`,
	 * with optional `filename`.
	 *
	 * ``` js
	 * request.post('/upload')
	 *   .attach(new Blob(['<a id="a"><b id="b">hey!</b></a>'], { type: "text/html"}))
	 *   .end(callback);
	 * ```
	 *
	 * @param {String} field
	 * @param {Blob|File} file
	 * @param {String} filename
	 * @return {Request} for chaining
	 * @api public
	 */

	Request.prototype.attach = function(field, file, filename){
	  this._getFormData().append(field, file, filename || file.name);
	  return this;
	};

	Request.prototype._getFormData = function(){
	  if (!this._formData) {
	    this._formData = new root.FormData();
	  }
	  return this._formData;
	};

	/**
	 * Send `data` as the request body, defaulting the `.type()` to "json" when
	 * an object is given.
	 *
	 * Examples:
	 *
	 *       // manual json
	 *       request.post('/user')
	 *         .type('json')
	 *         .send('{"name":"tj"}')
	 *         .end(callback)
	 *
	 *       // auto json
	 *       request.post('/user')
	 *         .send({ name: 'tj' })
	 *         .end(callback)
	 *
	 *       // manual x-www-form-urlencoded
	 *       request.post('/user')
	 *         .type('form')
	 *         .send('name=tj')
	 *         .end(callback)
	 *
	 *       // auto x-www-form-urlencoded
	 *       request.post('/user')
	 *         .type('form')
	 *         .send({ name: 'tj' })
	 *         .end(callback)
	 *
	 *       // defaults to x-www-form-urlencoded
	  *      request.post('/user')
	  *        .send('name=tobi')
	  *        .send('species=ferret')
	  *        .end(callback)
	 *
	 * @param {String|Object} data
	 * @return {Request} for chaining
	 * @api public
	 */

	Request.prototype.send = function(data){
	  var obj = isObject(data);
	  var type = this._header['content-type'];

	  // merge
	  if (obj && isObject(this._data)) {
	    for (var key in data) {
	      this._data[key] = data[key];
	    }
	  } else if ('string' == typeof data) {
	    if (!type) this.type('form');
	    type = this._header['content-type'];
	    if ('application/x-www-form-urlencoded' == type) {
	      this._data = this._data
	        ? this._data + '&' + data
	        : data;
	    } else {
	      this._data = (this._data || '') + data;
	    }
	  } else {
	    this._data = data;
	  }

	  if (!obj || isHost(data)) return this;
	  if (!type) this.type('json');
	  return this;
	};

	/**
	 * @deprecated
	 */
	Response.prototype.parse = function serialize(fn){
	  if (root.console) {
	    console.warn("Client-side parse() method has been renamed to serialize(). This method is not compatible with superagent v2.0");
	  }
	  this.serialize(fn);
	  return this;
	};

	Response.prototype.serialize = function serialize(fn){
	  this._parser = fn;
	  return this;
	};

	/**
	 * Invoke the callback with `err` and `res`
	 * and handle arity check.
	 *
	 * @param {Error} err
	 * @param {Response} res
	 * @api private
	 */

	Request.prototype.callback = function(err, res){
	  var fn = this._callback;
	  this.clearTimeout();
	  fn(err, res);
	};

	/**
	 * Invoke callback with x-domain error.
	 *
	 * @api private
	 */

	Request.prototype.crossDomainError = function(){
	  var err = new Error('Request has been terminated\nPossible causes: the network is offline, Origin is not allowed by Access-Control-Allow-Origin, the page is being unloaded, etc.');
	  err.crossDomain = true;

	  err.status = this.status;
	  err.method = this.method;
	  err.url = this.url;

	  this.callback(err);
	};

	/**
	 * Invoke callback with timeout error.
	 *
	 * @api private
	 */

	Request.prototype.timeoutError = function(){
	  var timeout = this._timeout;
	  var err = new Error('timeout of ' + timeout + 'ms exceeded');
	  err.timeout = timeout;
	  this.callback(err);
	};

	/**
	 * Enable transmission of cookies with x-domain requests.
	 *
	 * Note that for this to work the origin must not be
	 * using "Access-Control-Allow-Origin" with a wildcard,
	 * and also must set "Access-Control-Allow-Credentials"
	 * to "true".
	 *
	 * @api public
	 */

	Request.prototype.withCredentials = function(){
	  this._withCredentials = true;
	  return this;
	};

	/**
	 * Initiate request, invoking callback `fn(res)`
	 * with an instanceof `Response`.
	 *
	 * @param {Function} fn
	 * @return {Request} for chaining
	 * @api public
	 */

	Request.prototype.end = function(fn){
	  var self = this;
	  var xhr = this.xhr = request.getXHR();
	  var query = this._query.join('&');
	  var timeout = this._timeout;
	  var data = this._formData || this._data;

	  // store callback
	  this._callback = fn || noop;

	  // state change
	  xhr.onreadystatechange = function(){
	    if (4 != xhr.readyState) return;

	    // In IE9, reads to any property (e.g. status) off of an aborted XHR will
	    // result in the error "Could not complete the operation due to error c00c023f"
	    var status;
	    try { status = xhr.status } catch(e) { status = 0; }

	    if (0 == status) {
	      if (self.timedout) return self.timeoutError();
	      if (self.aborted) return;
	      return self.crossDomainError();
	    }
	    self.emit('end');
	  };

	  // progress
	  var handleProgress = function(e){
	    if (e.total > 0) {
	      e.percent = e.loaded / e.total * 100;
	    }
	    e.direction = 'download';
	    self.emit('progress', e);
	  };
	  if (this.hasListeners('progress')) {
	    xhr.onprogress = handleProgress;
	  }
	  try {
	    if (xhr.upload && this.hasListeners('progress')) {
	      xhr.upload.onprogress = handleProgress;
	    }
	  } catch(e) {
	    // Accessing xhr.upload fails in IE from a web worker, so just pretend it doesn't exist.
	    // Reported here:
	    // https://connect.microsoft.com/IE/feedback/details/837245/xmlhttprequest-upload-throws-invalid-argument-when-used-from-web-worker-context
	  }

	  // timeout
	  if (timeout && !this._timer) {
	    this._timer = setTimeout(function(){
	      self.timedout = true;
	      self.abort();
	    }, timeout);
	  }

	  // querystring
	  if (query) {
	    query = request.serializeObject(query);
	    this.url += ~this.url.indexOf('?')
	      ? '&' + query
	      : '?' + query;
	  }

	  // initiate request
	  if (this.username && this.password) {
	    xhr.open(this.method, this.url, true, this.username, this.password);
	  } else {
	    xhr.open(this.method, this.url, true);
	  }

	  // CORS
	  if (this._withCredentials) xhr.withCredentials = true;

	  // body
	  if ('GET' != this.method && 'HEAD' != this.method && 'string' != typeof data && !isHost(data)) {
	    // serialize stuff
	    var contentType = this._header['content-type'];
	    var serialize = this._parser || request.serialize[contentType ? contentType.split(';')[0] : ''];
	    if (!serialize && isJSON(contentType)) serialize = request.serialize['application/json'];
	    if (serialize) data = serialize(data);
	  }

	  // set header fields
	  for (var field in this.header) {
	    if (null == this.header[field]) continue;
	    xhr.setRequestHeader(field, this.header[field]);
	  }

	  if (this._responseType) {
	    xhr.responseType = this._responseType;
	  }

	  // send stuff
	  this.emit('request', this);

	  // IE11 xhr.send(undefined) sends 'undefined' string as POST payload (instead of nothing)
	  // We need null here if data is undefined
	  xhr.send(typeof data !== 'undefined' ? data : null);
	  return this;
	};


	/**
	 * Expose `Request`.
	 */

	request.Request = Request;

	/**
	 * GET `url` with optional callback `fn(res)`.
	 *
	 * @param {String} url
	 * @param {Mixed|Function} data or fn
	 * @param {Function} fn
	 * @return {Request}
	 * @api public
	 */

	request.get = function(url, data, fn){
	  var req = request('GET', url);
	  if ('function' == typeof data) fn = data, data = null;
	  if (data) req.query(data);
	  if (fn) req.end(fn);
	  return req;
	};

	/**
	 * HEAD `url` with optional callback `fn(res)`.
	 *
	 * @param {String} url
	 * @param {Mixed|Function} data or fn
	 * @param {Function} fn
	 * @return {Request}
	 * @api public
	 */

	request.head = function(url, data, fn){
	  var req = request('HEAD', url);
	  if ('function' == typeof data) fn = data, data = null;
	  if (data) req.send(data);
	  if (fn) req.end(fn);
	  return req;
	};

	/**
	 * DELETE `url` with optional callback `fn(res)`.
	 *
	 * @param {String} url
	 * @param {Function} fn
	 * @return {Request}
	 * @api public
	 */

	function del(url, fn){
	  var req = request('DELETE', url);
	  if (fn) req.end(fn);
	  return req;
	};

	request['del'] = del;
	request['delete'] = del;

	/**
	 * PATCH `url` with optional `data` and callback `fn(res)`.
	 *
	 * @param {String} url
	 * @param {Mixed} data
	 * @param {Function} fn
	 * @return {Request}
	 * @api public
	 */

	request.patch = function(url, data, fn){
	  var req = request('PATCH', url);
	  if ('function' == typeof data) fn = data, data = null;
	  if (data) req.send(data);
	  if (fn) req.end(fn);
	  return req;
	};

	/**
	 * POST `url` with optional `data` and callback `fn(res)`.
	 *
	 * @param {String} url
	 * @param {Mixed} data
	 * @param {Function} fn
	 * @return {Request}
	 * @api public
	 */

	request.post = function(url, data, fn){
	  var req = request('POST', url);
	  if ('function' == typeof data) fn = data, data = null;
	  if (data) req.send(data);
	  if (fn) req.end(fn);
	  return req;
	};

	/**
	 * PUT `url` with optional `data` and callback `fn(res)`.
	 *
	 * @param {String} url
	 * @param {Mixed|Function} data or fn
	 * @param {Function} fn
	 * @return {Request}
	 * @api public
	 */

	request.put = function(url, data, fn){
	  var req = request('PUT', url);
	  if ('function' == typeof data) fn = data, data = null;
	  if (data) req.send(data);
	  if (fn) req.end(fn);
	  return req;
	};


/***/ },
/* 29 */
/***/ function(module, exports, __webpack_require__) {

	
	/**
	 * Expose `Emitter`.
	 */

	if (true) {
	  module.exports = Emitter;
	}

	/**
	 * Initialize a new `Emitter`.
	 *
	 * @api public
	 */

	function Emitter(obj) {
	  if (obj) return mixin(obj);
	};

	/**
	 * Mixin the emitter properties.
	 *
	 * @param {Object} obj
	 * @return {Object}
	 * @api private
	 */

	function mixin(obj) {
	  for (var key in Emitter.prototype) {
	    obj[key] = Emitter.prototype[key];
	  }
	  return obj;
	}

	/**
	 * Listen on the given `event` with `fn`.
	 *
	 * @param {String} event
	 * @param {Function} fn
	 * @return {Emitter}
	 * @api public
	 */

	Emitter.prototype.on =
	Emitter.prototype.addEventListener = function(event, fn){
	  this._callbacks = this._callbacks || {};
	  (this._callbacks['$' + event] = this._callbacks['$' + event] || [])
	    .push(fn);
	  return this;
	};

	/**
	 * Adds an `event` listener that will be invoked a single
	 * time then automatically removed.
	 *
	 * @param {String} event
	 * @param {Function} fn
	 * @return {Emitter}
	 * @api public
	 */

	Emitter.prototype.once = function(event, fn){
	  function on() {
	    this.off(event, on);
	    fn.apply(this, arguments);
	  }

	  on.fn = fn;
	  this.on(event, on);
	  return this;
	};

	/**
	 * Remove the given callback for `event` or all
	 * registered callbacks.
	 *
	 * @param {String} event
	 * @param {Function} fn
	 * @return {Emitter}
	 * @api public
	 */

	Emitter.prototype.off =
	Emitter.prototype.removeListener =
	Emitter.prototype.removeAllListeners =
	Emitter.prototype.removeEventListener = function(event, fn){
	  this._callbacks = this._callbacks || {};

	  // all
	  if (0 == arguments.length) {
	    this._callbacks = {};
	    return this;
	  }

	  // specific event
	  var callbacks = this._callbacks['$' + event];
	  if (!callbacks) return this;

	  // remove all handlers
	  if (1 == arguments.length) {
	    delete this._callbacks['$' + event];
	    return this;
	  }

	  // remove specific handler
	  var cb;
	  for (var i = 0; i < callbacks.length; i++) {
	    cb = callbacks[i];
	    if (cb === fn || cb.fn === fn) {
	      callbacks.splice(i, 1);
	      break;
	    }
	  }
	  return this;
	};

	/**
	 * Emit `event` with the given args.
	 *
	 * @param {String} event
	 * @param {Mixed} ...
	 * @return {Emitter}
	 */

	Emitter.prototype.emit = function(event){
	  this._callbacks = this._callbacks || {};
	  var args = [].slice.call(arguments, 1)
	    , callbacks = this._callbacks['$' + event];

	  if (callbacks) {
	    callbacks = callbacks.slice(0);
	    for (var i = 0, len = callbacks.length; i < len; ++i) {
	      callbacks[i].apply(this, args);
	    }
	  }

	  return this;
	};

	/**
	 * Return array of callbacks for `event`.
	 *
	 * @param {String} event
	 * @return {Array}
	 * @api public
	 */

	Emitter.prototype.listeners = function(event){
	  this._callbacks = this._callbacks || {};
	  return this._callbacks['$' + event] || [];
	};

	/**
	 * Check if this emitter has `event` handlers.
	 *
	 * @param {String} event
	 * @return {Boolean}
	 * @api public
	 */

	Emitter.prototype.hasListeners = function(event){
	  return !! this.listeners(event).length;
	};


/***/ },
/* 30 */
/***/ function(module, exports) {

	
	/**
	 * Reduce `arr` with `fn`.
	 *
	 * @param {Array} arr
	 * @param {Function} fn
	 * @param {Mixed} initial
	 *
	 * TODO: combatible error handling?
	 */

	module.exports = function(arr, fn, initial){  
	  var idx = 0;
	  var len = arr.length;
	  var curr = arguments.length == 3
	    ? initial
	    : arr[idx++];

	  while (idx < len) {
	    curr = fn.call(null, curr, arr[idx], ++idx, arr);
	  }
	  
	  return curr;
	};

/***/ },
/* 31 */
/***/ function(module, exports, __webpack_require__) {

	/**
	 * Module of mixed-in functions shared between node and client code
	 */
	var isObject = __webpack_require__(32);

	/**
	 * Clear previous timeout.
	 *
	 * @return {Request} for chaining
	 * @api public
	 */

	exports.clearTimeout = function _clearTimeout(){
	  this._timeout = 0;
	  clearTimeout(this._timer);
	  return this;
	};

	/**
	 * Force given parser
	 *
	 * Sets the body parser no matter type.
	 *
	 * @param {Function}
	 * @api public
	 */

	exports.parse = function parse(fn){
	  this._parser = fn;
	  return this;
	};

	/**
	 * Set timeout to `ms`.
	 *
	 * @param {Number} ms
	 * @return {Request} for chaining
	 * @api public
	 */

	exports.timeout = function timeout(ms){
	  this._timeout = ms;
	  return this;
	};

	/**
	 * Faux promise support
	 *
	 * @param {Function} fulfill
	 * @param {Function} reject
	 * @return {Request}
	 */

	exports.then = function then(fulfill, reject) {
	  return this.end(function(err, res) {
	    err ? reject(err) : fulfill(res);
	  });
	}

	/**
	 * Allow for extension
	 */

	exports.use = function use(fn) {
	  fn(this);
	  return this;
	}


	/**
	 * Get request header `field`.
	 * Case-insensitive.
	 *
	 * @param {String} field
	 * @return {String}
	 * @api public
	 */

	exports.get = function(field){
	  return this._header[field.toLowerCase()];
	};

	/**
	 * Get case-insensitive header `field` value.
	 * This is a deprecated internal API. Use `.get(field)` instead.
	 *
	 * (getHeader is no longer used internally by the superagent code base)
	 *
	 * @param {String} field
	 * @return {String}
	 * @api private
	 * @deprecated
	 */

	exports.getHeader = exports.get;

	/**
	 * Set header `field` to `val`, or multiple fields with one object.
	 * Case-insensitive.
	 *
	 * Examples:
	 *
	 *      req.get('/')
	 *        .set('Accept', 'application/json')
	 *        .set('X-API-Key', 'foobar')
	 *        .end(callback);
	 *
	 *      req.get('/')
	 *        .set({ Accept: 'application/json', 'X-API-Key': 'foobar' })
	 *        .end(callback);
	 *
	 * @param {String|Object} field
	 * @param {String} val
	 * @return {Request} for chaining
	 * @api public
	 */

	exports.set = function(field, val){
	  if (isObject(field)) {
	    for (var key in field) {
	      this.set(key, field[key]);
	    }
	    return this;
	  }
	  this._header[field.toLowerCase()] = val;
	  this.header[field] = val;
	  return this;
	};

	/**
	 * Remove header `field`.
	 * Case-insensitive.
	 *
	 * Example:
	 *
	 *      req.get('/')
	 *        .unset('User-Agent')
	 *        .end(callback);
	 *
	 * @param {String} field
	 */
	exports.unset = function(field){
	  delete this._header[field.toLowerCase()];
	  delete this.header[field];
	  return this;
	};

	/**
	 * Write the field `name` and `val` for "multipart/form-data"
	 * request bodies.
	 *
	 * ``` js
	 * request.post('/upload')
	 *   .field('foo', 'bar')
	 *   .end(callback);
	 * ```
	 *
	 * @param {String} name
	 * @param {String|Blob|File|Buffer|fs.ReadStream} val
	 * @return {Request} for chaining
	 * @api public
	 */
	exports.field = function(name, val) {
	  this._getFormData().append(name, val);
	  return this;
	};


/***/ },
/* 32 */
/***/ function(module, exports) {

	/**
	 * Check if `obj` is an object.
	 *
	 * @param {Object} obj
	 * @return {Boolean}
	 * @api private
	 */

	function isObject(obj) {
	  return null != obj && 'object' == typeof obj;
	}

	module.exports = isObject;


/***/ },
/* 33 */
/***/ function(module, exports) {

	// The node and browser modules expose versions of this with the
	// appropriate constructor function bound as first argument
	/**
	 * Issue a request:
	 *
	 * Examples:
	 *
	 *    request('GET', '/users').end(callback)
	 *    request('/users').end(callback)
	 *    request('/users', callback)
	 *
	 * @param {String} method
	 * @param {String|Function} url or callback
	 * @return {Request}
	 * @api public
	 */

	function request(RequestConstructor, method, url) {
	  // callback
	  if ('function' == typeof url) {
	    return new RequestConstructor('GET', method).end(url);
	  }

	  // url first
	  if (2 == arguments.length) {
	    return new RequestConstructor('GET', method);
	  }

	  return new RequestConstructor(method, url);
	}

	module.exports = request;


/***/ },
/* 34 */
/***/ function(module, exports) {

	"use strict";

	var config = {};

	module.exports = {
	  get config() {
	    return Object.assign({}, config);
	  },
	  set config(newConfig) {
	    config = Object.assign({}, newConfig);
	  }
	};

/***/ },
/* 35 */
/***/ function(module, exports) {

	'use strict';

	var LAYER_COUNT = 4;
	var CssClasses_ = {
	  CONTAINER: 'spinner',
	  LAYER: 'spinner-layer',
	  CIRCLE_CLIPPER: 'spinner-circle-clipper',
	  CIRCLE: 'spinner-circle',
	  GAP_PATCH: 'spinner-gap-patch',
	  LEFT: 'spinner-left',
	  RIGHT: 'spinner-right',
	  IS_ACTIVE: 'is-active'
	};

	var CircleOwner = React.createClass({
	  displayName: 'CircleOwner',
	  render: function render() {
	    return React.createElement(
	      'div',
	      { className: this.props.classNames.join(' ') },
	      React.createElement('div', { className: CssClasses_.CIRCLE })
	    );
	  }
	});

	var Layer = React.createClass({
	  displayName: 'CircleOwner',
	  render: function render() {
	    return React.createElement(
	      'div',
	      { className: [CssClasses_.LAYER, CssClasses_.LAYER + '-' + this.props.index].join(' ') },
	      React.createElement(CircleOwner, { classNames: [CssClasses_.CIRCLE_CLIPPER, CssClasses_.LEFT] }),
	      React.createElement(CircleOwner, { classNames: [CssClasses_.GAP_PATCH] }),
	      React.createElement(CircleOwner, { classNames: [CssClasses_.CIRCLE_CLIPPER, CssClasses_.RIGHT] })
	    );
	  }
	});

	var Spinner = React.createClass({
	  displayName: 'Spinner',
	  propTypes: {
	    active: React.PropTypes.bool
	  },
	  handleRenderRef: function handleRenderRef(domReference) {
	    if (domReference !== null) {
	      domReference._mdl = {
	        start: function start() {
	          return domReference.classList.add(CssClasses_.IS_ACTIVE);
	        },
	        stop: function stop() {
	          return domReference.classList.remove(CssClasses_.IS_ACTIVE);
	        }
	      };
	    }
	  },
	  render: function render() {
	    var layers = [];
	    for (var i = 1; i <= LAYER_COUNT; i++) {
	      layers.push(React.createElement(Layer, { key: i, index: i }));
	    }
	    var classNames = [CssClasses_.CONTAINER];
	    if (this.props.active) {
	      classNames.push(CssClasses_.IS_ACTIVE);
	    }
	    return React.createElement(
	      'div',
	      { ref: this.handleRenderRef, className: classNames.join(' ') },
	      layers
	    );
	  }
	});
	module.exports = Spinner;

/***/ },
/* 36 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var Constants = __webpack_require__(9);
	var Group = __webpack_require__(37);
	var Item = __webpack_require__(41);

	var mapItems = function mapItems(item) {
	  var _this = this;

	  var itemPath = [this.props.path, item.codigo, Constants.PATH_SEPARATOR].join('');

	  var childrenGroups = (item.groupItems || []).map(function (group) {
	    var groupsProps = Object.assign({}, group);
	    groupsProps.key = [item.codigo || '', group.group.codigo].join(':');
	    groupsProps.folded = _this.state.selected !== item.codigo;
	    groupsProps.path = itemPath || Constants.PATH_SEPARATOR;
	    groupsProps.groupToolBar = _this.props.groupToolBar;
	    groupsProps.itemToolBar = _this.props.itemToolBar;
	    return React.createElement(Groups, groupsProps);
	  });

	  return React.createElement(
	    Item,
	    { key: itemPath, codigo: item.codigo, descricao: item.descricao, hasChildren: childrenGroups.length > 0,
	      itemToolBar: this.props.itemToolBar, path: itemPath, onSelect: this.handleItemSelect,
	      selected: this.state.selected === item.codigo },
	    childrenGroups
	  );
	};

	var Groups = React.createClass({
	  'displayName': 'Groups',
	  propTypes: {
	    folded: React.PropTypes.bool,
	    group: React.PropTypes.object.isRequired,
	    items: React.PropTypes.array.isRequired,
	    path: React.PropTypes.string.isRequired,
	    groupToolBar: React.PropTypes.arrayOf(React.PropTypes.shape({
	      icon: React.PropTypes.string,
	      title: React.PropTypes.string.isRequired,
	      onSelect: React.PropTypes.func.isRequired
	    })),
	    itemToolBar: React.PropTypes.arrayOf(React.PropTypes.shape({
	      icon: React.PropTypes.string,
	      title: React.PropTypes.string.isRequired,
	      onSelect: React.PropTypes.func.isRequired
	    }))
	  },
	  'getInitialState': function getInitialState() {
	    return {
	      'selected': ''
	    };
	  },
	  'selectItem': function selectItem(item) {
	    var state = Object.assign({}, this.state);
	    state.selected = state.selected === item ? '' : item;
	    this.setState(state);
	  },
	  'handleItemSelect': function handleItemSelect(evt, item) {
	    this.selectItem(item);
	  },
	  render: function render() {
	    var classesLista = [Constants.CSS.LIST];

	    if (this.props.folded) {
	      classesLista.push(Constants.CSS.LIST_FOLDED);
	    }
	    var groupProps = Object.assign({}, this.props.group);
	    groupProps.path = this.props.path;
	    groupProps.groupToolBar = this.props.groupToolBar;

	    var items = this.props.items.map(mapItems.bind(this));

	    return React.createElement(
	      'ul',
	      { className: classesLista.join(' ') },
	      React.createElement(Group, groupProps),
	      React.createElement(
	        'div',
	        { className: Constants.CSS.LIST_CONTENT },
	        items
	      )
	    );
	  }
	});
	module.exports = Groups;

/***/ },
/* 37 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var Constants = __webpack_require__(9);
	var ToolBar = __webpack_require__(38);
	var ToolBarItem = __webpack_require__(40);

	var mapToolBarItem = function mapToolBarItem(item, i) {
	  var params = [[this.props.path, this.props.codigo, Constants.PATH_SEPARATOR].join('')];
	  return React.createElement(ToolBarItem, { key: [this.props.codigo, item.title].join(''),
	    icon: item.icon, title: item.title, params: params,
	    onSelect: item.onSelect });
	};

	module.exports = React.createClass({
	  'displayName': 'Group',
	  propTypes: {
	    codigo: React.PropTypes.string.isRequired,
	    descricao: React.PropTypes.string.isRequired,
	    path: React.PropTypes.string.isRequired,
	    groupToolBar: React.PropTypes.arrayOf(React.PropTypes.shape({
	      icon: React.PropTypes.string,
	      title: React.PropTypes.string.isRequired,
	      onSelect: React.PropTypes.func.isRequired
	    }))
	  },
	  updateStyle: function updateStyle() {
	    if (this.domElements && this.domElements.groupContent && this.domElements.group) {
	      var groupContentStyle = getComputedStyle(this.domElements.groupContent);
	      this.domElements.group.style.minHeight = groupContentStyle.width;
	      this.domElements.group.style.minWidth = groupContentStyle.height;
	    }
	  },
	  referenceGroup: function referenceGroup(domElement) {
	    this.domElements = Object.assign({}, this.domElements, { group: domElement });
	    this.updateStyle();
	  },
	  referenceGroupContent: function referenceGroupContent(domElement) {
	    this.domElements = Object.assign({}, this.domElements, { groupContent: domElement });
	    this.updateStyle();
	  },
	  render: function render() {
	    var toolBarItems = (this.props.groupToolBar || []).map(mapToolBarItem.bind(this));
	    return React.createElement(
	      'div',
	      { className: Constants.CSS.GROUP, ref: this.referenceGroup },
	      React.createElement(
	        'div',
	        { className: Constants.CSS.GROUP_CONTENT, ref: this.referenceGroupContent },
	        React.createElement(
	          'label',
	          { className: Constants.CSS.GROUP_LABEL },
	          this.props.descricao
	        ),
	        React.createElement(
	          ToolBar,
	          { codigo: this.props.codigo },
	          toolBarItems
	        )
	      )
	    );
	  }
	});

/***/ },
/* 38 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var Constants = __webpack_require__(39);

	module.exports = React.createClass({
	  'displayName': 'Toolbar',
	  propTypes: {
	    codigo: React.PropTypes.string.isRequired,
	    children: React.PropTypes.arrayOf(React.PropTypes.element)
	  },
	  render: function render() {
	    return React.createElement(
	      'span',
	      { className: Constants.CSS.TOOL_BAR },
	      React.Children.toArray(this.props.children)
	    );
	  }
	});

/***/ },
/* 39 */
/***/ function(module, exports) {

	'use strict';

	module.exports = {
	  CSS: {
	    TOOL_BAR: 'ifx-toolbar',
	    TOOL_BAR_ITM: 'ifx-toolbar-itm'
	  }
	};

/***/ },
/* 40 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var Constants = __webpack_require__(39);

	module.exports = React.createClass({
	  'displayName': 'ToolBarItem',
	  propTypes: {
	    icon: React.PropTypes.string,
	    params: React.PropTypes.array.isRequired,
	    title: React.PropTypes.string.isRequired,
	    onSelect: React.PropTypes.func.isRequired
	  },
	  handleItemSelect: function handleItemSelect(event) {
	    this.props.onSelect.apply(this, this.props.params);
	    event.preventDefault();
	  },
	  render: function render() {
	    return React.createElement('img', { src: this.props.icon,
	      className: Constants.CSS.TOOL_BAR_ITM,
	      title: this.props.title,
	      onClick: this.handleItemSelect });
	  }
	});

/***/ },
/* 41 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var Constants = __webpack_require__(9);
	var ToolBar = __webpack_require__(38);
	var ToolBarItem = __webpack_require__(40);

	var mapToolBarItem = function mapToolBarItem(item, i) {
	  var params = [this.props.path];
	  return React.createElement(ToolBarItem, { key: [this.props.codigo, item.title].join(''),
	    icon: item.icon, title: item.title, params: params,
	    onSelect: item.onSelect });
	};

	var isLeafFilter = function isLeafFilter(cond) {
	  return !cond.hasOwnProperty('isLeaf') || cond.isLeaf && !this.props.hasChildren || !cond.isLeaf && this.props.hasChildren;
	};

	var filterToolBarItems = function filterToolBarItems(item) {
	  var result = true;
	  var conditions = item.conditions || [];
	  for (var i = 0, l = conditions.length; i < l; i++) {
	    var condition = conditions[i] || {};
	    result = result && isLeafFilter.bind(this)(condition);
	    if (!result) {
	      break;
	    }
	  }
	  return result;
	};

	module.exports = React.createClass({
	  'displayName': 'Item',
	  getInitialState: function getInitialState() {
	    return {
	      showCreateForm: false,
	      showEditForm: false
	    };
	  },
	  propTypes: {
	    codigo: React.PropTypes.string.isRequired,
	    descricao: React.PropTypes.string.isRequired,
	    path: React.PropTypes.string.isRequired,
	    selected: React.PropTypes.bool,
	    hasChildren: React.PropTypes.bool.isRequired,
	    onSelect: React.PropTypes.func,
	    itemToolBar: React.PropTypes.arrayOf(React.PropTypes.shape({
	      icon: React.PropTypes.string,
	      title: React.PropTypes.string.isRequired,
	      onSelect: React.PropTypes.func.isRequired
	    }))
	  },
	  handleLabelClick: function handleLabelClick(event) {
	    if (this.props.hasChildren) {
	      this.props.onSelect(event, this.props.codigo);
	    }
	    event.preventDefault();
	  },
	  handleShowCreateForm: function handleShowCreateForm(event) {
	    var state = Object.assign({}, this.state);
	    state.showCreateForm = !state.showCreateForm;
	    state.showEditForm = false;
	    this.setState(state);
	  },
	  handleShowEditForm: function handleShowEditForm(event) {
	    var state = Object.assign({}, this.state);
	    state.showCreateForm = false;
	    state.showEditForm = !state.showEditForm;
	    this.setState(state);
	  },
	  render: function render() {
	    var listItemClasses = [Constants.CSS.ITEM];
	    if (this.props.selected) {
	      listItemClasses.push(Constants.CSS.ITEM_SELECTED);
	    }
	    if (this.props.hasChildren) {
	      listItemClasses.push(Constants.CSS.ITEM_HAS_CHILDREN);
	    }

	    var toolBarItems = (this.props.itemToolBar || []).filter(filterToolBarItems.bind(this)).map(mapToolBarItem.bind(this));
	    return React.createElement(
	      'li',
	      { className: listItemClasses.join(' ') },
	      React.createElement(
	        'div',
	        { className: Constants.CSS.ITEM_CONTENT },
	        React.createElement(
	          'label',
	          { className: Constants.CSS.ITEM_LABEL, onMouseUp: this.handleLabelClick },
	          this.props.descricao
	        ),
	        React.createElement(
	          ToolBar,
	          { codigo: this.props.codigo },
	          toolBarItems
	        )
	      ),
	      React.Children.toArray(this.props.children)
	    );
	  }
	});

/***/ }
/******/ ])
});
;