function NodeArea(scid) {

    if ($('#nodes').length == 0) {
        var map = $('<map></map>');
        map.attr({'id': 'nodes', 'name': 'nodes'});
        $('body').append(map);

        if ($("#divDetail").length === 0){
          var tooltip = $("<div id='divDetail' class='graphHint'></div>");
          $("#nodes").after(tooltip);
        }
    }

    this.map = $('#nodes');
    this.nodes = [];
    this.scid = scid;

    this.map.empty();

    function createLink(nodeIndex, scid) {
        return "?tab=nodesTab&scid=" + scid + "&node=" + nodeIndex;
    }

    this.addToMap = function(id, coords, title, vars, cond) {
        var area = $('<area></area>');
        area.attr({
            id: '_' + id,
            coords: coords,
            shape: 'rect',
            areaName:title,
        });
        $(area).click(function(){onClickGraphElement(id)});
        $(area).hover(this.mouseover, this.mouseout);
        area[0].areaName = title;
        area[0].vars = vars;
        area[0].cond = cond;

        this.map.append(area);
    };

    function setVariableTooltip(dom){
      var tooltip = $('#divDetail');
      tooltip.empty();
      var area = $(dom);

      tooltip.append($("<div class='graphHint-title'>" + area[0].areaName + "</div>"));

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
          tooltip.append('<br/>Entrada:');
      }

      var ulIn = $('<ul style:"list-style:circle;margin:0"></ul>');
      tooltip.append(ulIn);

      if (outVars.length > 0) {
          var saida = "Sa\u00EDda";
          tooltip.append(saida + ':');
      }

      var ulOut = $('<ul style:"margin:0"></ul>');
      tooltip.append(ulOut);

      _.each(inVars, function(v) {
          ulIn.append($('<li>' + v.name + ' (' + v.type + ')' + '</li>'));
      });

      _.each(outVars, function(v) {
          ulOut.append($('<li>' + v.name + ' (' + v.type + ')' + '</li>'));
      });

      if (area[0].cond != null){
          var condicao = "Condi\u00E7\u00E3o";
          tooltip.append($('<p>' + condicao + ': ' + area[0].cond + '</p>'));
      }
      var coords = area.attr("coords").split(",");
      var left = coords[0]-90+area.position().top;
      var top = coords[1]-(-216)+area.position().left;

      tooltip.css({
        top:Math.max(0,Math.min(window.innerHeight - tooltip.height(), top)),
        left:Math.max(0,Math.min(window.innerHeight - tooltip.width(), left))
      });
    }

    this.mouseover = function(event) {
        setVariableTooltip(event.target);
    };

    this.mouseout = function(event) {

    };
}