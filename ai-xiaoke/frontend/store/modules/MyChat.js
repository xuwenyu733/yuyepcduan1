// import storage from '@/utils/storage'
// import constant from '@/utils/constant_local'

const MyChat = {
	state: {
		showFlag_MyChat: true, // 用户编号
		videoSocket: '', // 视频通话 socket
		my_that: null, // that对象
	},

	mutations: {
		SET_SHOW_FLAG: (state, showFlag_MyChat) => {
			state.showFlag_MyChat = showFlag_MyChat
		},
		SET_VIDEO_SOCKET: (state, videoSocket) => {
			state.videoSocket = videoSocket
		},
		SET_MY_THAT: (state, my_that) => {
			state.my_that = my_that;
		},
	},

	actions: {
		// 是否展示MyChat
		setIsShowMyChat({
			commit,
			state
		}, showFlag) {
			// console.log(`setIsShowMyChat===========>`,showFlag);
			commit('SET_SHOW_FLAG', showFlag);
		},
		// 视频聊天的 socket
		setVideoSocket({
			commit,
			state
		}, videoSocket) {
			// console.log(`setIsShowMyChat===========>`,showFlag);
			commit('SET_VIDEO_SOCKET', videoSocket);
		},
		// 视频聊天的 socket
		setMyThat({
			commit,
			state
		}, my_that) {
			// console.log(`setIsShowMyChat===========>`,showFlag);
			commit('SET_MY_THAT', my_that);
		}
	}
}

export default MyChat