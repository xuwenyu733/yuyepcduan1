<template>
    <div class="aliyun-voice2txt">
        <div v-if="false">
            <button @click="prepareRecording" :disabled="disableButton.start">Start Recording</button>
            <button @click="stopRecording" :disabled="disableButton.stop">Stop Recording</button>
            <br />
            <div class="section">
            <div>状态：</div>
            <div>{{ recordingStatus }}</div>
            </div>
            <div class="section">
            <div>最终录音结果：</div>
            <div>{{ resultText }}</div>
            </div>
            <div class="section">
            <div>录音过程： </div>
            <div class="recording">
                <p v-for="(recordText, index) in recordings" :key="index">{{ recordText }}</p>
            </div>
            </div>
        </div>
    </div>
</template>

<script>
import {
    setToken,
    removeToken,
    getUserInfo
} from '@/utils/auth';
export default {
    data() {
        return {
            accessToken: {
                token: null,
                expireTime: null
            },
            disableButton: {
                start: false,
                stop: true,
            },
            recordingStatus: null,
            resultText: "",
            recordings: [],
            token: "",
            abc: "",

            audioContext: null,
            audioProcessor: null,

            pcmBuffer: new Int16Array(),
            mediaRecorder: null,
            websocket: null,
        }
    },
    created() {
        let that = this;
        // console.log(`============>1`, my_jquery);
        let param = { preUUID: 'defaa81f4e1d418787e2877c3d47ce4b' };
        that.jq.ajax({
            url: 'https://t5.kdaqua.com/uuid/aliyun/genUUID', // 示例API
            type: 'POST',
            contentType: 'application/json', // 设置内容类型为JSON
            data: JSON.stringify(param), // 将对象转换为JSON字符串
            dataType: 'json', // 预期服务器返回的数据类型
            success: function (res) {
                that.accessToken.token = res.data;
                // console.log('请求成功!\n响应数据: ' + JSON.stringify(that.accessToken.token));
            },
            error: function (xhr, status, error) {
                console.log('请求失败!\n状态: ' + status + '\n错误: ' + error);
            }
        });
    },
    methods: {

        generateUniqueID() {
            return Array(32)
                .fill(0)
                .map(() => Math.floor(Math.random() * 16).toString(16))
                .join('');
        },

        onTextChanged(text) {
            const recordings = this.recordings || []
            const count = recordings.length;
            if (count > 0) {
                recordings[count - 1] = text
            } else {
                recordings[0] = text
            }

            this.recordings = recordings;
            let msgObj = {
                type: `changed_msg`,
                value: `${text}`
            }
            this.$emit('son_send_msg', msgObj);
            console.log('aliyun voice2txt onTextChanged:', text);
        },

        onTextFinished(text) {
            const recordings = this.recordings || []
            const count = recordings.length;
            if (count > 0) {
                recordings[count - 1] = text
            } else {
                recordings[0] = text
            }

            this.recordings = recordings

            this.resultText += text;
            let msgObj = {
                type: `finished_msg`,
                value: `${text}`
            }
            this.$emit('son_send_msg', msgObj);
            console.log('aliyun voice2txt onTextFinished:', this.resultText);
        },

        // 实现16000音频采样率
        async resampleAudioBuffer(audioBuffer, targetSampleRate) {
            const numberOfChannels = audioBuffer.numberOfChannels;
            const offlineContext = new OfflineAudioContext(
                numberOfChannels,
                audioBuffer.duration * targetSampleRate,
                targetSampleRate
            );

            const bufferSource = offlineContext.createBufferSource();
            bufferSource.buffer = audioBuffer;
            bufferSource.connect(offlineContext.destination);
            bufferSource.start();

            const resampledAudioBuffer = await offlineContext.startRendering();
            return resampledAudioBuffer;
        },

        async startCaptureAudio() {
            this.recordingStatus = '可以开始录音了！'
            this.audioContext = new AudioContext();
            const sampleRate = this.audioContext.sampleRate;
            console.log("Original sampleRate: ", sampleRate);
            let msgObj = {
                type: `mic_start_ok`,
                value: ``
            }
            this.$emit('son_send_msg', msgObj);

            const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
            const source = this.audioContext.createMediaStreamSource(stream);

            this.audioProcessor = this.audioContext.createScriptProcessor(1024, 1, 1);
            this.audioProcessor.onaudioprocess = async (event) => {
                const inputBuffer = event.inputBuffer;

                // Resample the buffer if needed
                let resampledBuffer = inputBuffer;
                if (sampleRate !== 16000) {
                    resampledBuffer = await this.resampleAudioBuffer(inputBuffer, 16000);
                }

                const pcmData = this.convertToPCM(resampledBuffer);
                this.pcmBuffer = this.concatenateTypedArrays(this.pcmBuffer, new Int16Array(pcmData));

                while (this.pcmBuffer.length * 2 >= 3200) {
                    const chunk = this.pcmBuffer.slice(0, 3200 / 2);
                    if(this.websocket) this.websocket.send(chunk.buffer);
                    else {
                        this.prepareRecording();
                        break;
                    }
                    this.pcmBuffer = this.pcmBuffer.slice(3200 / 2);
                }
            };

            source.connect(this.audioProcessor);
            this.audioProcessor.connect(this.audioContext.destination);
        },
        judgeIsStart() {
            if(!(this.websocket && this.websocket.onopen)) {
                return false;
            } 
            return true;
        },

        async prepareRecording() {
            this.disableButton.start = true;
            this.disableButton.stop = false;
            this.recordingStatus = '正在连接阿里云...'
            const APPKEY = "SBsgEzUd9pt76j60";
            // const APPKEY = "3391ba49f1bd431696b0cccf9e7db73b";
            const TOKEN = this.accessToken.token;
            const URL = `wss://nls-gateway.cn-shanghai.aliyuncs.com/ws/v1?token=${TOKEN}`;
            // const URL = `ws:192.168.31.136:10095`;

            if(!(this.websocket && this.websocket.onopen)) {
                this.websocket = new WebSocket(URL); //  , ['binary']
                this.websocket.onopen = () => {
                    const instruction = {
                        "header": {
                            "appkey": APPKEY,
                            "message_id": this.generateUniqueID(),
                            "task_id": this.generateUniqueID(),
                            "namespace": "SpeechTranscriber",
                            "name": "StartTranscription"
                        },
                        "payload": {
                            "format": "PCM",
                            "sample_rate": 16000,
                            "enable_intermediate_result": true,
                            "enable_punctuation_prediction": true,
                            "enable_inverse_text_normalization": true,
                            "max_sentence_silence": 500,
                        }
                    }
                    this.websocket.send(JSON.stringify(instruction));
                };
            }

            this.websocket.onmessage = (e) => {
                const ret = JSON.parse(e.data);
                if (ret.header.name === 'TranscriptionResultChanged') {
                    this.onTextChanged(ret.payload.result);
                } else if (ret.header.name === 'SentenceBegin') {
                    const recordings = this.recordings || [];
                    recordings.push("")
                    this.recordings = recordings;
                } else if (ret.header.name === 'TranscriptionStarted') {
                    this.startCaptureAudio();
                } else if (ret.header.name === 'SentenceEnd') {
                    this.onTextFinished(ret.payload.result);
                } else if (ret.header.name === 'TranscriptionCompleted') {
                    console.log('服务端已停止了语音转写', ret);
                } else {
                    console.log("response: ", e)
                }
            };
        },

        convertToPCM(audioBuffer) {
            let float32Array = audioBuffer.getChannelData(0);
            let pcmData = new Int16Array(float32Array.length);

            for (let i = 0; i < float32Array.length; i++) {
                let s = Math.max(-1, Math.min(1, float32Array[i]));
                pcmData[i] = s < 0 ? s * 0x8000 : s * 0x7FFF;
            }

            return pcmData.buffer;
        },

        concatenateTypedArrays(a, b) {
            const result = new a.constructor(a.length + b.length);
            result.set(a);
            result.set(b, a.length);
            return result;
        },

        stopRecording() {
            this.recordingStatus = '已停止录音'
            this.disableButton.start = false;
            this.disableButton.stop = true;
            if (this.audioProcessor) {
                this.audioProcessor.disconnect();
                this.audioProcessor = null;
            }
            if (this.websocket) {
                this.websocket.close();
                this.websocket = null;
            }
        }
    }
}
</script>

<style scoped>
.section {
  margin-top: 32px;
  display: flex;
  width: 100%;
  justify-content: center;
}

.recording {
  overflow-y: scroll;
  border: 2px solid #eee;
  width: 800px;
  height: 300px;
  color: #666;
  text-align: center;
}
</style>