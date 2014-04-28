function NodeArea(cid) {
	if ($('#nodes').length == 0) {
		var map = $('<map></map>');
		map.attr({'id': 'nodes', 'name': 'nodes'});
		$('body').append(map);
	}
	
	this.map = $('#nodes');
	this.nodes = [];
	this.cid = cid;

	this.map.empty();
	
	function createLink(nodeIndex, cid) {
		return "?tab=nodesTab&cid=" + cid + "&node=" + nodeIndex;
	}
	
	this.addToMap = function(id, coords, title, vars, cond) {
		var area = $('<area></area>');
		area.attr({
			id: '_' + id, 
			coords: coords,
			href: 'javascript:window.location.href = "' + createLink(id, this.cid) + '"',
			shape: 'rect',
			areaName:title,
		});
		$(area).hover(this.mouseover, this.mouseout);
		area[0].areaName = title;
		area[0].vars = vars;
		area[0].cond = cond;
		
		this.map.append(area);
	};
	
	this.mouseover = function(event) {
		var d = $('#divDetail');
		if (d.hasClass("hidden")) {
			d.removeClass("hidden");
		}

		d.empty();
		var area = $(event.target);
		d.append($("<div class='graphHint-title'>" + area[0].areaName + "</div>"));

		var inVars = [];
		var outVars = [];
		
		_.each(area[0].vars, function(v) {
			if (v.readonly == 'true') {
				inVars[inVars.length] = v;
			} else {
				outVars[outVars.length] = v;
			}
		});
		
		if (inVars.length > 0) {
			d.append('<br/>Entrada:');
		}
		
		var ulIn = $('<ul style:"list-style:circle;margin:0"></ul>'); 
		d.append(ulIn);
		
		if (outVars.length > 0) {
			var saida = "Sa\u00EDda";
			d.append(saida + ':');
		}
		
		var ulOut = $('<ul style:"margin:0"></ul>'); 
		d.append(ulOut);
		
		_.each(inVars, function(v) {
			ulIn.append($('<li>' + v.name + ' (' + v.type + ')' + '</li>'));
		});
		
		_.each(outVars, function(v) {
			ulOut.append($('<li>' + v.name + ' (' + v.type + ')' + '</li>'));
		});
		
		if (area[0].cond != null){
			var condicao = "Condi\u00E7\u00E3o";
			d.append($('<p>' + condicao + ': ' + area[0].cond + '</p>'));
		}
		var coords = area.attr("coords").split(",");
		var left = coords[0]-90;
		var top = coords[1]-(-106);
		

        d.css({
          top:top,
          left:left
        });
	};

	this.mouseout = function(event) {
	  var d = $("#divDetail");
      if (!d.hasClass("hidden")) {
          d.addClass("hidden");
      }
	};
}