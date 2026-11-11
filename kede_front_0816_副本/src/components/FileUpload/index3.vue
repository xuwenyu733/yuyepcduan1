<template>
  <div class="upload-file">
    <el-upload multiple :action="uploadFileUrl" :before-upload="handleBeforeUpload" :file-list="fileList" :limit="limit"
      :data="data" :accept="accept" :on-error="handleUploadError" :on-exceed="handleExceed" :on-success="handleUploadSuccess"
      :show-file-list="false" :headers="headers" class="upload-file-uploader" ref="fileUpload">
      <!-- 上传按钮 -->
      <el-button size="mini" type="primary" id="chooseFileBtn">选取文件</el-button>
      <!-- 上传提示 -->
      <div class="el-upload__tip" slot="tip" v-if="showTip">
        请上传
        <template v-if="fileSize"> 大小不超过 <b style="color: #f56c6c">{{ fileSize }}MB</b> </template>
        <template v-if="fileType"> 格式为 <b style="color: #f56c6c">{{ fileType.join("/") }}</b> </template>
        的文件
      </div>
    </el-upload>

    <!-- 文件列表 -->
    <transition-group class="upload-file-list el-upload-list el-upload-list--text" name="el-fade-in-linear" tag="ul">
      <li :key="file.url" class="el-upload-list__item ele-upload-list__item-content" v-for="(file, index) in fileList">
        <el-link :href="`${file.url}`" :underline="false" target="_blank">
          <span class="el-icon-document"> {{ getFileName(file.name) }} </span>
        </el-link>
        <div class="ele-upload-list__item-content-action">
          <el-link :href="`${file.url}`" :underline="false" target="_blank">
            <span class="el-icon-files" style="width: 150px;"> {{ getFileSize(file) }}kb </span>
          </el-link>
          <el-link :underline="false" @click="customDownload(file)" type="success">下载</el-link>
          <el-link :underline="false" @click="handleDelete(index)" type="danger">删除</el-link>
        </div>
      </li>
    </transition-group>
  </div>
</template>

<script>
import { getAccessToken } from "@/utils/auth";
import { getFileList, deleteFile, downloadFile, download } from '@/api/infra/file';

export default {
  name: "FileUpload",
  props: {
    // 值
    fid: [String, Object, Array],
    // value: [String, Object, Array],
    // 数量限制
    busType: {
      type: String,
      default: "",
    },
    // 数量限制
    limit: {
      type: Number,
      default: 5,
    },
    // 大小限制(MB)
    fileSize: {
      type: Number,
      default: 5,
    },
    // 文件类型, 例如['png', 'jpg', 'jpeg']
    fileType: {
      type: Array,
      default: () => ["doc", "xls", "ppt", "txt", "pdf", 'png', 'jpg', 'jpeg'],
    },
    // 是否显示提示
    isShowTip: {
      type: Boolean,
      default: true
    },
    // 文件选择器 accept，例如 video/* 或 .mp4,.mov
    accept: {
      type: String,
      default: ""
    }
  },
  data() {
    return {
      value: undefined,
      number: 0,
      uploadList: [],
      baseUrl: process.env.VUE_APP_BASE_API,
      uploadFileUrl: process.env.VUE_APP_BASE_API + "/admin-api/infra/file/upload", // 请求地址
      getFileUrl: process.env.VUE_APP_BASE_API + "/admin-api/infra/file", // 请求地址
      headers: { Authorization: "Bearer " + getAccessToken() }, // 设置上传的请求头部
      fileList: [],
      queryParams: {},
      data: {
        fid: '',
        busType: '',
      }
    };
  },
  created() {
    // this.value = this.fid;
    this.data = {
      fid: this.fid,
      busType: this.busType
    }
    this.selectList();
  },
  mounted() {
    // console.log(`=============>busType:${this.busType}`);
  },
  watch: {
    value: {
      handler(val) {
        if (val) {
          let temp = 1;
          // 首先将值转为数组
          const list = Array.isArray(val) ? val : this.value.split(',');
          // 然后将数组转为对象数组
          this.fileList = list.map(item => {
            if (typeof item === "string") {
              item = { name: item, url: item };
            }
            item.uid = item.uid || new Date().getTime() + temp++;
            return item;
          });
        } else {
          this.fileList = [];
          return [];
        }
      },
      deep: true,
      immediate: true
    }
  },
  computed: {
    // 是否显示提示
    showTip() {
      return this.isShowTip && (this.fileType || this.fileSize);
    },
  },
  methods: {
    startUpload() {
      // console.log(`startUpload===========>`, this.jq("#chooseFileBtn"));
      // this.$refs.fileUpload.click();
      this.jq("#chooseFileBtn").click();
    },
    getFileSize(file) {
      let size = this.my_common.computer(file.size, 1024, 3, 2);
      return size;
    },
    customDownload(file) {
      console.log(`==============>file:${JSON.stringify(file)}`);
      downloadFile(`${this.getFileUrl}/${file.configId}/get/${file.path}`).then(res => {
        this.$download.download0(res, file.name);
        console.log(`=========>`, res)
      });
    },
    // 上传前校检格式和大小
    handleBeforeUpload(file) {
      // 校检文件类型
      if (this.fileType) {
        let fileExtension = "";
        if (file.name.lastIndexOf(".") > -1) {
          fileExtension = file.name.slice(file.name.lastIndexOf(".") + 1);
        }
        const isTypeOk = this.fileType.some((type) => {
          if (file.type.toLowerCase().indexOf(type) > -1) return true;
          if (fileExtension && fileExtension.toLowerCase().indexOf(type) > -1) return true;
          return false;
        });
        if (!isTypeOk) {
          this.$modal.msgError(`文件格式不正确, 请上传${this.fileType.join("/")}格式文件!`);
          return false;
        }
      }
      // 校检文件大小
      if (this.fileSize) {
        const isLt = file.size / 1024 / 1024 < this.fileSize;
        if (!isLt) {
          this.$modal.msgError(`上传文件大小不能超过 ${this.fileSize} MB!`);
          return false;
        }
      }
      this.$modal.loading("正在上传文件，请稍候...");
      this.number++;
      return true;
    },
    // 文件个数超出
    handleExceed() {
      this.$modal.msgError(`上传文件数量不能超过 ${this.limit} 个!`);
    },
    // 上传失败
    handleUploadError(err) {
      this.$modal.msgError("上传图片失败，请重试");
      this.$modal.closeLoading()
    },
    // 上传成功回调
    handleUploadSuccess(res, file) {
      if (res.code === 0) {
        // edit by 芋道源码
        let param = { name: file.name, url: res.data };
        this.uploadList.push(param);
        // console.log(`==========>param:`, file);
        this.$emit("uploadSuccess", param);
        this.uploadedSuccessfully();
      } else {
        this.number--;
        this.$modal.closeLoading();
        this.$modal.msgError(res.msg);
        this.$refs.fileUpload.handleRemove(file);
        this.uploadedSuccessfully();
      }
    },
    // 删除文件
    handleDelete(index) {
      let that = this;
      let curr = this.fileList[index];
      this.$modal.confirm(`是否确认删除该文件(${curr.name})?`).then(function () {
        let file = that.fileList.splice(index, 1)[0];
        that.$emit("input", that.listToString(that.fileList));
        // console.log(`filefile================>${JSON.stringify(file)}`);
        if (file.id) deleteFile(file.id);
      }).then(() => {
      }).catch(() => { });
    },
    // 上传结束处理
    uploadedSuccessfully() {
      if (this.number > 0 && this.uploadList.length === this.number) {
        this.fileList = this.fileList.concat(this.uploadList);
        this.uploadList = [];
        this.number = 0;
        this.$emit("input", this.listToString(this.fileList));
        this.$modal.closeLoading();
      }
    },
    // 获取文件名称
    getFileName(name) {
      if (name.lastIndexOf("/") > -1) {
        return name.slice(name.lastIndexOf("/") + 1);
      } else {
        return name;
      }
    },
    // 对象转成指定字符串分隔
    listToString(list, separator) {
      let strs = "";
      separator = separator || ",";
      for (let i in list) {
        strs += list[i].url + separator;
      }
      return strs != '' ? strs.substr(0, strs.length - 1) : '';
    },

    selectList() {
      // console.log("========>" + this.fid)
      if (this.fid) {
        if (Array.isArray(this.fid)) {
          this.fid = this.fid.join(',');
        }
        this.queryParams = { 'fid': this.fid, 'busType': this.busType };
        getFileList(this.queryParams).then(response => {
          console.log("getFileList ========>", response)
          this.fileList = [];
          this.fileList = response.data;
          if (this.my_common.listIsEmpty(this.fileList))
            return;
          this.fileList.forEach(item => {
            // console.log(`======>item:${JSON.stringify(item)}`);
            this.$emit('sendCurrFiles', { "fid": item.fid, "fileUrl": item.url });
          })
          //console.log(response);
        });
      } else {
        this.fileList = [];
      }
    },
  }
};
</script>

<style scoped lang="scss">
.upload-file-uploader {
  margin-bottom: 5px;
}

.upload-file-list .el-upload-list__item {
  border: 1px solid #e4e7ed;
  line-height: 2;
  margin-bottom: 10px;
  position: relative;
}

.upload-file-list .ele-upload-list__item-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: inherit;
}

.ele-upload-list__item-content-action .el-link {
  margin-right: 10px;
}
</style>
