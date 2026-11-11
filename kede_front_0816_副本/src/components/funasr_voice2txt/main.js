/**
 * Copyright FunASR (https://github.com/alibaba-damo-academy/FunASR). All Rights
 * Reserved. MIT License  (https://opensource.org/licenses/MIT)
 */
/* 2022-2023 by zhaoming,mali aihealthx.com */

function on_recoder_mode_change() {
	var item = null;
	var obj = document.getElementsByName("recoder_mode");
	for (var i = 0; i < obj.length; i++) { //遍历Radio 
		if (obj[i].checked) {
			item = obj[i].value;
			break;
		}
	}
	if (item == "mic") {
		document.getElementById("mic_mode_div").style.display = 'block';
		document.getElementById("rec_mode_div").style.display = 'none';


		start.disabled = true;
		stop.disabled = true;
		btnConnect.disabled = false;
		isfilemode = false;
	} else {
		document.getElementById("mic_mode_div").style.display = 'none';
		document.getElementById("rec_mode_div").style.display = 'block';

		start.disabled = true;
		stop.disabled = true;
		btnConnect.disabled = true;
		isfilemode = true;
		info_div.innerHTML = '请点击选择文件';
	}
}
typeof window !== 'undefined' && (window.on_recoder_mode_change = on_recoder_mode_change);


function getHotwords() {
	return "";
}
typeof window !== 'undefined' && (window.getHotwords = getHotwords);

function getAsrMode() {
	var item = '2pass';
	return item;
}
typeof window !== 'undefined' && (window.getAsrMode = getAsrMode);

function funasr_judgeIsStart(wsconnecter) {
	let socketState = wsconnecter.judgeIsStart();
	return socketState == 1;
}
typeof window !== 'undefined' && (window.funasr_judgeIsStart = funasr_judgeIsStart);

function getUseITN() {
	var obj = document.getElementsByName("use_itn");
	for (var i = 0; i < obj.length; i++) {
		if (obj[i].checked) {
			return obj[i].value === "true";
		}
	}
	return false;
}
typeof window !== 'undefined' && (window.getUseITN = getUseITN);
