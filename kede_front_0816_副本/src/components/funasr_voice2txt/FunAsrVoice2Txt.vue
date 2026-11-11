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
	var stream = undefined;
	var temp_rec_text = "";
	export default {
		props: {
			// 是否停止语音识别
			isStop: {
				type: Boolean,
				default: false
			}
		},
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
				// FunASR 远端不可用时，使用浏览器 Web Speech API 兜底
				browserRecognition: null,
				browserAsrMode: false,
				browserFinalText: '',
			}
		},
		created() {
			let that = this;
			window.funasrReturnObj = {
				resolve: this.receiveMsg,
				now_ipaddress: process.env.VUE_APP_FUNASR_SOCKET,
			}
			console.log(`============>config : `, process.env.VUE_APP_FUNASR_SOCKET);
		},
		mounted() {
			this.loadModule();
		},
		destroyed() {
			this.funasr_stop(2);
			this.stopBrowserAsr();
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
				return this.isSpeaking;
				// return funasr_judgeIsStart(this.websocket);
			},
			receiveMsg(msgObj) {
				this.currMsg = msgObj.value;
				this.finalMsg = this.lastMsg + this.currMsg;
				if (msgObj.type == 'finished_msg') {
					this.lastMsg = this.finalMsg + this.currMsg;
					// console.log(`funasr stream receiveMsg==========>${this.finalMsg}`);
					
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
				if (this.browserAsrMode) {
					this.stopBrowserAsr();
					return;
				}
				this.funasr_stop(1);
				// console.log(`============>myEnd`);
			},
			canUseBrowserAsr() {
				return !!(typeof window !== 'undefined' && (window.SpeechRecognition || window.webkitSpeechRecognition));
			},
			stopBrowserAsr() {
				const rec = this.browserRecognition;
				this.browserRecognition = null;
				this.browserAsrMode = false;
				this.browserFinalText = '';
				if (!rec) return;
				try {
					rec.onresult = null;
					rec.onerror = null;
					rec.onend = null;
					rec.stop();
				} catch (e) { /* ignore */ }
			},
			startBrowserAsr() {
				const that = this;
				const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
				if (!SpeechRecognition) {
					console.warn('[ASR] 浏览器不支持 Web Speech API');
					that.$emit('son_send_msg', { type: 'mic_start_fail', reason: 'asr_unavailable' });
					that.isSpeaking = false;
					return;
				}
				this.stopBrowserAsr();
				this.browserAsrMode = true;
				this.browserFinalText = '';
				temp_rec_text = '';
				const recognition = new SpeechRecognition();
				recognition.lang = 'zh-CN';
				recognition.continuous = true;
				recognition.interimResults = true;
				recognition.maxAlternatives = 1;
				this.browserRecognition = recognition;

				recognition.onstart = function() {
					that.$emit('son_send_msg', { type: 'mic_start_ok', value: '' });
				};
				recognition.onresult = function(event) {
					if (that.isStop) return;
					let interim = '';
					let newlyFinal = '';
					for (let i = event.resultIndex; i < event.results.length; i++) {
						const piece = (event.results[i][0] && event.results[i][0].transcript) || '';
						if (event.results[i].isFinal) {
							newlyFinal += piece;
						} else {
							interim += piece;
						}
					}
					if (newlyFinal) {
						that.browserFinalText += newlyFinal;
						temp_rec_text = '';
						// MyChat：changed_msg 覆盖 full_msg，finished_msg 会再 += tempValue
						// 这里先写入完整文本，再发空 tempValue，避免重复拼接，同时触发自动发送定时器
						that.receiveMsg({
							type: 'changed_msg',
							value: that.browserFinalText,
							tempValue: newlyFinal,
						});
						that.receiveMsg({
							type: 'finished_msg',
							value: that.browserFinalText,
							tempValue: '',
						});
					} else if (interim) {
						temp_rec_text = that.browserFinalText + interim;
						that.receiveMsg({
							type: 'changed_msg',
							value: temp_rec_text,
							tempValue: interim,
						});
					}
				};
				recognition.onerror = function(event) {
					const err = (event && event.error) || '';
					console.warn('[ASR] browser recognition error', err);
					if (err === 'aborted' || err === 'no-speech') return;
					if (that.isSpeaking) {
						that.$emit('son_send_msg', {
							type: 'mic_start_fail',
							reason: err === 'not-allowed' ? 'mic_denied' : 'asr_unavailable',
						});
						that.isSpeaking = false;
					}
					that.stopBrowserAsr();
				};
				recognition.onend = function() {
					// 连续听写：用户仍在说话时自动重启
					if (that.isSpeaking && that.browserAsrMode && !that.isStop) {
						try {
							recognition.start();
						} catch (e) { /* ignore restart races */ }
					}
				};
				try {
					recognition.start();
				} catch (e) {
					console.warn('[ASR] browser recognition start failed', e && e.message);
					that.$emit('son_send_msg', { type: 'mic_start_fail', reason: 'asr_unavailable' });
					that.isSpeaking = false;
					that.stopBrowserAsr();
				}
			},
			// 加载组件依赖
			async loadModule() {
				let that = this;
				// 本地未配置或远程 ASR 不可用时，不主动建连，避免控制台刷 502
				const asrUrl = String(that.now_ipaddress || '').trim();
				if (!asrUrl || !asrUrl.match(/^(wss|ws):/)) {
					console.warn('[FunASR] 未配置有效 VUE_APP_FUNASR_SOCKET，跳过语音识别自动连接');
					return;
				}
				const enableRaw = String(process.env.VUE_APP_FUNASR_ENABLE == null ? '' : process.env.VUE_APP_FUNASR_ENABLE).trim().toLowerCase();
				const asrEnabled = enableRaw === 'true' || enableRaw === '1' || enableRaw === 'on' || enableRaw === 'yes';
				if (!asrEnabled) {
					console.warn('[FunASR] 未启用（VUE_APP_FUNASR_ENABLE≠true），跳过语音识别自动连接，避免远端 502');
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
						now_ipaddress: asrUrl
					});
					// 仅在显式启用时启动；连接失败由 getConnState 提示，不再反复重连
					this.funasr_start();
				} catch (error) {
					console.error('[FunASR] Failed to load module', error);
				}
			},

			async funasr_record() {
				let that = this;
				if (!that.wsconnecter || typeof that.wsconnecter.wsStart !== 'function') {
					console.warn('[FunASR] 远端未就绪，改用浏览器语音识别');
					this.startBrowserAsr();
					return;
				}
				// 1. 该方法能够重复执行 链接websocket
				const started = this.funasr_start();
				if (started !== 1) {
					console.warn('[FunASR] 连接失败，改用浏览器语音识别');
					this.startBrowserAsr();
					return;
				}
				// 2. 打开麦克风，录音会通过websocket发送后台
				try {
					stream = await navigator.mediaDevices.getUserMedia({ audio: true });
				} catch (e) {
					console.warn('[FunASR] 麦克风权限失败', e && e.message);
					that.$emit('son_send_msg', { type: 'mic_start_fail', reason: 'mic_denied' });
					return;
				}
				this.rec = Recorder({
					type: "pcm",
					bitRate: 16,
					sampleRate: 16000,
					sourceStream: stream,
					onProcess: that.recProcess
				});
				this.rec.open(function() {
					that.rec.start();
					that.start.disabled = true;
					that.stop.disabled = false;
					that.btnConnect.disabled = true;
					that.$emit('son_send_msg', { type: 'mic_start_ok', value: '' });
				});
			},

			// 识别启动、停止、清空操作
			funasr_start() {
				if (!this.wsconnecter || typeof this.wsconnecter.wsStart !== 'function') {
					if (this.info_div) this.info_div.innerHTML = "语音识别未配置或未启用";
					return 0;
				}
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
					return 1;
				} else {
					this.info_div.innerHTML = "连接asr服务器异常，请等待...";
					this.start.disabled = true;
					this.stop.disabled = true;
					this.btnConnect.disabled = false;
					return 0;
				}
			},

			/**
			 * stopState: 1:默认不关闭ws;2:关闭ws;
			 */
			funasr_stop(stopState = 1) {
				let that = this;
				const asrMode = (typeof getAsrMode === 'function'
					? getAsrMode()
					: (typeof window !== 'undefined' && typeof window.getAsrMode === 'function'
						? window.getAsrMode()
						: '2pass'));
				var chunk_size = new Array(5, 10, 5);
				var request = {
					"chunk_size": chunk_size,
					"wav_name": "h5",
					"is_speaking": false,
					"chunk_interval": 10,
					"mode": asrMode,
				};
				// console.log(request);
				if (!this.wsconnecter) {
					this.isRec = false;
					return;
				}
				if (this.sampleBuf && this.sampleBuf.length > 0) {
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
						if (stopState == 2 && that.wsconnecter) {
							that.wsconnecter.wsStop();
						}
						that.btnConnect.disabled = false;
						that.info_div.innerHTML = "请点击连接";
					}, 3000);
					if(this.rec) {
						this.rec.close();
						this.rec = undefined;
					}
					if(stream) {
						let tracks = stream.getTracks();
						if(tracks && tracks.length>0) tracks.forEach(item => item.stop());
						stream = undefined;
					}
				}
				// 停止连接
			},
			recProcess(buffer, powerLevel, bufferDuration, bufferSampleRate, newBufferIdx, asyncEnd) {
				// console.log(`funasr recProcess 发送数据===========>isRec：${isRec}`);
				if (this.isRec === true && !this.isStop) { // 
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
					// this.rec_text = this.offline_text;
					temp_rec_text = "";
					let msgObj = {
						type: `finished_msg`,
						value: `${this.offline_text}`,
						tempValue: `${rectxt}`,
					}
					this.receiveMsg(msgObj);
					// console.log(`is_final:${is_final},rec_text 1 if: rec_text:${this.rec_text},rectxt:${rectxt}`);
				} else {
					// this.rec_text = this.rec_text + rectxt; //.replace(/ +/g,"");
					temp_rec_text = temp_rec_text + rectxt; //.replace(/ +/g,"");
					let msgObj = {
						type: `changed_msg`,
						value: `${temp_rec_text}`,
						tempValue: `${rectxt}`,
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
				// this.varArea.value = "";
				this.rec_text = "";
				this.offline_text = "";
				// console.log(`funasr 执行清空逻辑……`)
			},

		}
	}
</script>

<style scoped>
</style>