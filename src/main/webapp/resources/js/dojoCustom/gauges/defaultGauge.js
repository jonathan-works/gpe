(function(){
	var $namespace = defineObject("br.com.infox.dojoCustom.gauges.DefaultGauge", function(args){
	  require(["dojox/gauges/AnalogGauge","dojox/gauges/AnalogArcIndicator","dojox/gauges/AnalogNeedleIndicator","#{util.contextPath/resources/js/jquery-ui-1.9.2.custom.js}"]);
	  dojo.ready(function() {
	    var $angle = args.angle || 300;
	    var $radius = args.radius || 150;
	    var $width = args.width || $radius*4;
	    var $height = args.height || $radius*4;
	    
	    var $props = {
			id: args.id || "id",
			width: $width,
			height: $height,
			cx: $width/2,
			cy: $height/2,
			radius: $radius,
			startAngle : -$angle/2,
			endAngle : $angle/2,
			hideValues : args.hideValues || false,
			useTooltip: false,
			background:'rgba(0,0,0,0)'
	    }

	    var $min = args.min || 0;
	    var $max = args.max || 100;
	    $props.ranges = [{
			id : $props.id+"Range",
			low : $min,
			high : $max,
			hover : $min+"-"+$max,
			color : {
			    type : "radial",
			    cx : $width/2,
			    cy : $height/2,
			    r : $radius,
			    colors : [{offset:0, color:'#AAA'},
		      			{offset:1, color:'#444'}]
			}
	    }];
	    
	    var $value = args.value || 0;
	    var $indicatorLabel = args.indicatorLabel || ""; 
		$props.indicators = [
			new dojox.gauges.AnalogArcIndicator({
				width:$radius*0.2,
				offset:$radius,
				noChange:true,
				value:$max,
				color:"#CC0000"
		    }),
			new dojox.gauges.AnalogArcIndicator({
				width:$radius*0.2,
				offset:$radius,
				noChange:true,
				value:$max*0.75,
				color:"#CCCC00"
		    }),
			new dojox.gauges.AnalogArcIndicator({
				width:$radius*0.2,
				offset:$radius,
				noChange:true,
				value:$max*0.25,
				color:"#00CC00"
		    }),
			new dojox.gauges.AnalogNeedleIndicator({
				id : dijit.getUniqueId($props.id+'Needle'),
				value : $value,
				width : Math.max(2,$radius/10),
				length : $radius*1.2,
				color : '#000000',
				title : $indicatorLabel,
				hover : $indicatorLabel+": "+$value,
				noChange : true
			})
		]
	    
	    if (!$props.hideValues) {
			$props.majorTicks = {
			    length : $radius*0.1,
			    offset : $radius*1.15,
			    interval : $max/4,
			    color : 'black'
			};
	    }
	    var $gauge = dojo.byId($props.id);
	    $props.id = dijit.getUniqueId($props.id);
	    $gauge = new dojox.gauges.AnalogGauge($props, $gauge);
	    $gauge.startup();
	    
	    $gauge.indicators[3].currentValue = 0;
	    $gauge.indicators[3].draw();
	    
	    var $containerId = args.containerId || false;
	    if (args.draggable && $containerId) {
	    	$j("#"+$containerId).draggable({scroll:false});
	    }
	  });  
	});
})()