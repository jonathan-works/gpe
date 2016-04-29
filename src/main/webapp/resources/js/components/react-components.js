(function(f){if(typeof exports==="object"&&typeof module!=="undefined"){module.exports=f()}else if(typeof define==="function"&&define.amd){define([],f)}else{var g;if(typeof window!=="undefined"){g=window}else if(typeof global!=="undefined"){g=global}else if(typeof self!=="undefined"){g=self}else{g=this}g.reactComponents = f()}})(function(){var define,module,exports;return (function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);var f=new Error("Cannot find module '"+o+"'");throw f.code="MODULE_NOT_FOUND",f}var l=n[o]={exports:{}};t[o][0].call(l.exports,function(e){var n=t[o][1][e];return s(n?n:e)},l,l.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
module.exports={
    CSS_CLASSES:{
        ITEM_LABEL:'ifx-menu-itm-lbl',
        ITEM_LABEL_ICON:'ifx-menu-itm-lbl-icon',
        ITEM_LABEL_TEXT:'ifx-menu-itm-lbl-text'
    }
};

},{}],2:[function(require,module,exports){
let MenuFilter = React.createClass({
    handleChange: function (event) {
        this.props.onFilter(event.target.value);
    },
    render: function () {
        return React.createElement('input', {
            value: this.props.labelFilter,
            placeholder: this.props.labelFilterPlaceholder || 'Your query here...',
            className: 'ifx-menu-filter',
            onChange: this.handleChange,
            autoFocus:true
        });
    }
});
module.exports=MenuFilter;
},{}],3:[function(require,module,exports){
let MenuLink = require('./MenuLink');
let MenuItem = React.createClass({

  handleClick:function(event){
    this.props.onSelect(this.props.label);
  },

  render: function() {
    var classes=['ifx-menu-itm'];
    if (this.props.selected){
      classes.push('ifx-menu-itm-sel');
    }

    var props = Object.assign({}, this.props);

    let submenu;

    if ((props.items || []).length > 0){
      classes.push('ifx-menu-itm-has-children');
      props.onClick=this.handleClick;
      submenu = this.props.submenu;
    }

    return React.createElement('li', {
        className: classes.join(' ')
      }, React.createElement(MenuLink, props), submenu);
  }
});
module.exports=MenuItem;
},{"./MenuLink":4}],4:[function(require,module,exports){
let MenuText = require('./MenuText');
let MenuLink = React.createClass({
  getInitialState: function() {
    return {
      selected: false
    };
  },
  handleClick:function(event){
    this.setState({selected:true});
    (this.props.onClick||function(){}).apply(this, [event, this.props.url||this.props.value]);
  },
  render:function(){
    var linkClasses=['ifx-menu-itm-lnk'];
    if (this.state.selected){
      linkClasses.push('ifx-menu-itm-lnk-sel');
    }
    return React.createElement('a', {
      className: linkClasses.join(' '),
      href: this.props.url,
      title: this.props.label,
      onClick: this.handleClick
    }, React.createElement(MenuText,this.props));
  }
});
module.exports=MenuLink;
},{"./MenuText":5}],5:[function(require,module,exports){
let MenuConstants = require('./MenuConstants');
const LeftIconText = React.createClass({
    render:function(){
        var labelProperties = {
            className:MenuConstants.CSS_CLASSES.ITEM_LABEL
        };
        if (this.props.icon){
            return React.createElement('span', labelProperties,
                React.createElement('img',{
                    className:MenuConstants.CSS_CLASSES.ITEM_LABEL_ICON,
                    title:this.props.label,
                    src:this.props.icon
                }), this.props.hideLabel ? undefined: React.createElement('span', {'className':MenuConstants.CSS_CLASSES.ITEM_LABEL_TEXT}, this.props.label)
            );
        } else {
            return React.createElement('span', labelProperties, this.props.hideLabel ? undefined: React.createElement('span', {'className':MenuConstants.CSS_CLASSES.ITEM_LABEL_TEXT}, this.props.label));
        }
    }
});
const RightIconText = React.createClass({
    render:function(){
        var labelProperties = {
            className:MenuConstants.CSS_CLASSES.ITEM_LABEL
        };
        if (this.props.icon){
            return React.createElement('span', labelProperties,
                this.props.hideLabel ? undefined: React.createElement('span', {'className':MenuConstants.CSS_CLASSES.ITEM_LABEL_TEXT}, this.props.label),
                React.createElement('img',{
                    className:MenuConstants.CSS_CLASSES.ITEM_LABEL_ICON,
                    title:this.props.label,
                    src:this.props.icon
                })
            );
        } else {
            return React.createElement('span', labelProperties, this.props.hideLabel ? undefined: React.createElement('span', {'className':MenuConstants.CSS_CLASSES.ITEM_LABEL_TEXT}, this.props.label));
        }
    }
});

let MenuText = React.createClass({
    render:function(){
        if (/left/.test((this.props['icon-align']||'left').toLowerCase())){
            return React.createElement(LeftIconText, this.props);
        } else {
            return React.createElement(RightIconText, this.props);
        }
    }
});
module.exports=MenuText;
},{"./MenuConstants":1}],6:[function(require,module,exports){
let UnorderedMenu = require('./UnorderedMenu');

const NavMenu = React.createClass({
  getDefaultProps:function(){
      return {
          "level":0,
          "showChildren":false,
          "labelFilter":""
      };
  },
  render:function(){
    return React.createElement('nav', {
      className: 'ifx-menu-nav'
    }, React.createElement(UnorderedMenu, Object.assign({},this.props))
    );
  }
});

const MenuContent = React.createClass({
  renderContent:function(container){
    let content = document.getElementById(this.props.content);
    if (content){
      container.appendChild(content);
    } else {
      let nodeList = document.querySelectorAll(this.props.content);
      for(let i=0,l=nodeList.length; i<l; i++){
        container.appendChild(nodeList.item(i));
      }
    }
  },
  render:function(){
    return React.createElement('div',{
      ref:this.renderContent,
      className:'ifx-menu-content'
    });
  }
});

let NavigationMenu = React.createClass({
  render: function() {
    return React.createElement('div',{
      className:'ifx-menu-container'
    }, React.createElement(MenuContent,{
      content:this.props.content
    }), React.createElement('div',{
      className:'ifx-navigation-menu'
    }, React.createElement(NavMenu,this.props.navigationMenuItems)), 
    React.createElement('div',{
      className:'ifx-top-menu'
    }, React.createElement('div',{className:'ifx-navigation-menu-overlay'}),
    React.createElement(NavMenu,this.props.topMenuItens)));
  }
});

module.exports=NavigationMenu;
},{"./UnorderedMenu":7}],7:[function(require,module,exports){
let MenuItem=require('./MenuItem');
let MenuFilter=require('./MenuFilter');

const hasChildWithContent = function(menuItem, content) {
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

const BaseMenuMixin = {
  handleSelect:function(label){
    this.unorderedMenu.addEventListener('mouseleave', this.handleMouseLeave);
    this.unorderedMenu.addEventListener('mouseenter', this.handleMouseEnter);
    var state = Object.assign({}, this.state);
    if (state.selected === label){
      state.selected = '';
    } else {
      state.selected = label || '';
    }
    this.setState(state);
  },
  handleMouseLeave:function(event){
    this.state.hideTimer = setTimeout(()=>{
      this.handleSelect();
      clearTimeout(this.state.hideTimer);
    }, 1000);
  },
  handleMouseEnter:function(event){
    clearTimeout(this.state.hideTimer);
  },
  getFilteredItems:function(labelFilter){
    var level = this.props.level || 0;
    return (this.props.items || []).filter((itm) => {
      return hasChildWithContent(itm, labelFilter);
    }).map((itm) => {
      var resultItem = Object.assign({}, itm);
      resultItem.key = resultItem.label;
      resultItem.level = level;
      resultItem.labelFilter = labelFilter||'';
      resultItem.onSelect = this.handleSelect;
      resultItem.selected = (this.state.selected === resultItem.label || resultItem.labelFilter.trim() !== '');
      if ((itm.items||[]).length > 0){
        resultItem.submenu = React.createElement(UnorderedMenu,{
          "items":resultItem.items,
          "level":level+1,
          "showFilter":resultItem.showFilter,
          "labelFilter":labelFilter
        });
      }
      return React.createElement(MenuItem, resultItem);
    });
  }
};

const FilterMenu = React.createClass(Object.assign(BaseMenuMixin, {
  getInitialState: function() {
    return {'selected':'', 'labelFilter':'', 'filterId':Date.now.toString(36)};
  },
  render:function(){
    return React.createElement('ul', {
      className: 'ifx-menu',
      style:this.props.style,
      ref:(ref)=>this.unorderedMenu=ref
    }, React.createElement(MenuFilter,{
        key:this.state.filterId,
        labelFilter:this.state.labelFilter,
        onFilter:this.handleLabelFilter
    }), this.getFilteredItems(this.state.labelFilter));
  },
  handleLabelFilter:function(labelFilter){
    var state = Object.assign({}, this.state);
    state.labelFilter = labelFilter;
    this.setState(state);
  }
}));

var NoFilterMenu=React.createClass(Object.assign(BaseMenuMixin, {
  render:function(){
    return React.createElement('ul', {
      className: 'ifx-menu',
      style:this.props.style,
      ref:(ref)=>this.unorderedMenu=ref
    }, this.getFilteredItems(this.props.labelFilter));
  }
}));

const UnorderedMenu = React.createClass(Object.assign(BaseMenuMixin, {
  render: function() {
    var menuProps = Object.assign({},this.props);
    menuProps.style={
      // 'maxHeight':(innerHeight - 13*parseInt(0+/\d*/.exec(getComputedStyle(document.body)['font-size'])[0]))
    };
    if (menuProps.showFilter || menuProps.level === 1){
      return React.createElement(FilterMenu, Object.assign({}, menuProps));
    }
    menuProps.onMouseLeave=this.handleMouseLeave;
    menuProps.onMouseEnter=this.handleMouseEnter;
    return React.createElement(NoFilterMenu, Object.assign({}, menuProps));
  }
}));
module.exports=UnorderedMenu;
},{"./MenuFilter":2,"./MenuItem":3}],8:[function(require,module,exports){
module.exports={
    NavigationMenu:require('./NavigationMenu')
};
},{"./NavigationMenu":6}]},{},[8])(8)
});