<template>
	<div class="funasr-voice2txt">
		<div class="demo2" v-if="false">
			<div class="content">
				{{finalMsg}}
			</div>
			<button type="primary" @longpress="myStart" @touchend="myEnd">{{btnStartTxt}}</button>
		</div>
	</div>
</template>

<script>
	// 配置信息
	// import config from '@/config.js'

	export default {
		props: ['sourceStream'],
		data() {
			return {
				start: {
					disabled: false
				},
				stop: {
					disabled: false
				},
				btnConnect: {
					disabled: false
				},
				awsslink: {
					disabled: false
				},
				info_div: {
					innerHTML: ''
				},
				upfile: {},
				rec: undefined,
				varArea: {value: ''},
				rec_text: '',
				offline_text: '',
				isfilemode: false,
				file_ext: '',
				file_sample_rate: 16000,
				file_data_array: undefined,
				totalsend: 0,
				sampleBuf: new Int16Array(),
				wsconnecter: undefined,
				audioBlob: undefined,
				sendBuf: undefined,
				now_ipaddress: process.env.VUE_APP_FUNASR_SOCKET,

				btnStartTxt: '语音识别(请按下1s后再说话)',
				accessToken: {
					token: null,
					expireTime: null
				},
				disableButton: {
					start: false,
					stop: true,
				},
				recordingStatus: null,
				finalMsg: "",
				lastMsg: "",
				currMsg: "",
				recordings: [],
				token: "",
				isSpeaking: false, // 是否开始说

				audioContext: null,
				audioProcessor: null,

				pcmBuffer: new Int16Array(),
				mediaRecorder: null,
				websocket: null,
			}
		},
		created() {
			let that = this;
			window.funasrReturnObj = {
				resolve: this.receiveMsg,
				now_ipaddress: process.env.VUE_APP_FUNASR_SOCKET,
			}
			// console.log(`============>config : `, config);
		},
		mounted() {
			this.loadModule();
		},
		destroyed() {
			this.funasr_stop(2);
			console.log(`funasr============>destroyed`);
		},
		methods: {
			prepareRecording() {
				this.myStart();
				// console.log(`===========>prepareRecording`);
			},
			stopRecording() {
				this.myEnd(1);
				// console.log(`===========>stopRecording`);
			},
			judgeIsStart() {
				// console.log(`===========>judgeIsStart`);
				return funasr_judgeIsStart();
			},
			receiveMsg(msgObj) {
				this.currMsg = msgObj.value;
				this.finalMsg = this.lastMsg + this.currMsg;
				if (msgObj.type == 'finished_msg') {
					this.lastMsg = this.finalMsg + this.currMsg;
					console.log(`funasr stream receiveMsg==========>${this.finalMsg}`);
					
					this.$emit('son_send_msg', msgObj);
					this.$emit('sonSendFinalMsg', this.currMsg);
				} else {
					this.$emit('son_send_msg', msgObj);
					this.$emit('sonSendMsg', this.currMsg);
				}
			},
			myStart() {
				this.isSpeaking = true;
				this.funasr_record();
				// console.log(`============>myStart`);
			},
			myEnd() {
				this.isSpeaking = false;
				this.funasr_stop(1);
				// console.log(`============>myEnd`);
			},
			// 加载组件依赖
			async loadModule() {
				let that = this;
				if (!that.now_ipaddress || !String(that.now_ipaddress).match(/^(wss|ws):/) || process.env.VUE_APP_FUNASR_ENABLE === 'false') {
					console.warn('[FunASR] 本地已关闭语音识别自动连接');
					return;
				}
				try {
					await import('./recorder-core.js');
					await import('./wav.js');
					await import('./pcm.js');
					await import('./wsconnecter.js');
					await import('./main.js');
					this.wsconnecter = new WebSocketConnectMethod({
						msgHandle: that.getJsonMessage,
						stateHandle: that.getConnState,
						now_ipaddress: `${that.now_ipaddress}`
					});
					this.rec = Recorder({
						type: "pcm",
						bitRate: 16,
						sampleRate: 16000,
						sourceStream: that.sourceStream,
						onProcess: that.recProcess
					});
					this.funasr_start();
					this.prepareRecording();
				} catch (error) {
					console.error('Failed to load module', error);
				}
			},

			funasr_record() {
				let that = this;
				// 1. 该方法能够重复执行 链接websocket
				this.funasr_start();
				// 2. 打开麦克风，录音会通过websocket发送后台
				this.rec.open(function() {
					// debugger
					that.rec.start();
					// console.log("funasr 开始 录音 ……");
					that.start.disabled = true;
					that.stop.disabled = false;
					that.btnConnect.disabled = true;
				});
			},

			// 识别启动、停止、清空操作
			funasr_start() {
				// 清除显示
				this.clear();
				//启动连接
				var ret = this.wsconnecter.wsStart();
				// 1 is ok, 0 is error
				if (ret == 1) {
					this.info_div.innerHTML = "已经连接asr服务器...";
					this.isRec = true;
					this.start.disabled = true;
					this.stop.disabled = true;
					this.btnConnect.disabled = true;
					// console.log(`${info_div.innerHTML}`);
					return 1;
				} else {
					this.info_div.innerHTML = "连接asr服务器异常，请等待...";
					this.start.disabled = true;
					this.stop.disabled = true;
					this.btnConnect.disabled = false;
					// console.log(`${info_div.innerHTML}`);
					return 0;
				}
			},

			/**
			 * stopState: 1:默认不关闭ws;2:关闭ws;
			 */
			funasr_stop(stopState = 1) {
				let that = this;
				var chunk_size = new Array(5, 10, 5);
				var request = {
					"chunk_size": chunk_size,
					"wav_name": "h5",
					"is_speaking": false,
					"chunk_interval": 10,
					"mode": getAsrMode(),
				};
				// console.log(request);
				if (this.sampleBuf.length > 0) {
					this.wsconnecter.wsSend(this.sampleBuf);
					// console.log("sampleBuf.length" + sampleBuf.length);
					this.sampleBuf = new Int16Array();
				}
				this.wsconnecter.wsSend(JSON.stringify(request));

				this.isRec = false;
				this.info_div.innerHTML = "发送完数据,请等候,正在识别...";

				if (this.isfilemode == false) {
					this.stop.disabled = true;
					this.start.disabled = true;
					this.btnConnect.disabled = true;
					//wait 3s for asr result 目前 销毁组件结束 websocket链接
					setTimeout(function() {
						// console.log("call funasr_stop ws!");
						if (stopState == 2) {
							that.wsconnecter.wsStop();
						}
						that.btnConnect.disabled = false;
						that.info_div.innerHTML = "请点击连接";
					}, 3000);
					this.rec.close();
				}
				// 停止连接
			},
			recProcess(buffer, powerLevel, bufferDuration, bufferSampleRate, newBufferIdx, asyncEnd) {
				// console.log(`funasr recProcess 发送数据===========>isRec：${isRec}`);
				if (this.isRec === true) {
					var data_48k = buffer[buffer.length - 1];

					var array_48k = new Array(data_48k);
					var data_16k = Recorder.SampleData(array_48k, bufferSampleRate, 16000).data;
					let tempBuf = this.sampleBuf;
					this.sampleBuf = Int16Array.from([...tempBuf, ...data_16k]);
					var chunk_size = 960; // for asr chunk_size [5, 10, 5]
					this.info_div.innerHTML = "" + bufferDuration / 1000 + "s";
					while (this.sampleBuf.length >= chunk_size) {
						tempBuf = this.sampleBuf;
						let sendBuf = tempBuf.slice(0, chunk_size);
						let t2Buf = tempBuf.slice(chunk_size, this.sampleBuf.length);
						this.sampleBuf = t2Buf;
						this.wsconnecter.wsSend(sendBuf);
						// console.log(`sendBuf===========>${this.sampleBuf.length}`);
					}
				}
			},

			// 语音识别结果; 对jsonMsg数据解析,将识别结果附加到编辑框中
			// window.funasrReturnObj.resolve 是回调
			getJsonMessage(jsonMsg) {
				// console.log(jsonMsg);
				var rectxt = "" + JSON.parse(jsonMsg.data)['text'];
				var asrmodel = JSON.parse(jsonMsg.data)['mode'];
				var is_final = JSON.parse(jsonMsg.data)['is_final'];
				// console.log(`asrmodel:${asrmodel} `+"message: " + JSON.parse(jsonMsg.data)['text']);
				var timestamp = JSON.parse(jsonMsg.data)['timestamp'];
				if (asrmodel == "2pass-offline" || asrmodel == "offline") {
					this.offline_text = this.offline_text + this.handleWithTimestamp(rectxt, timestamp); //rectxt; //.replace(/ +/g,"");
					this.rec_text = this.offline_text;
					let msgObj = {
						type: `finished_msg`,
						value: `${this.rec_text}`
					}
					this.receiveMsg(msgObj);
					// console.log(`is_final:${is_final},rec_text 1 if: rec_text:${this.rec_text},rectxt:${rectxt}`);
				} else {
					this.rec_text = this.rec_text + rectxt; //.replace(/ +/g,"");
					let msgObj = {
						type: `changed_msg`,
						value: `${this.rec_text}`
					}
					// console.log(`is_final:${is_final},rec_text 1 else: rec_text:${this.rec_text},rectxt:${rectxt}`);
					this.receiveMsg(msgObj);
				}
			},

			// 连接状态响应
			getConnState(connState) {
				if (connState === 0) { //on open
					this.info_div.innerHTML = '连接成功!请点击开始';

					this.start.disabled = false;
					this.stop.disabled = true;
					this.btnConnect.disabled = true;
				} else if (connState === 1) {
					this.funasr_stop();
				} else if (connState === 2) {
					this.funasr_stop();
					// console.log('connecttion error');

					console.log("连接地址" + this.now_ipaddress + "失败,请检查asr地址和端口。错误原因：语音识别服务器宕机。");
					this.start.disabled = true;
					this.stop.disabled = true;
					this.btnConnect.disabled = false;

					this.info_div.innerHTML = '请点击连接';
				}
				// console.log(`funasr function getConnState : ${info_div.innerHTML}`);
			},

			handleWithTimestamp(tmptext, tmptime) {
				// // console.log("tmptext: " + tmptext);
				// // console.log("tmptime: " + tmptime);
				if (tmptime == null || tmptime == "undefined" || tmptext.length <= 0) {
					return tmptext;
				}
				tmptext = tmptext.replace(/。|？|，|、|\?|\.|\ /g, ","); // in case there are a lot of "。"
				var words = tmptext.split(","); // split to chinese sentence or english words
				var jsontime = JSON.parse(
					tmptime); //JSON.parse(tmptime.replace(/\]\]\[\[/g, "],[")); // in case there are a lot segments by VAD
				var char_index = 0; // index for timestamp
				var text_withtime = "";
				for (var i = 0; i < words.length; i++) {
					if (words[i] == "undefined" || words[i].length <= 0) {
						continue;
					}
					// console.log("words===", words[i]);
					// console.log("words: " + words[i] + ",time=" + jsontime[char_index][0] / 1000);
					if (/^[a-zA-Z]+$/.test(words[i])) { // if it is english
						text_withtime = text_withtime + jsontime[char_index][0] / 1000 + ":" + words[i] + "\n";
						char_index = char_index + 1; //for english, timestamp unit is about a word
					} else {
						// if it is chinese
						text_withtime = text_withtime + jsontime[char_index][0] / 1000 + ":" + words[i] + "\n";
						char_index = char_index + words[i].length; //for chinese, timestamp unit is about a char
					}
				}
				return text_withtime;
			},
			clear() {
				this.varArea.value = "";
				this.rec_text = "";
				this.offline_text = "";
				// console.log(`funasr 执行清空逻辑……`)
			},

		}
	}
</script>

<style scoped>
</style>