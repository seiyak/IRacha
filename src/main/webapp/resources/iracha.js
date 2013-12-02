var scMap = new Array();
var selectedStates = new Array();
var allMarkers = new Array();
var maps = new Array();

function searchSpecific(val) {

	if (isEmptyRachaBox()) {
		return;
	}

	$.ajax({
		success : function(msg) {
			removeMapCanvas();
			setRerachaText();
			var json = msg;
			if (json["kind"] == "company") {
				workOnCompany(json);
			} else if (json["kind"] == "state") {

			}
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert("error for searchSpecific():" + jqXHR.responseText);
		},
		url : "./service/specific",
		headers : {
			"specific" : val
		}
	});
}

function setRerachaText() {
	$("#racha-link").text("Reracha!");
	$("#racha-link").attr("title", "Click here to go back to U.S map");
}

function setRachaText() {
	$("#racha-link").text("Racha!");
	$("#racha-link").attr("title", "Type or click marker and click here");
}

function workOnCompany(json) {

	var rachaTable = "<div id='rachaTable' class='ui-widget' style='width: 850px; height: 600px;'>";
	$.each(json["data"], function(index, value) {
		rachaTable += "<table class='rTable'" + "id='rtbl-" + index + "'>";
		rachaTable += createRachaTable(json["data"],
				json["data"][index]["company-" + index]["rankInState"],
				json["data"][index]["company-" + index]["rankInUS"], index);
		rachaTable += "</table>";
	});
	rachaTable += "</div>";
	appendRachaTable(rachaTable);
}

function appendRachaTable(rachaTable) {

	if (!$("#rachaTable").doesExist()) {

		removeIntroDiv();
		$("#container").append(rachaTable);
		addIntroDiv();
	} else {
		$("#rachaTable").remove();
		removeIntroDiv();
		$("#container").append(rachaTable);
		addIntroDiv();
	}
}

function createRachaTable(json, rankInState, rankInUS, index) {

	var comp = json[index]["company-" + index]["company"];
	var msg = "<tr><td>Name</td><td>" + comp["name"] + "</td></tr>"
			+ "<tr><td>Id</td><td>" + comp["id"] + "</td></tr>"
			+ "<tr><td>Location</td><td>" + comp["place"] + "</td></tr>"
			+ "<tr><td>Effective date</td><td>"
			+ json[index]["company-" + index]["startDate"] + "</td></tr>";

	if (json[index]["company-" + index]["currentMonthly"] > 0
			&& json[index]["company-" + index]["currentMonthly"] < 100) {
		msg += "<tr><td>Current monthly charge ("
				+ json[index]["company-" + index]["unit"] + ")</td><td>$ "
				+ json[index]["company-" + index]["currentMonthly"]
				+ "</td></tr>";
	} else {
		msg += "<tr><td>Current monthly charge ("
				+ json[index]["company-" + index]["unit"]
				+ ")</td><td> - </td></tr>";
	}

	if (rankInState != -1) {
		msg += "<tr><td>Rank in state</td><td>" + decorateRank(rankInState)
				+ "</td></tr>";
	} else {
		msg += "<tr><td>Rank in state</td><td> - </td></tr>";
	}

	if (rankInUS != -1) {
		msg += "<tr><td>Rank in U.S</td><td>" + decorateRank(rankInUS)
				+ "</td></tr>";
	} else {
		msg += "<tr><td>Rank in U.S</td><td> - </td></tr>";
	}

	msg += appendAverageTable(json[index]["company-" + index]["average"]);

	return msg;
}

function decorateRank(rank) {
	if (rank == 1) {
		return "1st";
	} else if (rank == 2) {
		return "2nd";
	} else if (rank == 3) {
		return "3rd";
	}

	return rank + "th";
}

function appendAverageTable(average) {

	var msg = "";
	var ave = average["industrial"];
	if (ave < 0.001) {
		msg += "<tr><td>Avg industrial (" + average["unit"]
				+ ")</td><td> - </td></tr>";
	} else {
		msg += "<tr><td>Avg industrial (" + average["unit"] + ")</td><td>"
				+ average["industrial"] + "</td></tr>";
	}

	ave = average["residential"];
	if (ave < 0.001) {
		"<tr><td>Avg residential (" + average["unit"]
				+ ")</td><td> - </td></tr>";
	} else {
		msg += "<tr><td>Avg residential (" + average["unit"] + ")</td><td>"
				+ average["residential"] + "</td></tr>";
	}

	ave = average["commercial"];
	if (ave < 0.001) {
		msg += "<tr><td>Avg commercial (" + average["unit"]
				+ ")</td><td> - </td></tr>";
	} else {
		msg += "<tr><td>Avg commercial (" + average["unit"] + ")</td><td>"
				+ average["commercial"] + "</td></tr>";
	}

	return msg;
}

function searchPrefix(val) {

	if (isEmptyRachaBox()) {
		return;
	}

	$.ajax({
		success : function(msg) {
			var json = msg["data"];
			var pfs = [];
			$.each(json, function(index, value) {
				pfs.push(value["prefix-" + index]);
			});

			pfs.sort();

			$("#racha-box").autocomplete({
				source : pfs,
				select : function(event, ui) {
					searchSpecific(ui.item.label);
				}
			});
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert("error for searchPrefix():" + jqXHR.responseText);
		},
		url : "./service/prefix",
		headers : {
			"prefix" : val
		}
	});
}

function isEmptyRachaBox() {

	if ($("#racha-box").val() == "" || $("#racha-box").val() == undefined)
		return true;
	return false;
}

function searchMultipleStates() {

	removeMarkers();
	if (selectedStates.length == 0) {
		return;
	}

	var dd = "";
	for (var i = 0; i < selectedStates.length; i++) {

		if (i < (selectedStates.length) - 1) {
			dd += selectedStates[i] + ",";
		} else {
			dd += selectedStates[i];
		}
	}

	$.ajax({
		success : function(msg) {

			removeMarkerAnimation();
			// reputStateCapitals();

			setRerachaText();
			selectedStates.clear();
			var json = msg;
			if (json["data"].length == 0) {
				alert("Could not find any rates. Please try again.");
				reputStateCapitals();
			} else {

				removeMapCanvas();
				if (json["kind"] == "company") {
					workOnCompany(json);
				} else if (json["kind"] == "state") {

				}
			}
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert("error for searchMultipleStates():" + jqXHR.responseText);
		},
		url : "./service/multiple-states",
		data : {
			"multiple-states" : dd
		}
	});
}

function appendMapCanvas() {

	if (!$("#map-canvas").doesExist()) {
		$("#container")
				.append(
						"<div id='map-canvas' class='ui-widget' style='width: 850px; height: 600px;'></div>");
	}
}

function removeMapCanvas() {

	if ($("#map-canvas").doesExist()) {
		$("#map-canvas").remove();
	}
}

function removeRachaTable() {

	if ($("#rachaTable").doesExist()) {
		if ($("#racha-link").text() == "Reracha!") {
			$("#racha-link").text("Racha!");
			$("#rachaTable").remove();
			$("#racha-box").val("");
			$("#racha-box").focus();
			removeIntroDiv();
			appendMapCanvas();
			initialize();
			addIntroDiv();
		}
	} else {
		if ($("#aboutRacha").doesExist()) {
			$("#aboutRacha").remove();
		} else if ($("#aboutAuthor").doesExist()) {
			$("#aboutAuthor").remove();
		}
		$("#racha-link").text("Racha!");
		$("#racha-box").val("");
		$("#racha-box").focus();
		removeIntroDiv();
		appendMapCanvas();
		initialize();
		addIntroDiv();
	}
}

function removeMapAndRachaTable() {

	if ($("#rachaTable").doesExist()) {
		$("#rachaTable").remove();
	}

	removeMapCanvas();
	removeIntroDiv();
	addIntroDiv();
}

function reputStateCapitals() {
	if (maps.length == 0) {
		maps.push(map);
	}
	putStateCapitals(maps[0]);
}

function putStateCapitals(map) {

	$
			.ajax({
				success : function(msg) {
					var json = msg["data"];
					$
							.each(
									json,
									function(index, value) {

										scMap[json[index]["capital-" + index]["capital"]] = json[index]["capital-"
												+ index]["state"];

										var marker = new google.maps.Marker(
												{
													position : new google.maps.LatLng(
															json[index]["capital-"
																	+ index]["latitude"],
															json[index]["capital-"
																	+ index]["longitude"]),
													map : map,
													title : json[index]["capital-"
															+ index]["capital"]
												});
										allMarkers.push(marker);

										var infowindow = new google.maps.InfoWindow(
												{
													content : "<div id='capital-"
															+ json[index]["capital-"
																	+ index]["capital"]
															+ "'>"
															+ "<table><tr><td>State</td><td>"
															+ json[index]["capital-"
																	+ index]["state"]
															+ "</td></tr>"
															+ "<tr><td>Capital</td><td>"
															+ json[index]["capital-"
																	+ index]["capital"]
															+ "</td></tr>"
															+ "<tr><td>Latitude</td><td>"
															+ json[index]["capital-"
																	+ index]["latitude"]
															+ "</td></tr>"
															+ "<tr><td>Longitude</td><td>"
															+ json[index]["capital-"
																	+ index]["longitude"]
															+ "</td></tr></table></div>",
													maxWidth : 300
												});

										google.maps.event
												.addListener(
														marker,
														'click',
														function() {
															// infowindow
															// .open(map,
															// marker);
															console
																	.log("state: "
																			+ scMap[marker
																					.getTitle()]);
															if (appendUnique(scMap[marker
																	.getTitle()])) {
																marker
																		.setAnimation(google.maps.Animation.BOUNCE);
															} else {
																marker
																		.setAnimation(google.maps.Animation.NONE);
															}
														});

									});
				},
				error : function(jqXHR, textStatus, errorThrown) {
					alert("error for putStateCapitals():" + jqXHR.responseText);
				},
				url : "./service/capitals",
			});
}

function appendUnique(state) {

	for (var i = 0; i < selectedStates.length; i++) {
		if (selectedStates[i] == state) {
			console
					.log("clicked marker twice, unselect the marker and remove it from selectedStates");
			selectedStates.splice(i, 1);

			return false;
		}
	}

	selectedStates.push(state);
	return true;
}

function removeMarkerAnimation() {
	for (var i = 0; i < allMarkers.length; i++) {
		if (allMarkers[i].getAnimation() != google.maps.Animation.NONE) {
			allMarkers[i].setAnimation(google.maps.Animation.NONE);
		}
	}

	selectedStates.clear();
}

function removeMarkers() {

	for (var i = 0; i < allMarkers.length; i++) {
		allMarkers[i].setMap(null);
	}

	allMarkers.clear();
}
