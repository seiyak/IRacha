<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="initial-scale=1.0, user-scalable=no">
<meta charset="utf-8">
<script src="http://code.jquery.com/jquery-1.10.1.min.js"></script>
<script src="http://code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
<link rel="stylesheet"
	href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css">
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<script type="text/javascript" src="./resources/iracha.js"></script>
<title>IRacha!</title>
<!--
<script type="text/javascript">
	searchMultipleStates();
	searchSpecific();
	searchPrefix();
</script>
-->
<style>
html,body,#map-canvas {
	height: 100%;
	margin: 0px;
	padding: 0px;
	text-aglin: center;
}

input {
	background: white;
	border-radius: 2em;
	border: none;
	margin: 2em;
	padding: 0.8em;
	color: #A2A2A2;
	font-size: 1.1em;
	padding-left: 1.5em;
	outline: none;
	box-shadow: 0 4px 6px -5px hsl(0, 0%, 40%), inset 0px 4px 6px -5px
		hsl(0, 0%, 2%)
}
</style>
<script
	src="https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false"></script>
<script>
	var map;
	function initialize() {
		var mapOptions = {
			zoom : 4,
			center : new google.maps.LatLng(41.850033, -87.6500523)
		};
		map = new google.maps.Map(document.getElementById('map-canvas'),
				mapOptions);
	}

	google.maps.event.addDomListener(window, 'load', initialize);

	$("#racha-box").autocomplete({
		source : searchPrefix(),
		select : searchSpecific()
	});

	$("racha-link").click(function(event) {
		event.preventDefault();

	});
</script>
</head>
<body>
	<div id="container" class="ui-widget" align="center"
		style="background-color: grey;">
		<div id="recha-box-container"
			style="width: 850px; height: 150px; background-color: #95B9C7;">
			<input id="racha-box" style="width: 400px; margin-top: 70px;">
			<a id="racha-link" href="#" style="text-decoration: none;">Racha!</a>
		</div>
		<div id="map-canvas" class="ui-widget"
			style="width: 850px; height: 600px;"></div>
	</div>
</body>
</html>


