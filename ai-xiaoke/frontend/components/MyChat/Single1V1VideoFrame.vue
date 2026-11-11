<template>
	<div class="Single1V1Video">
		<video id="local" muted controls="controls"> </video>
		<video id="remote" controls="controls"> </video>
		<div v-if="isCreateOfferOwner">
			<button size="default" type="default" style="color:#ffffff;backgroundColor:#1AAD19;borderColor:#1AAD19" @click="shareScreenStream" v-if="isLocalStream"
				hover-class="is-hover">分享屏幕</button>
			<button size="default" type="default" style="color:#ffffff;backgroundColor:#1AAD19;borderColor:#1AAD19" @click="changeLocalStream" v-if="!isLocalStream"
				hover-class="is-hover">切回主画面</button>
		</div>
	</div>
</template>


<script>
	var PeerConnection = window.RTCPeerConnection ||
		window.mozRTCPeerConnection ||
		window.webkitRTCPeerConnection;
	export default {
		name: "Single1V1VideoFrame",
		props: ['socket', 'userInfo', 'chatTarget'],
		data() {
			return {
				localStream: null, //本地视频流
				remoteStream: null,
				isLocalStream: true,
				isCreateOfferOwner: false,
				offerOption: {
					offerToReceiveAudio: 1,
					offerToReceiveVideo: 1
				},
				remoteAccount: '',
				initOkFlag: false, // 初始化完成标志
				iceServers: {
					iceServers: [
						{
							url: "stun:stun.l.google.com:19302"
						}, // 谷歌的公共服务
						{
							url: 'turn:43.138.113.106:3478',
							credential: '123456',
							username: 'admin'
						},
					],
					sdpSemantics: 'plan-b'
				},
				pc: '',

			}
		},
		created() {
			console.log(`初始化聊天窗口userInfo:${JSON.stringify(this.userInfo)},chatTarget:${JSON.stringify(this.chatTarget)}`);
			this.init()
		},
		mounted() {},
		methods: {
			async init() {
				this.initOkFlag = false;
				await this.createNative();
				if (!this.localStream) {
					console.log(`========>localStream 为空`);
					return;
				}
				await this.nativeMedia();
				await this.initPeer();
				await this.onListener();
				this.initOkFlag = true;
			},
			//创建本地流全局放置
			async createNative() {
				const that = this;
				that.localStream = await this.createMedia();
				if (!that.localStream) {
					console.log(`========>localStream 为空`);
					return;
				}
			},
			// 本地摄像头打开
			nativeMedia() {
				const that = this;
				console.log("open local video", that.localStream)
				let video = document.querySelector('#local');
				console.log("open local video ===>", ("srcObject" in video))
				video.srcObject = that.localStream;
    			video.volume = 0;
				// eslint-disable-next-line no-unused-vars
				video.onloadedmetadata = function(e) {
					video.play();
				};
			},
			// 创建媒体流
			async createMedia() {
				const that = this;
				let streamTep = null;
				console.log("start prepare localStream origin")
				if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
					console.log("设备不支持")
					console.log('getUserMedia is not support!');
					return null;
				}
				await navigator.mediaDevices.getUserMedia({
						audio: true,
						video: true
					})
					.then(function(mediaStream) {
						console.log("mediaStream", mediaStream)
						streamTep = mediaStream;
					}).catch(error => {
						console.error("获取媒体设备异常", error)
						// that.$message.warning("获取媒体设备异常")
					})
				return streamTep;
			},
			//分享屏幕
			shareScreenStream() {
				const that = this;
				const displayMediaStreamConstraints = {
					video: {
						cursor: "always"
					},
					audio: true
				};
				if (navigator.mediaDevices.getDisplayMedia) {
					navigator.mediaDevices.getDisplayMedia(displayMediaStreamConstraints).then(function(mediaStream) {
						console.log("mediaStream", mediaStream)
						that.shareStream = mediaStream;
						that.pc.removeStream(that.localStream)
						that.pc.addStream(mediaStream)
						that.onCreateOffer();
						let video = document.querySelector('#local');
						if ("srcObject" in video) {
							video.srcObject = that.shareStream;
						} else {
							video.src = window.URL.createObjectURL(that.shareStream);
							video.volume = 0
						}
						// eslint-disable-next-line no-unused-vars
						video.onloadedmetadata = function(e) {
							video.play();
						};
						that.isLocalStream = false;
					}).catch(error => {
						console.log("error", error)
						console.log("媒体设备获取异常，请检查设备或浏览器是否支持")
					});
				} else {
					console.log("navigator.mediaDevices.getDisplayMedia  false");
					console.log("浏览器不不支持")
					// navigator.getDisplayMedia(displayMediaStreamConstraints).then(success).catch(error);
				}
			},
			//切回本地摄像头
			changeLocalStream() {
				const that = this;
				that.pc.removeStream(that.shareStream)
				that.pc.addStream(that.localStream)
				that.onCreateOffer();
				that.nativeMedia();
				that.isLocalStream = true;
			},
			//关闭本地画面
			closeNativeVideo() {
				let video = document.querySelector('#local');
				video.srcObject = null;
				
				if(!this.localStream) return;
				let tracks = this.localStream.getTracks();
				if(tracks && tracks.length>0) tracks.forEach(item => item.stop());
				this.localStream = undefined;
			},
			//挂断
			hangUp() {
				
				// 停止所有媒体流
				if (this.localStream) {
					this.localStream.getTracks().forEach(track => track.stop());
					this.localStream = null;
				}

				if (this.shareStream) {
					this.shareStream.getTracks().forEach(track => track.stop());
					this.shareStream = null;
				}

				if (this.remoteStream) {
					this.remoteStream.getTracks().forEach(track => track.stop());
					this.remoteStream = null;
				}

				this.closeNativeVideo();
				if (this.pc) {
					this.pc.close();
				}

				// 清除视频元素
				const localVideo = document.querySelector('#local');
				const remoteVideo = document.querySelector('#remote');
				if (localVideo) localVideo.srcObject = null;
				if (remoteVideo) remoteVideo.srcObject = null;
				return true;
			},
			//初始化 PeerConnection
			initPeer() {
				const that = this;
				that.pc = new PeerConnection(that.iceServers);
				that.pc.addStream(that.localStream);
				that.pc.onicecandidate = function(event) {
					console.log("监听ice候选信息 ", event.candidate)
					if (event.candidate) {
						let candidate_data = {
							userId: that.userInfo.userId,
							username: that.userInfo.username,
							candidate: event.candidate
						}
						let params = {
							userId: that.userInfo.userId,
							targetId: that.chatTarget.userId,
							targetName: that.chatTarget.chatName,
							targetType: that.chatTarget.type,
							data: candidate_data
						}
						that.socket.emit("candidate", params)
					} else {
						console.log("ICE收集已经完成")
					}
				};
				that.pc.onaddstream = (event) => {
					console.log("监听到视频加入 onaddstream ", event)
					let video = document.querySelector('#remote');
					video.srcObject = event.stream;
					// eslint-disable-next-line no-unused-vars
					video.onloadedmetadata = function(e) {
						video.play();
					};
				};
			},
			//监听服务器信息
			onListener() {
				const that = this;
				that.socket.on("candidate", function(e) {
					console.log("服务器发送 candidate", e)
					that.onIceCandidate(e)
				})
				that.socket.on("join", function(e) {
					console.log("服务器发送 join", e)
					that.onJoin(e)
				})
				that.socket.on("offer", function(e) {
					console.log("服务器发送 offer", e)
					that.onOffer(e)
				})
				that.socket.on("answer", function(e) {
					console.log("服务器发送 answer", e)
					that.onAnswer(e)
				})
			},
			//监听 Ice 候选
			async onIceCandidate(data) {
				const that = this;
				try {
					await that.pc.addIceCandidate(data.data.candidate)
				} catch(error) {
				    console.log(`onIceCandidate==========>`, error);
				}
			},
			//监听远端offer
			async onOffer(data) {
				const that = this;
				try {
					await that.pc.setRemoteDescription(data.data.offer)
				} catch(error) {
				    console.log(`onOffer==========>`, error);
				}
				// 接收端创建 answer
				let answer = await that.pc.createAnswer();
				// 接收端设置本地 answer 描述
				await that.pc.setLocalDescription(answer);
				//发送到呼叫端 answer
				let answer_data = {
					userId: that.userInfo.userId,
					username: that.userInfo.username,
					answer: answer
				}
				let params = {
					userId: that.userInfo.userId,
					targetId: data.data.userId,
					targetName: data.data.username,
					targetType: data.targetType,
					data: answer_data
				}
				that.socket.emit("answer", params)
			},
			//监听远程响应
			async onAnswer(data) {
				const that = this;
				// 发送端 设置远程 answer 描述
				await that.pc.setRemoteDescription(data.data.answer);
			},
			//创建连接
			async onCreateOffer() {
				const that = this;
				that.isCreateOfferOwner = true;
				//创建offer
				let offer = await that.pc.createOffer(this.offerOption);
				console.log("呼叫端 创建 offer", offer)
				//设置本地描述
				await that.pc.setLocalDescription(offer)
				//远程发送到服务器
				let data = {
					offer: offer,
					userId: that.userInfo.userId,
					username: that.userInfo.username,
					info: "发送offer"
				}
				let params = {
					userId: that.userInfo.userId,
					targetId: that.chatTarget.userId,
					targetName: that.chatTarget.chatName,
					targetType: that.chatTarget.type,
					data: data
				}
				that.socket.emit("offer", params)
			},

			/**
			 * 检查初始化状态
			 */
			isInitialized() {
				return this.initOkFlag;
			}
		},
		watch: {

		},
		destroyed() {
			console.log("销毁子组件")
		}
	}
</script>

<style scoped>
	.Single1V1Video {
		display: flow;
		background-color: black;
	}

	.Single1V1Video video {
		object-fit: fill;
		width: 320px;
		height: 200px;
	}
</style>