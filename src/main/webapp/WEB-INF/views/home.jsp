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
<script type="text/javascript" src="./resources/json2.js"></script>
<script type="text/javascript" src="./resources/iracha.js"></script>
<title>IRacha! - Rank utility charge at once</title>
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

#recha-box-container {
	border-radius: 2em;
	border: none;
	margin: 2em;
	padding: 0.8em;
	font-size: 1.1em;
	padding-left: 1.5em;
	outline: none;
	box-shadow: 0 4px 6px -5px hsl(0, 0%, 40%), inset 0px 4px 6px -5px
		hsl(0, 0%, 2%)
}

table {
	background: #E5E4E2;
	color: black;
	border-radius: 2em;
	border: none;
	margin: 2em;
	padding: 0.8em;
	font-size: 1.1em;
	padding-left: 1.5em;
	outline: none;
	box-shadow: 0 4px 6px -5px hsl(0, 0%, 40%), inset 0px 4px 6px -5px
		hsl(0, 0%, 2%)
}

#intro {
	width: 850px;
	background: #E5E4E2;
	border-radius: 2em;
	border: none;
	margin: 2em;
	padding: 0.8em;
	font-size: 1.1em;
	padding-left: 1.5em;
	outline: none;
	box-shadow: 0 4px 6px -5px hsl(0, 0%, 40%), inset 0px 4px 6px -5px
		hsl(0, 0%, 2%);
	background: #E5E4E2;
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
		putStateCapitals(map);
	}

	google.maps.event.addDomListener(window, 'load', initialize);

	$(function() {
		$("#racha-link").click(function(event) {
			event.preventDefault();
			removeRachaTable();
			searchMultipleStates();
			removeIntroDiv();
			addIntroDiv();
		});

		$("#racha-box").val("Please type utility company/state name");
		$("#racha-box").keyup(function(event) {
			searchPrefix($(this).val());
		});

		$("#racha-box").focus(function(event) {
			$("#racha-box").val("");
		});

		appendEventForAboutIRacha();
		appendEventForAboutAuthor();
		appendEventForAboutIHowTo();
	});

	Array.prototype.clear = function() {
		if (this.length > 0) {
			this.splice(0, this.length);
		}
	};

	jQuery.fn.doesExist = function() {
		return jQuery(this).length > 0;
	};

	function addIntroDiv() {
		if (!$("#intro").doesExist()) {
			var tbl = $("[id^=rtbl-]:last-child");
			if (tbl == "undefined" || tbl.length == 0) {
				$("#container")
						.append(
								'<div id="intro" class="ui-widget"><ul style="list-style-type;"><li style="display: inline;"><a id="aboutIHowTo" href="#"style="text-decoration: none;">How To</a></li><li style="display: inline;"><a id="aboutIRacha" href="#"style="text-decoration: none;padding-left:10px;">IRacha</a></li><li style="display: inline;"><a id="aboutIAuthor" href="#"style="text-decoration: none;padding-left:10px;">Author</a></li></ul></div>');
			} else {
				tbl
						.after('<div id="intro" class="ui-widget"><ul style="list-style-type;"><li style="display: inline;"><a id="aboutIHowTo" href="#"style="text-decoration: none;">How To</a></li><li style="display: inline;"><a id="aboutIRacha" href="#"style="text-decoration: none;padding-left:10px;">IRacha</a></li><li style="display: inline;"><a id="aboutIAuthor" href="#"style="text-decoration: none;padding-left:10px;">Author</a></li></ul></div>');
			}
			appendEventForAboutIRacha();
			appendEventForAboutAuthor();
			appendEventForAboutIHowTo();
		}
	}

	function appendEventForAboutIHowTo() {
		$("#aboutIHowTo").click(function(event) {
			event.preventDefault();
			removeAllAbouts();
			removeMapAndRachaTable();
			$("#recha-box-container").after(appendHowToMessage());
		});
	}

	function appendEventForAboutIRacha() {
		$("#aboutIRacha").click(function(event) {
			event.preventDefault();
			removeAllAbouts();
			removeMapAndRachaTable();
			$("#recha-box-container").after(appendIRachaMessage());
		});
	}

	function appendEventForAboutAuthor() {
		$("#aboutIAuthor").click(function(event) {
			event.preventDefault();
			console.log("come here for aboutAuthor!!");
			removeAllAbouts();
			removeMapAndRachaTable();
			$("#recha-box-container").after(appendAuthorMessage());
		});
	}

	function removeAllAbouts() {
		removeAboutAuthor();
		removeAboutRacha();
		removeAboutHowTo();
	}

	function removeAboutAuthor() {
		if ($("#aboutAuthor").doesExist()) {
			$("#aboutAuthor").remove();
		}
	}

	function removeAboutRacha() {
		if ($("#aboutRacha").doesExist()) {
			$("#aboutRacha").remove();
		}
	}

	function removeAboutHowTo() {
		if ($("#aboutHowTo").doesExist()) {
			$("#aboutHowTo").remove();
		}
	}

	function appendHowToMessage() {
		var msg = "";
		msg = "<div id='aboutRacha' class='ui-widget' style='width:850px;text-align:left;'>There are two ways to rank utility comapnies.";
		msg += "<ol><li>Type a specific utility company name</li><li>Click markers on the map and click \"Racha!\" or \"Reracha!\" link</li></ol>";
		msg += "</div>";
		return msg;
	}

	function appendIRachaMessage() {
		return "<div id='aboutRacha' class='ui-widget' style='width:850px;text-align:left;'>IRacha lets you rank all the available utility companies by their monthly charges at one ! It sounds easy ? Actually it's NOT! Because of the APIs that we can used from The Department of Enery.Their APIs are awful and messy. Also if it's easy, it would have existed even before IRacha. As long as I know, IRacha would be the first application to generate such ranking with the APIs.Enojoy it.</div>";
	}

	function appendAuthorMessage() {
		return "<div id='aboutAuthor' class='ui-widget' style='width:850px;text-align:left;'><p>The author of IRacha is Seiya Kawashima. He works for The University of Chicago Radiology Department as an Application Programmer."
				+ "He is very interested in the Internet architecture,web search engines, distributed computing."
				+ "When he has spare time, he works on his own open source projects. You can find his work at <a href='https//www.github.com/seiyak'>github</a>.Also you can reach him at <a href='skawashima@uchicago.edu'>skawashima@uchicago.edu</a>.</p>"
				+ "<p>His quotes are like below.</p>"
				+ "<ul style='list-style-type: none;'>"
				+ "<li>\"Facebook ? Never used it. It's boring.\"</li>"
				+ "<li>\"Twitter ? Never used it. It's boring.\"</li>"
				+ "<li>\"I'll use Java when need to do a demo quickly. I'll use C++ when need to be serious.I'll use C when need to be more serious.\"</li>"
				+ "</ul>" + "</div>";
	}

	function removeIntroDiv() {
		if ($("#intro").doesExist()) {
			$("#intro").remove();
		}
	}
</script>
</head>
<body>
	<div id="container" class="ui-widget" align="center"
		style="background-color: white;">
		<div id="recha-box-container"
			style="width: 850px; height: 100px; background-color: #E5E4E2;">
			<ul style="list-style: none; padding: 0; margin: 0;">
				<li><img id="iracha-logo" border="0"
					src="./resources/images/iracha_image.gif" alt="could not"
					width="300" height="50"
					title="IRacha ! - Rank utility charge at once"></li>
				<li><input id="racha-box"
					style="width: 550px; height: 20px; margin-top: 1px;"> <a
					id="racha-link" href="#"
					title="Type or click marker and click here"
					style="text-decoration: none;">Racha!</a></li>
			</ul>
		</div>
		<div id="map-canvas" class="ui-widget"
			style="width: 850px; height: 600px;"></div>
		<div id="intro" class="ui-widget">
			<ul style="list-style-type: none;">
				<li style="display: inline;"><a id="aboutIHowTo" href="#"
					style="text-decoration: none;">How To</a></li>
				<li style="display: inline;"><a id="aboutIRacha" href="#"
					style="text-decoration: none;">IRacha</a></li>
				<li style="display: inline;"><a id="aboutIAuthor" href="#"
					style="text-decoration: none; padding-left: 10px;">Author</a></li>
			</ul>
		</div>
	</div>
</body>
</html>


