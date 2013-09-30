namespace("infox.ProgressBarGauge", function ProgressBarGauge(args) {
	require([ "dojox/gauges/BarIndicator", "dojox/gauges/BarGauge" ]);
	dojo.ready(function() {
		function $clamp(min, max, value) {
			return Math.min(Math.max(value || 0, min), max);
		}

		function $getColor(value) {
			if (value <= 0.25) {
				return "#00CC00";
			} else if (value <= 0.75) {
				return "#CCCC00";
			} else {
				return "#CC0000";
			}
		}
		var $width = args.width || 300;
		var $height = args.height || 55;
		var $barHeight = args.barHeight || ($height - 5) / 2;
		var $barWidth = args.barWidth || $width - 25;

		var $props = {
			id : args.id || "id",
			height : $height,
			dataHeight : $barHeight,
			dataY : args.barPosY || 0,
			width : $width,
			dataWidth : $barWidth,
			dataX : args.barPosX || 0,
			hideValues : false,
			useTooltip : false,
			background : 'rgba(0,0,0,0)'
		};

		var $min = args.min || 0;
		var $max = args.max || 100;
		$props.ranges = [ {
			low : $min,
			high : $max,
			hover : $min + " - " + $max,
			color : args.backgroundColor || "#CCCCCC"
		} ];

		if (!args.hideTicks) {
			$props.majorTicks = {
				length : 0.01,
				width : 1,
				offset : args.tickOffset || 0,
				interval : args.tickInterval || $max / 5
			};
		}

		var $barColor = args.barColor || $getColor(args.value / $max);

		var $gauge = dojo.byId($props.id);
		$props.id = dijit.getUniqueId($props.id);
		$props.indicators = [ new dojox.gauges.BarIndicator({
			id : $props.id + "Indicator",
			value : args.value || 0,
			width : ($barHeight * 0.8),
			color : $barColor,
			noChange : true
		}) ];

		$gauge = new dojox.gauges.BarGauge($props, $gauge);
		$gauge.startup();

		var $containerId = args.containerId || false;
		if (args.draggable && $containerId) {
			$j("#" + $containerId).draggable({
				scroll : false
			});
		}
		return $gauge;
	});
});