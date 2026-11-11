import {
	UserInfoKey,
	ChatMsgKey,
	PageToKey,
	ASK_CHAT_LOG_LIST,
	CHAT_MSG_FOR_USER,
	CHAT_MSG_FOR_GROUP,
	CHAT_MSG_FOR_DELETE,
	UserChatLogKey,
	GroupChatLogKey,
	ChatLogKey
} from "@/utils/constants";
import my_common from '@/utils/common.js';
import {
	getUUID,
	formatDate
} from "@/utils";

// 将接受的消息进行json序列化
function parseMsg(msg) {
	if(!msg) return null;
	try {
		let obj = JSON.parse(msg);
		if(obj && obj.operateKey && obj.msg) {
			return obj;
		}
		return null;
	} catch(error) {
		return null;
	}
}


/**
 * 不是自己给自己发送消息，需要播放声音
 * @param {Object} data 消息对象
 * @param {Object} that socketVuex的store对象 
 */
function playAudio(data, that) {
	try {
		if(!data.isSelf) that.commit('playMyAudio');
	} catch(error) {
		console.log(`state.my_audio.play======>`, error);
	}
}



// ==================================================================================================群聊处理 start
/**
 * 群聊 信息 已经处理过的 记录
 * @param {Object} data 收到的消息 zk_chat_msg_receive.id
 */
const groupMsgIdHandledKey = `${GroupChatLogKey}_hadHandledId`;
function groupMsgIdHandledAdd(data, that) {
	try {
		const receiveId = data.chatMsgReceiveId;
		// 1. 消息已经处理则加入缓存
		let hadHandledIdStoreStr = localStorage.getItem(groupMsgIdHandledKey);
		if(!hadHandledIdStoreStr) hadHandledIdStoreStr = "[]";
		let hadHandledIdStore = JSON.parse(hadHandledIdStoreStr);
		if(!hadHandledIdStore.includes(receiveId)) hadHandledIdStore.push(receiveId);
		// 2. 消息已经处理需要 通知后台
		let version = new Date().getTime();
		let params = {
			"chatMsgReceiveId": receiveId,
		    "currUserId": data.currUserId,
		    "oppositeUserId": data.oppositeUserId,
		    "chatTime": formatDate(version),
		    "msg": `消息已处理反馈后台chatMsgReceiveId:${receiveId}`,
		    "operateKey": CHAT_MSG_FOR_DELETE,
		    "version": version,
		}
		that.commit('sendMsg', JSON.stringify(params), 'groupMsgIdHandledAdd');
		localStorage.setItem(groupMsgIdHandledKey, JSON.stringify(hadHandledIdStore));
	} catch(error) {
	    console.log(`groupMsgIdHandledAdd==========>`, error);
	}
}

/**
 * 群聊 信息 处理 判断 
 * @param {Object} receiveId zk_chat_msg_receive.id
 */
function groupMsgIdIsHandled(receiveId) {
	try {
		let hadHandledIdStoreStr = localStorage.getItem(groupMsgIdHandledKey);
		if(!hadHandledIdStoreStr) return false;
		let hadHandledIdStore = JSON.parse(hadHandledIdStoreStr);
		return hadHandledIdStore.includes(receiveId)
	} catch(error) {
	    console.log(`groupMsgIdIsHandled==========>`, error);
	}
	return false;
}

// 群聊 信息 处理
function groupHandle(data, params) {
	let that = params.that;
	let state = params.state;
	try {
		if(data.operateKey == CHAT_MSG_FOR_GROUP) {
			// console.log(`playNotice========>`, state.my_audio); // playMyAudio
			if(groupMsgIdIsHandled(data.chatMsgReceiveId)) {
				console.log(`groupHandle==========>当前消息已经处理，无需重复处理`, data);
				return;
			}
			playAudio(data, that);
			// 2. 用户A给群聊发送的消息
			const cacheKey = `${GroupChatLogKey}_${data.oppositeUserId}`;
			let chatGroupStoreStr = localStorage.getItem(cacheKey);
			if(!chatGroupStoreStr) { // 第一次接受群聊消息，缓存为空，则需要添加当前消息 oppositeUserId表自己
				let temp = {
						groupItemList: [{
							"oppositeUserId": data.oppositeUserId, "groupId": data.groupId, "id": data.groupId,
							"msgItemList": [data],
						}]
					};
				localStorage.setItem(cacheKey, JSON.stringify(temp));
				state.chatGroupStore = temp;
				state.groupMsgItem = JSON.stringify(data);
				groupMsgIdHandledAdd(data, that);
				// console.log(`groupHandle==========>state.chatGroupStore `, state.chatGroupStore);
				return;
			}
			// console.log(`msgHandle===========>chatGroupStoreStr:${chatGroupStoreStr}`);
			let param = JSON.parse(chatGroupStoreStr);
			// 3. 针对群聊聊天 进行处理 chatGroupStoreStr.groupItemList
			if(!(param && param.groupItemList && param.groupItemList.length > 0)) param.groupItemList = [];
			let curr = my_common.listFindOne(param.groupItemList, "groupId", data.groupId);
			if(curr) {
				if(!curr.msgItemList) curr.msgItemList = [];
				curr.msgItemList = [...curr.msgItemList, data];
			} else {
				curr = {
					"oppositeUserId": data.oppositeUserId, "groupId": data.groupId, "id": data.groupId,
					"msgItemList": [data],
				};
				param.groupItemList.push(curr);
			}
			// 重复消息去重
			param.groupItemList.forEach(l => l.msgItemList = my_common.listToSet(l.msgItemList, "chatMsgReceiveId"));
			param.groupItemList.filter(obj => !my_common.listIsEmpty(obj.msgItemList))
						.forEach(obj => obj.unReadCount = obj.msgItemList.filter(m => !m.hadRead).length);
			localStorage.setItem(cacheKey, JSON.stringify(param));
			state.chatGroupStore = param;
			state.groupMsgItem = JSON.stringify(data);
			groupMsgIdHandledAdd(data, that);
			// console.log(`groupHandle==========>state.chatGroupStore `, state.chatGroupStore);
		}
	} catch(error) {
		console.log(`groupHandle==========>`, error);
	}
}
// ==================================================================================================群聊处理 end


// ==================================================================================================一对一聊天处理 start
/**
 * 一对一聊天 信息 已经处理过的 记录
 * @param {Object} data 收到的消息 zk_chat_msg_receive.id
 */
const userMsgIdHandledKey = `${UserChatLogKey}_hadHandledId`;
function userMsgIdHandledAdd(data, that) {
	try {
		const receiveId = data.chatMsgReceiveId;
		// 1. 消息已经处理则加入缓存
		let hadHandledIdStoreStr = localStorage.getItem(userMsgIdHandledKey);
		if(!hadHandledIdStoreStr) hadHandledIdStoreStr = "[]";
		let hadHandledIdStore = JSON.parse(hadHandledIdStoreStr);
		if(!hadHandledIdStore.includes(receiveId)) hadHandledIdStore.push(receiveId);
		// 2. 消息已经处理需要 通知后台
		let version = new Date().getTime();
		let params = {
			"chatMsgReceiveId": receiveId,
		    "currUserId": data.currUserId,
		    "oppositeUserId": data.oppositeUserId,
		    "chatTime": formatDate(version),
		    "msg": `消息已处理反馈后台chatMsgReceiveId:${receiveId}`,
		    "operateKey": CHAT_MSG_FOR_DELETE,
		    "version": version,
		}
		that.commit('sendMsg', JSON.stringify(params), 'userMsgIdHandledAdd');
		localStorage.setItem(userMsgIdHandledKey, JSON.stringify(hadHandledIdStore));
	} catch(error) {
	    console.log(`userMsgIdHandledAdd==========>`, error);
	}
}

/**
 * 一对一聊天 信息 处理 判断 
 * @param {Object} receiveId zk_chat_msg_receive.id
 */
function userMsgIdIsHandled(receiveId) {
	try {
		let hadHandledIdStoreStr = localStorage.getItem(userMsgIdHandledKey);
		if(!hadHandledIdStoreStr) return false;
		let hadHandledIdStore = JSON.parse(hadHandledIdStoreStr);
		return hadHandledIdStore.includes(receiveId)
	} catch(error) {
	    console.log(`userMsgIdIsHandled==========>`, error);
	}
	return false;
}

// 一对一聊天 信息 处理
function userHandle(data, params) {
	// debugger
	let that = params.that;
	let state = params.state;
	try {
		if(data.operateKey == CHAT_MSG_FOR_USER) {
			// console.log(`playNotice========>`, state.my_audio); // playMyAudio
			if(userMsgIdIsHandled(data.chatMsgReceiveId)) {
				userMsgIdHandledAdd(data, that);
				console.log(`userHandle==========>当前消息已经处理，无需重复处理`, data);
				return;
			}
			playAudio(data, that);
			// 2. 用户A给一对一聊天发送的消息
			const cacheKey = `${UserChatLogKey}_${data.oppositeUserId}`;
			let chatUserStoreStr = localStorage.getItem(cacheKey);
			if(!chatUserStoreStr) { // 第一次接受一对一聊天消息，缓存为空，则需要添加当前消息 currUserId表对面，oppositeUserId表自己
				let temp = {
						userItemList: [{
							"oppositeUserId": data.oppositeUserId, "currUserId": data.currUserId,
							"msgItemList": [data],
						}]
					};
				localStorage.setItem(cacheKey, JSON.stringify(temp));
				state.chatUserStore = temp;
				state.userMsgItem = JSON.stringify(data);
				userMsgIdHandledAdd(data, that);
				console.log(`userHandle 1==========>state.chatUserStore `, state.chatUserStore);
				return;
			}
			// console.log(`msgHandle===========>chatUserStoreStr:${chatUserStoreStr}`);
			let param = JSON.parse(chatUserStoreStr);
			// 3. 针对一对一聊天聊天 进行处理 chatUserStoreStr.userItemList
			if(!(param && param.userItemList && param.userItemList.length > 0)) param.userItemList = [];
			let curr = my_common.listFindOne(param.userItemList, "currUserId", data.currUserId);
			if(curr) {
				if(!curr.msgItemList) curr.msgItemList = [];
				curr.msgItemList = [...curr.msgItemList, data];
			} else {
				curr = {
					"oppositeUserId": data.oppositeUserId, "currUserId": data.currUserId,
					"msgItemList": [data],
				};
				param.userItemList.push(curr);
			}
			// 重复消息去重
			param.userItemList.forEach(l => l.msgItemList = my_common.listToSet(l.msgItemList, "chatMsgReceiveId"));
			param.userItemList.filter(obj => !my_common.listIsEmpty(obj.msgItemList))
						.forEach(obj => obj.unReadCount = obj.msgItemList.filter(m => !m.hadRead).length);
			localStorage.setItem(cacheKey, JSON.stringify(param));
			state.chatUserStore = param;
			state.userMsgItem = JSON.stringify(data);
			userMsgIdHandledAdd(data, that);
			console.log(`userHandle 2==========>state.chatUserStore `, state.chatUserStore);
		}
	} catch(error) {
		console.log(`userHandle==========>`, error);
	}
}
// ==================================================================================================一对一聊天处理 end

// 消息处理方法 
function msgHandle(msg, params) {
	let that = params.that;
	let state = params.state;
	// console.log(`chat msgHandle===========>msg:${msg}`);
	// debugger
    // 一. 消息常规处理
	let data = parseMsg(msg);
	if(!data) return;
	// 一. 1. 群聊
	groupHandle(data, params);
    // 二. 根据消息 operateKey 判断如何处理
	if(data.operateKey == ASK_CHAT_LOG_LIST) {
		// 1. 获取最新在线人员及消息 ASK_CHAT_LOG_LIST
		let chatUserStore = JSON.parse(data.msg);
		// console.log(`聊天人数:${chatUserStore.userItemList.length},具体消息:${data.msg}`);
		if(chatUserStore && chatUserStore.userItemList) {
			// 2. 设置当前聊天列表到state，触发address_book.vue进行处理
			state.chatUserStore = chatUserStore;
		}
	} else if(data.operateKey == CHAT_MSG_FOR_USER) {
		userHandle(data, params);
	}
}

export default {
    msgHandle,   //   消息处理方法
}
