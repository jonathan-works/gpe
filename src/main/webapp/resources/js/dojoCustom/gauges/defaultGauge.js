namespace("infox.DefaultGauge", function DefaultGauge(args) {
	require({
		async : true,
		baseUrl:args.baseUrl || ""
	}, [ "dojox/gauges/AnalogGauge", "dojox/gauges/AnalogArcIndicator",
			"dojox/gauges/AnalogNeedleIndicator" ], function() {
		dojo.ready(function() {
			var $angle = args.angle || 300;
			var $radius = args.radius || 150;
			var $width = args.width || $radius * 4;
			var $height = args.height || $radius * 4;

			var $props = {
				id : args.id || "id",
				width : $width,
				height : $height,
				cx : $width / 2,
				cy : $height / 2,
				radius : $radius,
				startAngle : -$angle / 2,
				endAngle : $angle / 2,
				hideValues : args.hideValues || false,
				useTooltip : false,
				background : 'rgba(0,0,0,0)'
			}

			var $min = args.min || 0;
			var $max = args.max || 100;
			$props.ranges = [ {
				id : $props.id + "Range",
				low : $min,
				high : $max,
				hover : $min + "-" + $max,
				color : {
					type : "radial",
					cx : $width / 2,
					cy : $height / 2,
					r : $radius,
					colors : [ {
						offset : 0,
						color : '#AAA'
					}, {
						offset : 1,
						color : '#444'
					} ]
				}
			} ];

			var $value = args.value || 0;
			$props.indicators = [ new dojox.gauges.AnalogArcIndicator({
				width : $radius * 0.2,
				offset : $radius,
				noChange : true,
				value : $max,
				color : "#CC0000"
			}), new dojox.gauges.AnalogArcIndicator({
				width : $radius * 0.2,
				offset : $radius,
				noChange : true,
				value : $max * 0.75,
				color : "#CCCC00"
			}), new dojox.gauges.AnalogArcIndicator({
				width : $radius * 0.2,
				offset : $radius,
				noChange : true,
				value : $max * 0.25,
				color : "#00CC00"
			}), new dojox.gauges.AnalogNeedleIndicator({
				id : dijit.getUniqueId($props.id + 'Needle'),
				value : $value,
				width : Math.max(2, $radius / 10),
				length : $radius * 1.2,
				color : '#000000',
				hover : (args.indicatorLabel || "") + ": " + $value,
				noChange : true
			}) ]

			if (!$props.hideValues) {
				$props.majorTicks = {
					length : $radius * 0.1,
					offset : $radius * 1.15,
					interval : args.tickInterval || ($max / 4),
					color : 'black'
				};
			}
			var $gauge = dojo.byId($props.id);
			$props.id = dijit.getUniqueId($props.id);
			$gauge = new dojox.gauges.AnalogGauge($props, $gauge);
			$gauge.startup();
			var $containerId = args.containerId || false;
		});
	});
});