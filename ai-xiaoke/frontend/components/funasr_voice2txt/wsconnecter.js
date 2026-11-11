/**
 * Copyright FunASR (https://github.com/alibaba-damo-academy/FunASR). All Rights
 * Reserved. MIT License  (https://opensource.org/licenses/MIT)
 */
/* 2021-2023 by zhaoming,mali aihealthx.com */

function WebSocketConnectMethod( config ) { //定义socket连接方法类
	var speechSokt;
	var connKeeperID;
	var speekingState = 0; // 0:没有说话;1:说话中;
	
	var msgHandle = config.msgHandle;
	var stateHandle = config.stateHandle;
	var now_ipaddress = config.now_ipaddress;
			  
	this.wsStart = function () {
		var Uri = `${now_ipaddress}`;
		if(!Uri || !Uri.match(/^(wss|ws):\/\//)) {
			console.warn('[FunASR] 请检查 wss 地址正确性', Uri);
			return 0;
		}
		if ( 'WebSocket' in window ) {
			try {
				if(speechSokt && speechSokt.readyState != 1) {
					onOpen({});
					console.log(`funasr wsconnecter wsStart websocket 仍旧在 speekingState:${speekingState}`);
				} else {
					speechSokt = new WebSocket( Uri , ['binary']);
				}
				speechSokt.onopen = function(e){onOpen(e);};
				speechSokt.onclose = function(e){ onClose(e); };
				speechSokt.onmessage = function(e){onMessage(e);};
				speechSokt.onerror = function(e){onError(e);};
				return 1;
			} catch (err) {
				console.warn('[FunASR] WebSocket 创建失败', err && err.message);
				stateHandle(2);
				return 0;
			}
		} else {
			console.log('当前浏览器不支持 WebSocket');
			return 0;
		}
	};
	
	// 定义停止与发送函数
	this.wsStop = function () {
		if(speechSokt != undefined) {
			console.log("stop ws!");
			speechSokt.close();
		}
		speekingState = 0;
	};
	
	this.wsSend = function ( oneData ) {
		if(speechSokt == undefined) return;
		if ( speechSokt.readyState === 1 ) { // 0:CONNECTING, 1:OPEN, 2:CLOSING, 3:CLOSED
			speechSokt.send( oneData );
			let temp = JSON.stringify(oneData);
			if(temp && temp.length < 100) {
				console.log(`funasr wsconnecter wsSend speekingState:${speekingState} ， oneData:${temp}`);
			}
		}
	};
	
	// SOCEKT连接中的消息与状态响应
	function onOpen( e ) {
		// 发送json
		var chunk_size = new Array( 5, 10, 5 );
		var request = {
			"chunk_size": chunk_size,
			"wav_name":  "h5",
			"is_speaking":  true,
			"chunk_interval":10,
			"itn":getUseITN(),
			"mode":getAsrMode(),
			
		};
		// if(isfilemode) {
		// 	request.wav_format=file_ext;
		// 	if(file_ext=="wav")
		// 	{
		// 		request.wav_format="PCM";
		// 		request.audio_fs=file_sample_rate;
		// 	}
		// }
		// var hotwords=getHotwords();
		// if(hotwords!=null  ) {
		// 	request.hotwords=hotwords;
		// }
		console.log(JSON.stringify(request));
		if(speechSokt.readyState === 1) speechSokt.send(JSON.stringify(request));
		console.log(`funasr wsconnecter onOpen speekingState:${speekingState}`);
		stateHandle(0);
		speekingState = 1;
	}
	
	function onClose( e ) {
		stateHandle(1);
		speekingState = 0;
		console.log(`funasr wsconnecter onclose websocket onclose ws! speekingState:${speekingState}`);
	}
	
	function onMessage( e ) {
		msgHandle( e );
	}
	
	function onError( e ) {
		// info_div.innerHTML="连接"+e;
		console.log(e);
		stateHandle(2);
		speekingState = 0;
		console.log(`funasr wsconnecter onError websocket onclose ws! speekingState:${speekingState}`);
	}
	
	// 0:断开;1:已连接;
	function judgeIsStart() {
		return speekingState;
	}
}

typeof window !== 'undefined' && (window.WebSocketConnectMethod = WebSocketConnectMethod);
