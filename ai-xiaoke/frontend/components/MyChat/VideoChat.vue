<template>
	<div class="home">
		<!-- 视频通话框1V1-->
		<el-dialog title="视频通话中" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false"
			:modal="false" :visible.sync="dialogSingle1V1VideoFlag">
			<div v-if="dialogSingle1V1VideoFlag && socket">
				<Single1V1VideoFrame :socket="socket" :userInfo="userInfo" :chatTarget="chatTarget"
					ref="dialogSingle1V1VideoRef"></Single1V1VideoFrame>
				<div style="width: 100%;padding: 14px 0;">
					<el-button type="primary" @click="hangUpVideo">挂断</el-button>
				</div>
			</div>
		</el-dialog>
		<!-- 语音通话框1V1-->
		<el-dialog title="语音通话中" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false"
			:modal="false" :visible.sync="dialogSingle1V1PhoneFlag">
			<div v-if="dialogSingle1V1PhoneFlag && socket">
				<Single1V1PhoneFrame :socket="socket" :userInfo="userInfo" :chatTarget="chatTarget"
					ref="dialogSingle1V1PhoneRef"></Single1V1PhoneFrame>
				<div style="width: 100%;padding: 14px 0;">
					<el-button type="primary" @click="hangUpPhone">挂断</el-button>
				</div>
			</div>
		</el-dialog>
	</div>

</template>

<script>
import io from 'socket.io-client';
import Single1V1VideoFrame from "./Single1V1VideoFrame";
import Single1V1PhoneFrame from "./Single1V1PhoneFrame.vue";
import { UserInfoKey } from "@/utils/constants";
import {
	setToken,
	removeToken,
	getUserInfo
} from '@/utils/auth';

/** 规范化本地 IM Socket 地址；缺 host 时回退 127.0.0.1:9091，避免刷 http://:9091 */
function resolveSocketBaseUrl() {
	const raw = String(process.env.VUE_APP_SOCKET_BACE_URL || '').trim().replace(/^['"]|['"]$/g, '');
	if (!raw) return 'http://127.0.0.1:9091';
	// socket.io 轮询用 http(s)；ws(s) 可转
	let url = raw.replace(/^ws:/i, 'http:').replace(/^wss:/i, 'https:');
	try {
		const u = new URL(url);
		if (!u.hostname) {
			u.hostname = '127.0.0.1';
			if (!u.port) u.port = '9091';
			url = u.toString().replace(/\/$/, '');
		}
		return url;
	} catch (e) {
		return 'http://127.0.0.1:9091';
	}
}
let socketBaseUrl = resolveSocketBaseUrl();

export default {
	name: 'VideoChat',
	props: {
		// "targetUserInfo": {
		// 	type: Object,
		// 	default() {
		// 		return {};
		// 	}
		// }
	},
	components: {
		Single1V1VideoFrame,
		Single1V1PhoneFrame
	},
	data() {
		return {
			isInit: true,
			socket: '',
			chatTarget: {
				type: 1
			}, //聊天对象
			messageForNew: '', //新消息发送
			dialogSingle1V1VideoFlag: false, //1v1视频聊天对话框
			dialogSingle1V1PhoneFlag: false, //1v1语音聊天对话框
			userInfo: {},
			currUserId: 3,
			targetUserId: 4,

			content: '',
			show4: false,
			show5: false,
			currE: null,
		}
	},
	created() {
		this.userInfo = JSON.parse(localStorage.getItem(UserInfoKey));
		// console.log(`VideoChat.vue====>userInfo:${JSON.stringify(this.userInfo)}`);
		this.currUserId = this.userInfo.userId;
		this.userInfo = {
			...this.userInfo,
			nickname: this.userInfo.username
		};

		console.log(`VideoChat.vue====>targetUserInfo:${JSON.stringify(socketBaseUrl)}`);
		this.init();
		// console.log(`VideoChat.vue====>userInfo:${JSON.stringify(this.userInfo)}`);
	},
	mounted() { },
	methods: {
		//初始化基础信息
		async init() {
			const that = this;
			that.isInit = true;
			//初始化socket
			if (this.userInfo) {
				this.initSocket(this.currUserId)
			}
			//监听消息
			that.onListener()
		},
		//初始化socket链接
		async initSocket(username) {
			let params = {
				query: {
					uid: username,
					deviceType: 'pc'
				},
				// 本地无 IM 服务时避免无限重连刷屏
				reconnection: true,
				reconnectionAttempts: 5,
				reconnectionDelay: 2000,
				timeout: 8000
			};
			this.socket = this.$store.getters.videoSocket;
			if (!this.socket) {
				console.log(`=======》重新设置socket-io ${socketBaseUrl}`);
				this.socket = io.connect(socketBaseUrl, params);
				this.socket.on('connect_error', (err) => {
					console.warn('[VideoChat] socket connect_error:', err && err.message)
				});
				this.$store.dispatch('setVideoSocket', this.socket);
			} else {
				console.log(`=======》复用视频socket-io ${socketBaseUrl}`);
			}
		},
		//发送消息
		sendMessageToTarget() {
			if (!this.messageForNew) {
				if (this.imageUrl) {
					this.$message.info("图片发送中")
				} else {
					this.$message.error("消息不能为空")
					return;
				}
			}
			let message = {
				username: this.userInfo.username,
				nickname: this.userInfo.nickname,
				imageUrl: '',
				createTime: this.formatDate(new Date()),
				avatarUrl: this.userInfo.avatarUrl,
				message: this.messageForNew
			}
			message.imageUrl = this.imageUrl
			this.chatHistoryList.push(message)
			let params = {
				userId: this.userInfo.userId,
				targetId: this.targetUserId,
				targetName: this.chatTarget.chatName,
				targetType: this.chatTarget.type,
				data: message
			}
			this.socket.emit('sendMessage', params)
			this.messageForNew = ''
			this.imageUrl = ''
		},
		formatDate(inputTime) {
			var date = new Date(inputTime);
			var y = date.getFullYear();
			var m = date.getMonth() + 1;
			m = m < 10 ? ('0' + m) : m;
			var d = date.getDate();
			d = d < 10 ? ('0' + d) : d;
			var h = date.getHours();
			h = h < 10 ? ('0' + h) : h;
			var minute = date.getMinutes();
			var second = date.getSeconds();
			minute = minute < 10 ? ('0' + minute) : minute;
			second = second < 10 ? ('0' + second) : second;
			return y + '-' + m + '-' + d + ' ' + h + ':' + minute + ':' + second;
		},
		//监听正在输入
		changeValue() {
			console.log('正在输入')
		},
		phoneWithPartner() {
			//单人通话
			this.dialogSingle1V1PhoneFlag = true;
			let data = {
				type: 1,
				info: "呼叫",
				userId: this.userInfo.userId,
				username: this.userInfo.username
			}
			let params = {
				userId: this.userInfo.userId,
				targetId: this.chatTarget.userId,
				targetName: this.chatTarget.chatName,
				targetType: this.chatTarget.type,
				data: data
			}
			console.log(`开启语音聊天===========》${JSON.stringify(params)}`);
			this.socket.emit("1V1CommunicatePhone", params);
		},
		//视频聊天
		videoWithPartner(targetUserInfo) {
			this.targetUserInfo = targetUserInfo;
			// console.log(`VideoChat.vue 1====>targetUserInfo:${(JSON.stringify(this.targetUserInfo))}`);
			if (this.targetUserInfo && this.targetUserInfo.currUserId) {
				this.chatTarget = {
					nickname: this.targetUserInfo.currUserName,
					type: 1,
					chatName: this.targetUserInfo.currUserName,
					userId: this.targetUserInfo.currUserId
				};
				this.targetUserId = this.chatTarget.userId;
			}
			console.log(`VideoChat.vue====>chatTarget:${JSON.stringify(this.chatTarget)}`);

			this.dialogSingle1V1VideoFlag = true;
			// return;
			let data = {
				type: 1,
				info: "呼叫",
				userId: this.userInfo.userId,
				username: this.userInfo.username
			}
			let params = {
				userId: this.currUserId,
				targetId: this.chatTarget.userId,
				targetName: this.chatTarget.username,
				targetType: this.chatTarget.type,
				data: data
			}
			console.log(`开启视频聊天 1 ===========》${JSON.stringify(params)}`);
			this.socket.emit("1V1CommunicateVideo", params);
		},
		//1v1 视频通话
		hangUpVideo() {
			let data = {
				type: 3,
				info: "挂断",
				username: this.userInfo.username
			}
			let params = {
				userId: this.userInfo.userId,
				targetId: this.chatTarget.userId,
				targetName: this.chatTarget.chatName,
				targetType: this.chatTarget.type,
				data: data
			}
			this.socket.emit("1V1CommunicateVideo", params)
			console.log(`hangUpVideo params:${JSON.stringify(params)}===>chatTarget:${JSON.stringify(this.chatTarget)}`);
			this.$refs.dialogSingle1V1VideoRef.hangUp();
			this.dialogSingle1V1VideoFlag = false;
			// this.$refs['dialogSingle1V1VideoRef'].hangUp();
			// this.$router.go(0)
		},
		//1v1 phone
		hangUpPhone() {
			let data = {
				type: 3,
				info: "挂断",
				username: this.userInfo.username
			}
			let params = {
				userId: this.userInfo.userId,
				targetId: this.chatTarget.userId,
				targetName: this.chatTarget.chatName,
				targetType: this.chatTarget.type,
				data: data
			}
			this.socket.emit("1V1CommunicatePhone", params);
			this.$refs['dialogSingle1V1PhoneRef'].hangUp();
			this.dialogSingle1V1PhoneFlag = false;
			// this.$router.go(0)
		},
		//监听消息
		onListener() {
			const that = this;
			that.socket.on('connect', function () {
				console.log({
					title: '提示',
					message: '服务链接',
					type: 'success'
				})
			})
			that.socket.on('disconnect', function () {
				console.log({
					title: '提示',
					message: '服务断线',
					type: 'warn'
				})
			});
			that.socket.on("sendMessage", function (e) {
				console.log("sendMessage =======>", e)
				//1.点对点
				if (e.targetType === 1) {
					console.log({
						title: '提示',
						message: '收到' + e.data.username + '的消息',
						type: 'info'
					});
					//2.群聊
				} else {
					console.log({
						title: '提示',
						message: '收到群聊 ' + e.targetName + '的消息',
						type: 'info'
					});
				}
				that.chatHistoryList.push(e.data)
				if (e.userId && e.targetType) {
					let tempRef = 'obj' + e.userId + '' + e.targetType + 'obj';
					this.chatObjectCSS = null;
					that.changeCss(tempRef)
				}

			});
			that.socket.on("1V1CommunicatePhone", function (e) {
				console.log("1V1CommunicatePhone 0 ==========>", e);
				//呼叫
				if (e.data.type === 1) {
					that.$confirm('用户' + e.data.username + ' 是申请语音通话，是否接听?', '提示', {
						closeOnClickModal: false,
						closeOnPressEscape: false,
						confirmButtonText: '确定',
						cancelButtonText: '取消',
						type: 'warning'
					}).then(() => {
						let data = { type: 2, info: "接通", username: that.userInfo.username }
						let params = { userId: that.userInfo.userId, targetId: e.data.userId, targetName: e.data.username, targetType: e.targetType, data: data }
						that.socket.emit("1V1CommunicatePhone", params)
						that.dialogSingle1V1PhoneFlag = true;
					}).catch(() => {
						let data = { type: 3, info: "拒接", username: that.userInfo.username }
						let params = { userId: that.userInfo.userId, targetId: e.data.userId, targetName: e.data.username, targetType: e.targetType, data: data }
						that.socket.emit("1V1CommunicatePhone", params)
					});
					//用户接通通知
				} else if (e.data.type === 2) {
					console.log({
						type: 'info',
						message: e.data.username + '用户' + e.data.info
					});
					// console.log(that.$refs)
					//接通后创建offer
					// setTimeout(function () {
					// 	that.$refs['dialogSingle1V1PhoneRef'].onCreateOffer();
					// }, 2000)
					
					let flag = false;
					let interval = setInterval(() => {
						if(!that.$refs['dialogSingle1V1PhoneRef']) return;
						let initOk = that.$refs['dialogSingle1V1PhoneRef'].isInitialized();
						if(!initOk) return;
						if(interval) clearInterval(interval);
						if(flag) return;
						flag = true;
						that.$refs['dialogSingle1V1PhoneRef'].onCreateOffer();
					}, 500);
					//拒接和挂断通知
				} else if (e.data.type === 3) {
					console.log({
						type: 'error',
						message: e.data.username + '用户' + e.data.info
					});
					// console.log(`dialogSingle1V1PhoneRef 挂断===>`, that.$refs.dialogSingle1V1PhoneRef);
					if (that.$refs.dialogSingle1V1PhoneRef) that.$refs.dialogSingle1V1PhoneRef.hangUp();
					// that.hangUpPhone();
					that.dialogSingle1V1PhoneFlag = false;
					// that.$router.go(0)
				}


			});
			that.socket.on("1V1CommunicateVideo", function (e) {
				that.currE = e;
				// console.log("1V1CommunicateVideo 2 =====>收到视频通话需求：", e);
				//呼叫
				if (e.data.type === 1) {
					that.$confirm('用户' + e.data.username + ' 是申请视频通话，是否接听?', '提示', {
						closeOnClickModal: false,
						closeOnPressEscape: false,
						confirmButtonText: '确定',
						cancelButtonText: '取消',
						type: 'warning'
					}).then(() => {
						// let data = { type: 2, info: "接通", username: that.userInfo.username }
						// let params = { userId: that.userInfo.userId, targetId: e.data.userId, targetName: e.data.username, targetType: e.targetType, data: data }
						// that.socket.emit("1V1CommunicateVideo", params)
						// that.dialogSingle1V1VideoFlag = true;
						that.receivceVideo();
					}).catch(() => {
						// let data = { type: 3, info: "拒接", username: that.userInfo.username }
						// let params = { userId: that.userInfo.userId, targetId: e.data.userId, targetName: e.data.username, targetType: e.targetType, data: data }
						// that.socket.emit("1V1CommunicateVideo", params)
						that.receivceVideo();
					});
					//用户接通通知
				} else if (e.data.type === 2) {
					console.log({
						type: 'info',
						message: e.data.username + '用户' + e.data.info
					});
					console.log(that.$refs)
					//接通后创建offer
					// setTimeout(function () {
					// 	that.$refs['dialogSingle1V1VideoRef'].onCreateOffer();
					// }, 2000)
					
					let flag = false;
					let interval = setInterval(() => {
						if(!that.$refs['dialogSingle1V1VideoRef']) return;
						let initOk = that.$refs['dialogSingle1V1VideoRef'].isInitialized();
						if(!initOk) return;
						if(interval) clearInterval(interval);
						if(flag) return;
						flag = true;
						that.$refs['dialogSingle1V1VideoRef'].onCreateOffer();
					}, 500);
					//拒接和挂断通知
				} else if (e.data.type === 3) {
					console.log({
						type: 'error',
						message: e.data.username + '用户' + e.data.info
					});
					// console.log(`dialogSingle1V1VideoRef 挂断===>`, that.$refs.dialogSingle1V1VideoRef);
					if (that.$refs.dialogSingle1V1VideoRef) that.$refs.dialogSingle1V1VideoRef.hangUp();
					// that.hangUpVideo();
					that.dialogSingle1V1VideoFlag = false;
					// that.hangUpVideo();
					// that.$router.go(0)
				}
			});
			that.socket.on("notOnline", function (e) {
				console.log(e)
			});
		},
		receivcePhone() {
			this.show4 = false;
			let that = this;
			that.dialogSingle1V1PhoneFlag = true;
			
			let flag = false;
			let interval = setInterval(() => {
				if(!that.$refs['dialogSingle1V1PhoneRef']) return;
				let initOk = that.$refs['dialogSingle1V1PhoneRef'].isInitialized();
				if(!initOk) return;
				if(interval) clearInterval(interval);
				if(flag) return;
				flag = true;
				
				let data = {
					type: 2,
					info: "接通",
					username: that.userInfo.username
				}
				let params = {
					userId: that.userInfo.userId,
					targetId: e.data.userId,
					targetName: e.data.username,
					targetType: e.targetType,
					data: data
				}
				that.chatTarget = {
					nickname: e.data.username,
					type: 1,
					chatName: e.data.username,
					userId: e.data.userId
				};
				that.targetUserId = that.chatTarget.userId;
				that.socket.emit("1V1CommunicatePhone", params)
			}, 500);
		},
		rejectPhone() {
			this.show5 = false;
			let e = this.currE;
			let that = this;

			let data = {
				type: 3,
				info: "拒接",
				username: that.userInfo.username
			}
			let params = {
				userId: that.userInfo.userId,
				targetId: e.data.userId,
				targetName: e.data.username,
				targetType: e.targetType,
				data: data
			}
			that.socket.emit("1V1CommunicatePhone", params);
		},
		receivceVideo() {
			this.show5 = false;
			let that = this;
			that.dialogSingle1V1VideoFlag = true;
			
			let flag = false;
			let interval = setInterval(() => {
				if(!that.$refs['dialogSingle1V1VideoRef']) return;
				let initOk = that.$refs['dialogSingle1V1VideoRef'].isInitialized();
				if(!initOk) return;
				if(interval) clearInterval(interval);
				if(flag) return;
				flag = true;
				
				let e = this.currE;
				let data = {
					type: 2,
					info: "接通",
					username: that.userInfo.username
				}
				let params = {
					userId: that.userInfo.userId,
					targetId: e.data.userId,
					targetName: e.data.username,
					targetType: e.targetType,
					data: data
				}
				that.chatTarget = {
					nickname: e.data.username,
					type: 1,
					chatName: e.data.username,
					userId: e.data.userId
				};
				that.targetUserId = that.chatTarget.userId;
				// console.log(`==========>接受视频：${JSON.stringify(that.chatTarget)}`);
				that.socket.emit("1V1CommunicateVideo", params)
			}, 500);
		},
		rejectVideo() {
			this.show5 = false;
			let e = this.currE;
			let that = this;
			let data = {
				type: 3,
				info: "拒接",
				username: that.userInfo.username
			}
			let params = {
				userId: that.userInfo.userId,
				targetId: e.data.userId,
				targetName: e.data.username,
				targetType: e.targetType,
				data: data
			}
			that.socket.emit("1V1CommunicateVideo", params)
		},
	},
	watch: {

	}
}
</script>
<style lang="scss" scoped>
.home {
	padding: 50px;

	.my_button {
		width: 80px;
		height: 44px;
		border: 1px solid green;
		display: flex;
		justify-content: center;
		align-items: center;
		margin-right: 7px;
	}

	.video_comtent {
		.input_area {
			.input_ele {
				display: flex;
				flex-direction: column;
				margin: 7px 0;

				.uni-column {
					display: flex;
					margin: 7px 0;

					.title {
						display: flex;
						align-items: center;
						justify-content: center;
						// height: 70rpx;
						// background-color: green;
						margin-right: 14px;
					}

					.uni-input {
						// background-color: green;
						// height: 70rpx;
						width: 50%;
						line-height: 70rpx;
						padding: 7px 19px;
						border: 1px solid #eee;
					}
				}
			}

			.operation {
				width: 100%;
				display: flex;
				margin: 7px 0;

			}
		}
	}
}

.el-header {
	text-align: left;
	border-bottom: 1px gainsboro solid;
	line-height: 60px;
	opacity: 0.5
}

/**个人信息**/
.el-aside01 {
	background-color: #161c23;
	padding-top: 20px;
	height: 750px;
}

.el-aside01 .me-menu-logout {
	margin-top: 30px;
	font-size: 30px;
	cursor: pointer;
	font-weight: bold;
}

.el-aside01 .me-menu-friends {
	margin-top: 30px;
	font-size: 30px;
	cursor: pointer;
	font-weight: bold;
}

/**群聊信息列表**/
.el-aside02 {
	padding-top: 10px;
	background-color: #d7d4dc;
	opacity: 0.7;
}

@keyframes msg-me {
	from {
		background: #fff6ef;
	}

	to {
		background: #ff4fa5;
	}
}

@-webkit-keyframes msg-me

/* Safari 与 Chrome */
	{
	from {
		background: #fff6ef;
	}

	to {
		background: #ff4fa5;
	}
}

/**群聊对象整体设置**/
.el-aside02 .chatObject {
	height: 35px;
	/*border: 1px silver solid;*/
	border-bottom: 1px gray solid;
	padding: 5px;
	transition-duration: 0.5s;
	display: flex;
	/*animation:msg-me  2s infinite;*/
	/*-webkit-animation:msg-me 2s infinite;*/
}

.el-aside02 .chatObject:hover {
	background: rgba(42, 54, 71, 0.16);
}

/**群聊对象名称字体标签设置**/
.el-aside02 .chatObject .chatObjectNameLabel {
	margin-top: 4px;
	margin-left: 4px;
	font-size: 13px;
	font-family: Arial, sans-serif;
	overflow: hidden;
	/**超出部分隐藏**/
	text-overflow: ellipsis;
	/**超出部分添加省略号**/
	white-space: nowrap;
	/**不换行**/
}

/**聊天框内容设置**/
.chatContentDiv {
	display: flex;
	justify-content: flex-start;
	clear: both;
}

.el-main {
	border-left: 1px darkgray solid;
	background-color: white;
	height: 500px;
	width: 80vw;
	line-height: 35px;
	opacity: 0.7;
}

/**聊天内容用户显示**/
.el-main .chatContentDiv .chatNameAndMessage {
	/*border: 1px red solid;*/
	text-align: left;
	/*width: 40vw;*/

}

.chatContentDiv .chatNameAndMessage .chatNameAndMessage-name {
	display: block;
	/*height: 10px;*/
	font-size: 14px;
}

.chatContentDiv .chatNameAndMessage .chatNameAndMessage-message {
	display: block;
	font-size: 18px;
}

.el-footer {
	background: white;
	border-top: 1px gray solid;

}

/**发送消息框**/
.chatMessageSend .el-input__inner {
	background: none;
	outline: none;
	border: none;
	font-size: 25px;
}

/**消息框工具栏目**/
.eventForChat {
	text-align: left;
	display: flex;
}

.eventForChat i {
	margin-left: 9px;
}

.eventForChat i:hover {
	cursor: pointer;
}

.eventForChat i:hover:before {
	/*content:attr(data-tooltip);*/
}
</style>