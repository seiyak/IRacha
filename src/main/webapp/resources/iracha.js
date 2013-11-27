function searchSpecific() {
	$.ajax({
		success : function(msg) {
			console.log("searchSpecific() success");
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert("error:" + textStatus);
		},
		url : "./service/specific",
		headers : {
			"specific" : $("#racha-box").val()
		}
	});
}

function searchPrefix() {
	$.ajax({
		success : function(msg) {
			console.log("searchPrefix() success");
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert("error:" + textStatus);
		},
		url : "./service/prefix",
		headers : {
			"prefix" : $("#racha-box").val()
		}
	});
}

function searchMultipleStates() {

	$.ajax({
		success : function(msg) {
			console.log("searchMultipleStates() success");
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert("error:" + textStatus);
		},
		url : "./service/multiple-states",
		data : {
			"multiple-states0" : "Illinois",
			"multiple-states1" : "Texas",
			"multiple-states2" : "California"
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
