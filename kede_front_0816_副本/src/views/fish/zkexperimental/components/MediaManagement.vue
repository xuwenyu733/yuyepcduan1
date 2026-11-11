<template>
  <div class="media-management">
    <!-- 上传区域 -->
    <el-card>
      <div slot="header">
        <span>多媒体上传</span>
      </div>

      <el-form :model="uploadForm" label-width="100px" size="small">
        <!-- <el-form-item label="选择实验" required>
          <el-select
            v-model="uploadForm.experimentId"
            placeholder="请选择实验"
            filterable
            style="width: 100%"
            @change="handleExperimentChange"
          >
            <el-option
              v-for="exp in experiments"
              :key="exp.id"
              :label="`${exp.experimentNumber} - ${exp.experimentTitle}`"
              :value="exp.id"
            />
          </el-select>
        </el-form-item> -->
        <el-form-item label="选择实验" prop="experimentalId">
          <el-select style="width:100%;" v-model="uploadForm.experimentId" placeholder="请选择实验" filterable clearable  @change="update_ExperimentalId">
            <el-option v-for="(item, index) in experimentalList" :key="item.id+'__'+index" :label="item.experimentTitle"
              :value="item.id"></el-option>
          </el-select>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="6">
            <!-- <el-form-item label="文件类型">
              <el-select v-model="uploadForm.fileType" style="width: 100%">
                <el-option label="图片" :value="1" />
                <el-option label="视频" :value="2" />
              </el-select>
            </el-form-item> -->
            <el-form-item label="文件类型" prop="fileType">
              <el-select v-model="uploadForm.fileType" placeholder="请输入文件类型" filterable clearable size="small" style="width: 100%;">
                <el-option v-for="(dict, index) in this.getDictDatas(DICT_TYPE.ZK_EXPERIMENTAL_MEDIA_FILE_TYPE)" :key="dict.value+'_33_'+index"
                  :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <!-- <el-form-item label="体表类别">
              <el-select v-model="uploadForm.bodyType" style="width: 100%">
                <el-option label="体表" value="body" />
                <el-option label="鳃" value="gill" />
                <el-option label="内脏" value="organ" />
              </el-select>
            </el-form-item> -->
            <el-form-item label="体表类别" prop="bodyType">
              <el-select v-model="uploadForm.bodyType" placeholder="请选择体表类别" filterable clearable  @change="update_bodyType" size="small" style="width: 100%;">
                <el-option v-for="(dict, index) in bodyTypeList" :key="dict.value+'_33_'+index"
                  :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6" v-if="sonBodyTypeList && sonBodyTypeList.length>0">
            <!-- <el-form-item label="子类别" v-if="uploadForm.bodyType === 'body'">
              <el-select v-model="uploadForm.subCategory" style="width: 100%">
                <el-option label="体侧" value="side" />
                <el-option label="头嘴" value="head" />
                <el-option label="腹部" value="abdomen" />
                <el-option label="各鳍" value="fin" />
              </el-select>
            </el-form-item>
            <el-form-item label="子类别" v-else-if="uploadForm.bodyType === 'organ'">
              <el-select v-model="uploadForm.subCategory" style="width: 100%">
                <el-option label="肝脏" value="liver" />
                <el-option label="肠道" value="intestine" />
              </el-select>
            </el-form-item> -->
            <el-form-item label="子类别" prop="sonBodyType">
              <el-select v-model="uploadForm.sonBodyType" placeholder="请选择子类别" size="small" style="width: 100%;">
                <el-option v-for="(dict, index) in sonBodyTypeList" :key="dict.value+'_33_'+index"
                  :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <!-- <el-col :span="6">
            <el-form-item label="池号">
              <el-input v-model="uploadForm.pondNumber" placeholder="例如: A1" />
            </el-form-item>
          </el-col> -->
        </el-row>

        <!-- <el-form-item label="文件上传">
          <el-upload
            ref="upload"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :file-list="fileList"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :on-remove="handleRemove"
            :before-upload="handleBeforeUpload"
            :limit="10"
            multiple
            list-type="picture-card"
            :accept="uploadForm.fileType === 1 ? 'image/*' : 'video/*'"
          >
            <i class="el-icon-plus"></i>
            <div slot="tip" class="el-upload__tip">
              支持{{ uploadForm.fileType === 1 ? '图片' : '视频' }}格式，最多上传10个文件
            </div>
          </el-upload>
        </el-form-item> -->
        <el-form-item label="文件上传" prop="fid" v-if="uploadForm.fid">
          <FileBusUpload
            ref="uploadRef"
            :key="'upload-' + uploadForm.fileType"
            :fid="uploadForm.fid"
            bus-type="zk_experimental_media"
            :file-size="uploadFileSize"
            :file-type="uploadFileTypes"
            :accept="uploadAccept"
            @uploadSuccess="confirmUrl"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving" :disabled="!hasUploadedFiles()">
            保存
          </el-button>
          <el-button @click="handleReset()">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 媒体文件列表 -->
    <el-card style="margin-top: 20px">
      <div slot="header" class="card-header">
        <span>媒体文件库 ({{ mediaList.length }})</span>
        <!-- <el-select
          v-model="filterCategory"
          placeholder="筛选类别"
          size="small"
          style="width: 150px"
          clearable
        >
          <el-option label="全部类别" value="" />
          <el-option label="体表" value="body" />
          <el-option label="鳃" value="gill" />
          <el-option label="内脏" value="organ" />
        </el-select> -->
        <el-select v-model="filterCategory" placeholder="筛选类别" filterable clearable size="small" style="width: 150px">
          <el-option v-for="(dict, index) in bodyTypeList" :key="dict.value+'_313_'+index"
            :label="dict.label" :value="dict.value" />
        </el-select>
      </div>

      <div v-loading="loading" class="media-grid">
        <div
          v-for="media in mediaList"
          :key="media.id"
          class="media-item"
          @click="handlePreview(media)"
        >
          <div class="media-thumbnail">
            <img
              v-if="media.fileType === 1"
              :src="getFileUrl(media.fileUrls)"
              alt=""
              @error="handleImageError"
            />
            <video
              v-else
              :src="getFileUrl(media.fileUrls)"
              preload="metadata"
            />
            <div class="media-type-badge">
              <i :class="media.fileType === 1 ? 'el-icon-picture' : 'el-icon-video-camera'"></i>
            </div>
          </div>
          <div class="media-info">
            <div class="media-category">
              {{ getCategoryLabel(media.bodyType) }}
              <span v-if="media.subCategory"> - {{ getSubCategoryLabel(media.bodyType, media.subCategory) }}</span>
            </div>
            <div class="media-meta">
              <span>{{ media.experimentNumber }}</span>
              <span v-if="media.pondNumber"> | {{ media.pondNumber }}</span>
            </div>
            <div class="media-time">{{ parseTime(media.createTime) }}</div>
          </div>
        </div>

        <el-empty
          v-if="!loading && filteredMediaList.length === 0"
          description="暂无媒体文件"
          :image-size="100"
        />
      </div>
    </el-card>

    <!-- 预览对话框 -->
    <el-dialog
      :title="previewMedia ? '媒体预览' : ''"
      :visible.sync="previewVisible"
      width="800px"
      append-to-body
    >
      <div v-if="previewMedia" class="preview-content">
        <img
          v-if="previewMedia.fileType === 1"
          :src="getFileUrl(previewMedia.fileUrls)"
          style="max-width: 100%; height: auto"
        />
        <video
          v-else
          :src="getFileUrl(previewMedia.fileUrls)"
          controls
          style="max-width: 100%; height: auto"
        />
        <div class="preview-info" style="margin-top: 20px">
          <p><strong>类别:</strong> {{ getCategoryLabel(previewMedia.bodyType) }}</p>
          <p v-if="previewMedia.subCategory">
            <strong>子类别:</strong> {{ getSubCategoryLabel(previewMedia.bodyType, previewMedia.subCategory) }}
          </p>
          <p><strong>池号:</strong> {{ previewMedia.pondNumber || '无' }}</p>
          <p><strong>时间:</strong> {{ parseTime(previewMedia.createTime) }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import * as ZkExperimentalMediaApi from '@/api/fish/zkexperimentalmedia'
import { getAccessToken } from '@/utils/auth'
import * as ZkExperimentalApi from '@/api/fish/zkexperimental';
import { DICT_TYPE, getDictDataLabel } from "@/utils/dict";
import FileBusUpload from "@/components/FileUpload/index3.vue";
import { getUUID } from "@/utils";

export default {
  name: 'MediaManagement',
  components: {
    FileBusUpload,
  },
  props: {
    experiments: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      loading: false,
      saving: false,
      mediaList: [],
      filterCategory: '',
      uploadForm: {
        pondNumber: '',
        
        id: undefined,
        experimentId: undefined,
        experimentNumber: undefined,
        fileType: "1",
        bodyType: '',
        sonBodyType: undefined,
        fid: undefined,
        fileNames: undefined,
        fileUrls: undefined,
        deptId: undefined,
      },
      fileList: [],
      previewVisible: false,
      previewMedia: null,
      uploadUrl: process.env.VUE_APP_BASE_API + '/admin-api/infra/file/upload',
      uploadHeaders: {
        Authorization: 'Bearer ' + getAccessToken()
      }, 
      experimentalList: [], 
      bodyTypeList: [], 
      sonBodyTypeList: [], 
      fileUrls: [],
      fileNames: [],
      filteredMediaList: [],
    }
  },
  computed: {
    isVideoUpload() {
      const v = this.uploadForm.fileType
      if (v === 2 || v === '2') return true
      const dicts = this.getDictDatas(DICT_TYPE.ZK_EXPERIMENTAL_MEDIA_FILE_TYPE) || []
      const hit = dicts.find(d => String(d.value) === String(v))
      return !!(hit && String(hit.label).includes('视频'))
    },
    uploadFileSize() {
      // 视频默认放宽到 500MB；图片保持 20MB
      return this.isVideoUpload ? 500 : 20
    },
    uploadFileTypes() {
      if (this.isVideoUpload) {
        return ['mp4', 'mov', 'avi', 'mkv', 'wmv', 'flv', 'webm', 'm4v', 'mpeg', 'mpg', '3gp', 'ts']
      }
      return ['png', 'jpg', 'jpeg', 'gif', 'bmp', 'webp']
    },
    uploadAccept() {
      if (this.isVideoUpload) {
        return 'video/*,.mp4,.mov,.avi,.mkv,.wmv,.flv,.webm,.m4v,.mpeg,.mpg,.3gp,.ts'
      }
      return 'image/*,.png,.jpg,.jpeg,.gif,.bmp,.webp'
    }
  },
  watch: {
    filterCategory: {
      handler(newV, oldV) {
        this.loadMedia()
      }
    }
  },
  created() {
    this.bodyTypeList = this.getDictDatas(DICT_TYPE.ZK_EXPERIMENTAL_MEDIA_JSON);
    // this.loadMedia()
    this.get_ExperimentalList();
  },
  methods: {
    confirmUrl(obj='') {
      if (!obj || typeof obj !== 'object') return
      const name = obj.name
      const url = obj.url
      if (!url || !String(url).trim()) {
        this.$modal.msgWarning('文件上传未成功，请重新上传')
        return
      }
      this.fileUrls.push(String(url).trim())
      this.fileNames.push(name || String(url).trim())
      this.uploadForm.fileUrls = this.fileUrls.join(',')
      this.uploadForm.fileNames = this.fileNames.join(',')
    },
    update_bodyType(parentValue) {
      let curr = null;
      this.bodyTypeList.forEach(obj => {
        if(obj.value == parentValue) {
          curr = obj;
          return;
        }
      })
      if(!curr || !curr.remark) {
        this.sonBodyTypeList = [];
        this.uploadForm.sonBodyType = "";
        return;
      }
      this.sonBodyTypeList = JSON.parse(curr.remark);
      // console.log(`sonBodyTypeList================>${JSON.stringify(this.sonBodyTypeList)}`);
    },
    update_ExperimentalId(currId){
      let item = null;
      this.experimentalList.forEach(obj => {
        if(obj.id == currId) {
          item = obj;
          return;
        }
      })
      // 2. 执行 赋值
      this.uploadForm.experimentTitle = item.experimentTitle;
      this.uploadForm.experimentalId = item.id;
      this.uploadForm.tenantId = item.tenantId;
      this.uploadForm.deptId = item.deptId;
      this.uploadForm.experimentNumber = item.experimentNumber;
      if(!this.uploadForm.fid) this.uploadForm.fid = getUUID()
      // console.log("===========>"+JSON.stringify(this.uploadForm));
      this.loadMedia();
    },
    get_ExperimentalList() {
      this.experimentalList = []
      let params = {
        pageNo: 1,
        pageSize: 500,
      }
      ZkExperimentalApi.getZkExperimentalPage(params).then(response => {
        this.experimentalList.push(...response.data.list)
      })
    },
    /** 加载媒体 */
    async loadMedia() {
      try {
        if (!this.uploadForm.experimentId) {
          this.mediaList = []
          return
        }
        this.loading = true
        let query = {
          pageNo: 1,
          pageSize: 10,
          experimentId: this.uploadForm.experimentId,
        };
        if (this.filterCategory) {
          query.bodyType = this.filterCategory;
        }
        const res = await ZkExperimentalMediaApi.getZkExperimentalMediaPage(query)
        this.mediaList = res.data.list || []
      } catch (error) {
        this.$modal.msgError('加载媒体列表失败')
      } finally {
        this.loading = false
      }
    },

    /** 实验变更 */
    handleExperimentChange() {
      this.fileList = []
    },

    /** 上传前检查 */
    handleBeforeUpload(file) {
      if (this.uploadForm.fileType === 1) {
        const isImage = file.type.startsWith('image/')
        if (!isImage) {
          this.$modal.msgError('请上传图片文件')
          return false
        }
      } else {
        const isVideo = file.type.startsWith('video/')
        if (!isVideo) {
          this.$modal.msgError('请上传视频文件')
          return false
        }
      }
      return true
    },

    /** 上传成功 */
    handleUploadSuccess(res, file) {
      if (res.code === 0) {
        this.fileList.push({
          name: file.name,
          url: res.data,
          uid: file.uid
        })
      } else {
        this.$modal.msgError(res.msg || '上传失败')
      }
    },

    /** 上传失败 */
    handleUploadError() {
      this.$modal.msgError('上传失败，请重试')
    },

    /** 移除文件 */
    handleRemove(file, fileList) {
      this.fileList = fileList
    },

    /** 是否已有有效上传文件 */
    hasUploadedFiles() {
      const urls = (this.fileUrls || []).map(u => String(u || '').trim()).filter(Boolean)
      const formUrls = String(this.uploadForm.fileUrls || '')
        .split(',')
        .map(u => u.trim())
        .filter(Boolean)
      return urls.length > 0 && formUrls.length > 0
    },

    /** 保存 */
    async handleSave() {
      if (!this.uploadForm.experimentId) {
        this.$modal.msgWarning('请选择实验')
        return
      }
      if (!this.hasUploadedFiles()) {
        this.$modal.msgWarning('请先上传文件后再保存')
        return
      }

      try {
        this.saving = true
        const experiment =
          (this.experiments || []).find(e => e.id === this.uploadForm.experimentId) ||
          (this.experimentalList || []).find(e => e.id === this.uploadForm.experimentId)
        if (!experiment) {
          this.$modal.msgWarning('未找到所选实验，请重新选择')
          return
        }

        const data = {
          experimentId: this.uploadForm.experimentId,
          experimentNumber: experiment.experimentNumber,
          fileType: this.uploadForm.fileType,
          bodyType: this.uploadForm.bodyType,
          fileNames: this.uploadForm.fileNames,
          fileUrls: this.uploadForm.fileUrls,
          tenantId: experiment.tenantId,
          deptId: experiment.deptId,
        }
        if (this.uploadForm.sonBodyType) data.bodyType += ',' + this.uploadForm.sonBodyType

        await ZkExperimentalMediaApi.createZkExperimentalMedia(data)
        this.handleReset(this.uploadForm.experimentId)
        this.$modal.msgSuccess('保存成功')
        this.loadMedia()
        this.$emit('refresh')
      } catch (error) {
        // 错误已在拦截器中处理
      } finally {
        this.saving = false
      }
    },

    /** 重置 */
    handleReset(experimentId) {
      const keepId =
        experimentId != null && (typeof experimentId === 'string' || typeof experimentId === 'number')
          ? experimentId
          : this.uploadForm.experimentId
      this.uploadForm = {
        pondNumber: '',
        id: undefined,
        experimentId: keepId,
        experimentNumber: undefined,
        fileType: '1',
        bodyType: '',
        sonBodyType: undefined,
        fid: getUUID(),
        fileNames: undefined,
        fileUrls: undefined,
        deptId: undefined,
      }
      this.fileList = []
      this.fileUrls = []
      this.fileNames = []
      if (this.$refs.upload) {
        this.$refs.upload.clearFiles()
      }
    },

    /** 预览 */
    handlePreview(media) {
      this.previewMedia = media
      this.previewVisible = true
    },

    /** 获取文件URL */
    getFileUrl(fileUrls) {
      if (!fileUrls) return ''
      const urls = fileUrls.split(',')
      return urls[0]
    },

    /** 图片加载错误 */
    handleImageError(e) {
      e.target.src = require('@/assets/404_images/404.png')
    },

    /** 获取类别标签 */
    getCategoryLabel(category) {
      const map = {
        body: '体表',
        gill: '鳃',
        organ: '内脏'
      }
      return map[category] || category
    },

    /** 获取子类别标签 */
    getSubCategoryLabel(category, subCategory) {
      if (category === 'body') {
        const map = {
          side: '体侧',
          head: '头嘴',
          abdomen: '腹部',
          fin: '各鳍'
        }
        return map[subCategory] || subCategory
      } else if (category === 'organ') {
        const map = {
          liver: '肝脏',
          intestine: '肠道'
        }
        return map[subCategory] || subCategory
      }
      return subCategory
    }
  }
}
</script>

<style scoped lang="scss">
.media-management {
  ::v-deep .el-card {
    background-color: #12202d !important;
    border: 1px solid rgba(255, 255, 255, 0.08) !important;
    border-radius: 10px !important;
    color: #ffffff !important;
    box-shadow: none !important;

    .el-card__header {
      background-color: transparent !important;
      border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
      color: rgba(255, 255, 255, 0.9) !important;
      padding: 14px 18px !important;
      font-weight: 600;
    }

    .el-card__body {
      background-color: transparent !important;
      color: #ffffff !important;
      padding: 16px 18px !important;
    }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: #ffffff !important;
  }

  // 表单样式
  ::v-deep .el-form {
    .el-form-item__label {
      color: rgba(255, 255, 255, 0.8) !important;
    }

    .el-input__inner {
      background-color: rgba(255, 255, 255, 0.05) !important;
      border-color: rgba(255, 255, 255, 0.1) !important;
      color: #ffffff !important;

      &::placeholder {
        color: rgba(255, 255, 255, 0.4) !important;
      }
    }

    .el-select .el-input__inner {
      background-color: rgba(255, 255, 255, 0.05) !important;
      border-color: rgba(255, 255, 255, 0.1) !important;
      color: #ffffff !important;
    }

    .el-select .el-input__suffix {
      color: rgba(255, 255, 255, 0.6) !important;
    }
  }

  // 上传组件样式
  ::v-deep .el-upload {
    .el-upload--picture-card {
      background-color: rgba(255, 255, 255, 0.05) !important;
      border-color: rgba(255, 255, 255, 0.1) !important;

      &:hover {
        border-color: #54907a !important;
      }
    }

    .el-upload-list--picture-card .el-upload-list__item {
      background-color: rgba(255, 255, 255, 0.05) !important;
      border-color: rgba(255, 255, 255, 0.1) !important;
    }
  }

  .media-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 20px;
  }

  .media-item {
    background: #12202d !important;
    border: 1px solid rgba(255, 255, 255, 0.08) !important;
    border-radius: 10px;
    overflow: hidden;
    cursor: pointer;
    transition: border-color 0.2s ease, transform 0.2s ease;
    box-shadow: none;

    &:hover {
      box-shadow: none;
      transform: translateY(-2px);
      border-color: rgba(84, 144, 122, 0.45) !important;
    }

    .media-thumbnail {
      position: relative;
      width: 100%;
      height: 150px;
      background: rgba(255, 255, 255, 0.05);
      display: flex;
      align-items: center;
      justify-content: center;
      overflow: hidden;

      img,
      video {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .media-type-badge {
        position: absolute;
        top: 8px;
        right: 8px;
        background: rgba(0, 0, 0, 0.6);
        color: #fff;
        padding: 4px 8px;
        border-radius: 4px;
        font-size: 12px;
      }
    }

    .media-info {
      padding: 12px;

      .media-category {
        font-weight: 500;
        color: #ffffff;
        margin-bottom: 8px;
        font-size: 14px;
      }

      .media-meta {
        font-size: 12px;
        color: rgba(255, 255, 255, 0.6);
        margin-bottom: 4px;
      }

      .media-time {
        font-size: 12px;
        color: rgba(255, 255, 255, 0.4);
      }
    }
  }

  .preview-content {
    text-align: center;

    .preview-info {
      text-align: left;
      padding: 20px;
      background: rgba(255, 255, 255, 0.05);
      border-radius: 4px;

      p {
        margin: 8px 0;
        color: rgba(255, 255, 255, 0.8);
      }
    }
  }

  // 空状态样式
  ::v-deep .el-empty {
    .el-empty__description {
      color: rgba(255, 255, 255, 0.6) !important;
    }
  }

  // 预览对话框样式
  ::v-deep .el-dialog {
    background-color: #0f1a26 !important;
    border: 1px solid rgba(255, 255, 255, 0.12) !important;

    .el-dialog__header {
      background-color: transparent !important;
      border-bottom: 1px solid rgba(255, 255, 255, 0.1) !important;
      padding: 20px 20px 15px;

      .el-dialog__title {
        color: #ffffff !important;
      }

      .el-dialog__close {
        color: rgba(255, 255, 255, 0.6) !important;

        &:hover {
          color: #ffffff !important;
        }
      }
    }

    .el-dialog__body {
      background-color: transparent !important;
      color: #ffffff !important;
      padding: 20px;
    }
  }
}
</style>