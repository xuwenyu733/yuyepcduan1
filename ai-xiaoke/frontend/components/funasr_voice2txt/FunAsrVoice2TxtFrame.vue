<template>
	<div class="funasr-voice2txt">
		<div class="demo2" v-if="false">
			<div class="content">
				{{finalMsg}}
			</div>
			<button type="primary" @click.stop="myStart">开始说话</button>
			<button type="primary" @click.stop="myEnd">结束说话</button>
		</div>
		<div class="iframe_content">
			<iframe class="iframe" id="FunAsrFrame" align="center" height="1px" width="1px" :src="iframe_url" frameborder="no"
				border="0" marginwidth="0" marginheight="0" allow='microphone;camera;'></iframe>
			<!-- <web-div id="web_div" class="web-div no-scrollbar" :webdiv-styles="webdivStyles"
				:src="iframe_url" allow='microphone;camera;'></web-div> -->
		</div>
	</div>
</template>

<script>
	// import config from '@/config.js'
	export default {
		props: ['funasrKey'],
		data() {
			return {
				webdivStyles: {
					width: '1px'
				},
				iframe_url: `${process.env.VUE_APP_FUNASR_H5}?key=${this.funasrKey}`,
				btnStartTxt: '语音识别(请按下1s后再说话)',
				finalMsg: "",
				lastMsg: "",
				currMsg: "",
				isSpeaking: false,
			}
		},
		created() {
			let that = this;
			window.funasrReturnObj = {
				resolve: this.receiveMsg,
				now_ipaddress: process.env.VUE_APP_FUNASR_SOCKET,
			}
			this.receiveFrameMsg();
			// console.log(`============>config : `, process.env.VUE_APP_FUNASR_H5);
			console.log(`============>config : `, this.iframe_url);
		},
		mounted() {
		},
		destroyed() {
			// console.log(`funasr============>destroyed`);
		},
		methods: {
			prepareRecording() {
				let msgObj = {
					type: `mic_start_ok`,
					value: ``
				}
				this.$emit('son_send_msg', msgObj);
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
			},
			receiveMsg(msgObj) {
				console.log(`test funasr 1 ${JSON.stringify(msgObj)}`);
				if(msgObj.type == 'finished_msg') {
					if(msgObj.value && msgObj.value.indexOf("，")!=-1) {
						msgObj.value = msgObj.value.substring(1);
					}
					this.$emit('son_send_msg', msgObj);
					this.$emit('sonSendFinalMsg', msgObj.value);
				} else {
					this.$emit('son_send_msg', msgObj);
					this.$emit('sonSendMsg', msgObj.value);
				}
			},
			myStart() {
				this.isSpeaking = true;
				// console.log(`funasr ${name}============>myStart`);
				// funasr_record();
				this.sendMsg('', 'funasr_start');
			},
			myEnd() {
				this.isSpeaking = false;
				// funasr_stop(1);
				this.sendMsg('', 'funasr_stop');
				// console.log(`funasr ${name}============>myEnd`);
			},
			sendMsg(value_type, msg) {
				// console.log(`========>parent click`)
				//获取iframe元素
				var iframe = document.getElementById("FunAsrFrame");
				// console.log(`========>parent click `, iframe);
				//通过iframe的contentWindow给云服务器发送请求（这里省略了云服务器的url）
				// iframe.contentWindow.postMessage("export", "*");
				iframe.contentWindow.postMessage({
					type: 'parent_send_to_son',
					value_type: `${value_type}`,
					value: `${msg}`
				}, "*");
			},
			receiveFrameMsg() {
				let that = this;
				//允许任意URL发送消息，不会跨域
				window.postMessage("message", '*');
				//监听传过来的消息
				window.addEventListener('message', function(event) {
					// console.log(`event parent===${event.data.type}==${(event.data.type == 'son_send_to_parent')}=====>`, JSON.stringify(event.data));
					if (event.data.type == 'son_send_to_parent' && event.data.value_type == that.funasrKey) {
						// console.log(`event parent===2==${(event.data.type == 'son_send_to_parent')}=====>`, event)
						// console.log(`funasr ${that.funasrKey}====我父页面，收到子组件消息：${JSON.stringify(event.data)}`);
						that.handleFrameMsg(event.data.value);
					}
				})
			},
			handleFrameMsg(msg) {
				console.log(`handleFrameMsg===========>`, msg);
				if(msg && typeof msg == 'string' && msg.indexOf('_msg')!=-1) {
					let msgObj = JSON.parse(msg);
					this.receiveMsg(msgObj);
				}
			}
		}
	}
</script>

<style scoped>
	.iframe_content {
		position: fixed;
		bottom: 0;
	}
</style>