<template>
	<div class="chat-msg-container">
		<div class="msg-content" v-if="fileType != undefined">
			<!-- 文本 -->
			<div class="chatMsg markdown-body" v-if="fileType == 0" v-html="renderedMsg">
			</div>
			<ChatBiPanel
				v-if="fileType == 0 && data && data.viz"
				:viz="data.viz"
				:conversation-id="data.conversation_id || data.conversationId || ''"
			/>
			<!-- 图片 -->
			<div class="chatMsgImg" v-if="fileType == 1">
				<!-- <img :src="fileUrl" class="user-head" mode="widthFix">
				</img> -->
				<image-preview v-if="fileUrl != null && fileUrl != ''" :src="fileUrl"
            		width="349px" height="auto"></image-preview>
			</div>
			<!-- pdf -->
			<div class="chatMsgPdf" v-if="fileType == 4" 
				@click.stop="openPdf('show', `${predivFile}${fileUrl}`)">
				<div class="fileName">
					{{fileName}}
				</div>
				<div class="fileIcon">
					<i class="fa fa-file-pdf-o fa-4x"></i>
				</div>
			</div>
			<!-- 视频 -->
			<div class="chatMsgVideo" v-if="fileType == 2">
				<yingbing-video :poster="fileName" :muted="true" class="myVideo" :src="fileUrl">
				</yingbing-video>
			</div>
			<!-- 文档及其他 -->
			<div class="chatMsgOther" v-if="fileType == 3 || fileType == 99"
				@click.stop="openPdf('download', `${fileUrl}`)">
				<div class="fileName">
					{{fileName}}
				</div>
				<div class="fileIcon">
					<i class="fa fa-file-o fa-4x"></i>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	// import config from '@/config';
	import ImagePreview from "@/components/ImagePreview";
	import ChatBiPanel from "@/components/MyChat/ChatBiPanel.vue";
	import {
		CURR_DEVICE_TYPE,
		CURR_DEVICE_TYPE_MOBILE,
		CURR_DEVICE_TYPE_H5,
	} from "@/utils/constants";
	
	export default {
		name: "ChatMsg",
		components: {
			ImagePreview,
			ChatBiPanel,
		},
		props: {
			data: { //展示图片最大值
				type: Object,
				default: {},
			},
			currUserId: { //展示图片最大值
				type: String,
				default: undefined,
			},
			currPage: { // 
				type: String,
				default: undefined,
			},
		},
		data() {
			return {
				predivFile: process.env.VUE_APP_PREVIEW_FILE,
				fileType: undefined, // 文件类型 0:文本;1:图片;2:视频;3:文档;4:pdf;99:其他;
				fileUrl: undefined,
				fileName: undefined,
			};
		},
		computed: {
			renderedMsg() {
				const raw = (this.data && this.data.msg) ? String(this.data.msg) : "";
				return this.renderLiteMarkdown(raw);
			},
		},
		created() {
			// console.log(`ChatMsg.vue============>data: ${this.predivFile}`);
			if (!this.data) return;
			if (this.data.fid) {
				this.fileType = this.my_common.judgeFileType(this.data.msg);
				this.fileName = this.data.msg.split(",")[0];
				this.fileUrl = this.data.msg.split(",")[1];

				// console.log(`============>data: fid`);
			} else {
				this.fileType = 0;
				// console.log(`============>data: else`);
			}
		},
		methods: {
			_escapeHtml(s) {
				return String(s)
					.replace(/&/g, "&amp;")
					.replace(/</g, "&lt;")
					.replace(/>/g, "&gt;")
					.replace(/\"/g, "&quot;")
					.replace(/'/g, "&#39;");
			},
			_renderInline(md) {
				// inline code: `code`
				let s = md.replace(/`([^`]+?)`/g, (m, g1) => `<code>${this._escapeHtml(g1)}</code>`);
				// bold: **text**
				s = s.replace(/\*\*([^*]+?)\*\*/g, "<strong>$1</strong>");
				// italic: *text*（尽量避免与列表冲突）
				s = s.replace(/(^|[^\*])\*([^\s][^*]*?)\*(?!\*)/g, "$1<em>$2</em>");
				return s;
			},
			renderLiteMarkdown(text) {
				// 轻量 Markdown 渲染：覆盖常见输出（加粗/列表/换行/代码块/行内代码）
				let src = String(text || "");
				if (!src) return "";
				// 后端或其它入口可能把换行转成 <br>；全文 escape 后 <br> 会显示成字符，先还原为换行
				src = src.replace(/<br\s*\/?>/gi, "\n");

				// 代码块 ``` ... ```
				const blocks = [];
				let escaped = src.replace(/```([\s\S]*?)```/g, (m, code) => {
					const key = `__CODE_BLOCK_${blocks.length}__`;
					const cleaned = String(code).replace(/^\w+\n/, "");
					blocks.push(`<pre><code>${this._escapeHtml(cleaned)}</code></pre>`);
					return key;
				});

				// 先转义剩余内容
				escaped = this._escapeHtml(escaped);

				const lines = escaped.split(/\r?\n/);
				let html = "";
				let inUl = false;
				const closeUl = () => {
					if (inUl) {
						html += "</ul>";
						inUl = false;
					}
				};
				for (let i = 0; i < lines.length; i++) {
					const line = lines[i];
					const m = line.match(/^\s*[-*]\s+(.*)$/);
					if (m) {
						if (!inUl) {
							html += "<ul>";
							inUl = true;
						}
						html += `<li>${this._renderInline(m[1])}</li>`;
						continue;
					}
					closeUl();
					if (line.trim() === "") {
						html += "<br/>";
						continue;
					}
					html += `<div class="md-line">${this._renderInline(line)}</div>`;
				}
				closeUl();

				// 还原代码块占位符
				for (let i = 0; i < blocks.length; i++) {
					const key = `__CODE_BLOCK_${i}__`;
					html = html.split(key).join(blocks[i]);
				}
				return html;
			},
			/**
			 * @param {Object} type show or download
			 * @param {Object} pdfUrl pdf的文件地址
			 */
			openPdf(type, pdfUrl) {
				if (type == 'download') {
					this.my_common.download({fileUrl:this.fileUrl, fileName: this.fileName});
					return;
				}
				window.open(pdfUrl, '_blank');
			},
		}
	}
</script>

<style lang="scss" scoped>
	@import url("../../assets/font/css/font-awesome.min.css");
	.chat-msg-container {
		.msg-content {
			.chatMsg {
				// border: 1px solid red;
				.md-line {
					margin: 2px 0;
					line-height: 1.6;
					white-space: normal;
					word-break: break-word;
				}
				ul {
					margin: 6px 0;
					padding-left: 18px;
				}
				li {
					margin: 3px 0;
					line-height: 1.6;
				}
				code {
					padding: 1px 6px;
					border-radius: 6px;
					background: rgba(255, 255, 255, 0.12);
					font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
				}
				pre {
					margin: 8px 0;
					padding: 10px 12px;
					border-radius: 10px;
					overflow: auto;
					background: rgba(0, 0, 0, 0.25);
				}
				pre code {
					padding: 0;
					background: transparent;
				}
			}

			.chatMsgImg {
				max-width: 80%;
			}
			.chatMsgVideo {
				display: flex;
				justify-content: center;
				align-items: center;
			}
			.chatMsgPdf {
				padding: 15px 7px;
				display: flex;
				justify-content: center;
				align-items: center;
				.fileIcon {
					margin-left: 7px;
				}
			}
			.chatMsgOther {
				padding: 15px 7px;
				display: flex;
				justify-content: center;
				align-items: center;
				.fileIcon {
					margin-left: 7px;
				}
			}
		}
	}
</style>