<template>
	<div class="aliyun-txt2voice">
	</div>
</template>

<script>
	import '@/components/aliyun_txt2voice/app.js';
	import {
		setToken,
		removeToken,
		getUserInfo
	} from '@/utils/auth';

	// 断句
	let splitItems = [`：`, `。`, `？`, '！'];
	let replaceItems = ['<br>', '\n', ' ', '*'];
	function punctuation(str) {
		let parts = [];
		let start = 0;
		let end = 0;
		for(let i = 0;i<str.length;i++) {
			let curr_t = str[i];
			let curr_end = '';
			for(let j=0;j<splitItems.length;j++) {
				if(curr_t == splitItems[j]) {
					curr_end = splitItems[j];
					end = i+1;
					break;
				}
			}
			if(curr_end) {
				parts.push(str.substring(start, end));
				start = end;
			}

		}
		parts = parts.map(i => {
			let t = i;
			replaceItems.forEach(ii => {
				t = t.replaceAll(ii, '');
			
			});
			return t;
		});
		return parts;
	}
	export default {
		data() {
			return {
				init_uuid: '',
				currMsg: '',
				txt2voice_flag: false,
				userInfo: {},
				myToken: '',
				isSpeeching: false,
				needPlayList: [],
				hasPlayList: [],
				interval: false, 
				stopCount: 0,
				sendCount: 0,
			}
		},
		created() {
			this.create_interval();
			this.getUUID();
			// this.userInfo = getUserInfo();
		},
		methods: {
			exe_play(msg) {
				let that = this;
				let isStart = judge_isStart();
				// debugger
				// return;
				if(isStart) {
					// console.log(`====isStart:${isStart} 1============>开始播放 exe_play 1`);
					start_txt2voice(msg);
				} else {
					// console.log(`====isStart:${isStart} 2============>开始播放 exe_play 2`);
					start_connect(that.myToken, 
					() => {
						isStart = judge_isStart();
						// console.log(`=======isStart:${isStart} 3=========>开始播放 exe_play 3 `);
						start_txt2voice(msg);
					}, 
					() => { // startFunc
						if(!that.isSpeeching) that.$emit('son_startPlay');
						that.isSpeeching = true;
						// console.log(`=======isStart:${isStart} 3=========>开始播放 exe_play 0 `);
					},
					() => { // endFunc
						that.$emit('son_endPlay');
						that.isSpeeching = false;
						// console.log(`=======isStart:${isStart} 3=========>结束播放 exe_play 4 `);
					});
				}
			},
			create_interval() {
				if(this.interval) return;
				let that = this;
				this.interval = setInterval(() => {
					// console.log(`interval exe =============>${new Date()}`);
					if(that.needPlayList.length == 0) {
						this.sendCount = 0;
						if(that.stopCount > 10 && judge_isStart()) {
							stop_txt2voice();
							that.stopCount = 0;
						}
						that.stopCount += 1;
						// console.log(`interval exe clear =============>${new Date()}`);
						// clearInterval(that.interval);
						// this.interval = null;
						return;
					}
					that.stopCount = 0;
					if(this.sendCount % 3 == 0) {
						let msg = this.needPlayList.splice(0, 1)[0];
						that.hasPlayList.push(msg);
						that.exe_play(msg);
					}
					this.sendCount += 1;
				}, 300);
			},
			clear_interval() {
				// setTimeout(() => {
				// });
			},
			splitStrIntoParts(str, partSize = 50) {
				// let parts = [];
				// for(let i = 0;i<str.length;i+=partSize) {
				// 	parts.push(str.substring(i, i+partSize));
				// }
				// return parts;
				return punctuation(str);
			},
			myStart(msg) {
				let parts = this.splitStrIntoParts(msg, 50);
				this.needPlayList= [...this.needPlayList, ...parts];
				let that = this;
				this.txt2voice_flag = true;
				// console.log(`AliyunTxt2Voice myStart=======================>msg:${msg}`);
				if(that.interval) return;
				else that.create_interval();
			},
			myEnd() {
				this.txt2voice_flag = false;
				this.needPlayList = [];
				stop_txt2voice();
				stop_play();
			},
			getUUID(msg, func) {
				let that = this;
				that.myToken = "ea1c3660f39f43f29081fee934ec36fb";
				// console.log(`getUUID=======>${this.jq}`);
				// let postUrl = "http://localhost:9391/aliyun/genUUID";
				// let postUrl = `${process.env.VUE_APP_ROOT_API}/uuid/aliyun/genUUID`;
				let postUrl = "https://www.kdaqua.cn/kede_common_api/aliyun/genUUID";
				// let postUrl = "https://proxy.kedeyuye.online/kede_common_api/aliyun/genUUID";
				this.jq.ajax({
					url: `${postUrl}`,
					data: JSON.stringify({ preUUID: 'defaa81f4e1d418787e2877c3d47ce4b' }),
					type: "POST",
      				contentType: "application/json", // 设置内容类型为JSON
					// dataType: "json",
					success: function(data) {
						// data = JSON.parse(data);  //dataType指明了返回数据为json类型，故不需要再序列化
						console.log("getUUID==============>"+JSON.stringify(data));
						if(data.data && !func) {
							that.myToken = data.data;
						}else if(data.data && func) {
							that.myToken = data.data;
							func(data.data, msg);
						}
					},
					error: function(err) {
						console.log("getUUID==============>err:"+JSON.stringify(err));
					}
				});
			}
		}
	}
</script>

<style lang="scss" scoped>

</style>