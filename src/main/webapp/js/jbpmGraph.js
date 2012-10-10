function showDetail(event) {
	divDetail = $('divDetail');
	p = Event.pointer(event);
	area = Event.element(event);
	coords = area.coords.split(',');
//	divDetail.style.left= p.x + 'px';
//	divDetail.style.top= p.y + 'px';
	divDetail.innerHTML = area.id;
	divDetail.show();
}

function hideDetail() {
	$('divDetail').hide();
}

function openLink(nodeIndex, cid) {
	window.location.href = "?tab=nodesTab&cid=" + cid + "&node=" + nodeIndex;
}

NodeArea=Class.create({
	initialize:function(cid){
		if (! $('nodes')) {
			document.body.appendChild(new Element('map', {name:'nodes', id:'nodes'}));
		}
		this.map = $('nodes');
		this.nodes = new Array();
		this.cid = cid;
	},
	
	addToMap: function(id, coords, title, vars, cond) {
		area = this.map.appendChild(new Element('area', 
				{id:'_'+id, coords:coords, 
					href:'javascript:openLink(' + id + ',' + this.cid + ')' , 
					shape:'rect'}));
		area.observe('mouseover', this.mouseover);
		area.observe('mouseout', this.mouseout);
		area.areaName = title;
		area.vars = vars;
		area.cond = cond;
	},
	
	mouseover: function(event) {
		d = $('divDetail');
		if (d.visible()) {
			debug.log("visible")
			return;
		}
		d.innerHTML = '';
		area = Event.element(event);
		d.insert('<div style="width:100%; text-align:center;font-weight:bold">' + area.areaName + '</div>');
		inVars = new Array();
		outVars = new Array();
		$H(area.vars).each(
			function(v) {
				if (v.value.readonly == 'true') {
					inVars[inVars.length] = v;
				} else {
					outVars[outVars.length] = v;
				}
			}
		);
		if (inVars.length > 0) {
			d.insert('<br/>Entrada:');
		}
		ulIn = d.appendChild(new Element('ul', {style:'list-style:circle;margin:0'}));
		if (outVars.length > 0) {
			d.insert('Saída:');
		}
		ulOut = d.appendChild(new Element('ul', {style:'margin:0'}));
		inVars.each(function(v) {
			ulIn.insert('<li>' + v.value.name + ' (' + v.value.type + ')' + '</li>');
		});
		outVars.each(function(v) {
			ulOut.insert('<li>' + v.value.name + ' (' + v.value.type + ')' + '</li>');
		});
		d.show();
		if (area.cond != null){
			d.insert('<br/>Condição: ' + area.cond);
		}
	},

	mouseout: function(event) {
		$('divDetail').hide();
	}

});