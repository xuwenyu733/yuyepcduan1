<template>
  <div class="dynamic-data-form">
    <el-row :gutter="20">
      <el-col
        v-for="(field, index) in fields"
        :key="index"
        :span="getColSpan(field)"
      >
        <el-form-item :label="field.keyCh" :required="field.required">
          <!-- input 类型 -->
          <el-input-number
            v-if="field.type === 'input' && isNumericField(field)"
            :value="getNumericValue(field.val)"
            :precision="getPrecision(field)"
            :min="0"
            :disabled="isFieldDisabled(field)"
            style="width: 100%"
            @change="(val) => { field.val = val; handleFieldChange(); }"
          />
          <el-input
            v-else-if="field.type === 'input'"
            v-model="field.val"
            :placeholder="getPlaceholder(field)"
            :disabled="isFieldDisabled(field)"
            @input="handleFieldChange"
          />

          <!-- input_calculate 类型 -->
          <el-input-number
            v-else-if="field.type === 'input_calculate'"
            :value="getNumericValue(field.val)"
            :precision="getPrecision(field)"
            :disabled="true"
            style="width: 100%"
          />

          <!-- select 类型 -->
          <el-select
            v-else-if="field.type === 'select'"
            v-model="field.val"
            :placeholder="`请选择${field.keyCh}`"
            style="width: 100%"
            @change="handleFieldChange"
          >
            <el-option
              v-for="option in field.slectList"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'DynamicDataForm',
  props: {
    fields: {
      type: Array,
      required: true,
      default: () => []
    },
    activeTabLabel: {
      type: String,
      default: ''
    },
    poolInfo: {
      type: Object,
      default: () => ({})
    },
    cumulativeMortality: {
      type: Number,
      default: 0
    }
  },
  watch: {
    fields: {
      deep: true,
      immediate: true,
      handler() {
        this.$nextTick(() => {
          this.calculateFields()
        })
      }
    },
    poolInfo: {
      deep: true,
      handler() {
        this.$nextTick(() => this.calculateFields())
      }
    },
    cumulativeMortality() {
      this.$nextTick(() => this.calculateFields())
    }
  },
  methods: {
    /** 获取列宽 */
    getColSpan(field) {
      // 根据字段数量动态调整列宽
      const totalFields = this.fields.length
      if (totalFields <= 2) return 12
      if (totalFields <= 4) return 6
      if (totalFields <= 6) return 8
      return 6
    },

    /** 判断是否为数字字段 */
    isNumericField(field) {
      const numericKeywords = ['重量', '量', '率', '比', '温度', 'pH', '氧', '氮', '尾', 'kg', 'g', '%', 'mg/L', '℃', 'Kg']
      return numericKeywords.some(keyword => field.keyCh.includes(keyword))
    },

    /** 获取精度 */
    getPrecision(field) {
      if (field.keyCh.includes('率') || field.keyCh.includes('%')) return 2
      if (field.keyCh.includes('重量') || field.keyCh.includes('kg') || field.keyCh.includes('g') || field.keyCh.includes('Kg')) return 2
      if (field.keyCh.includes('温度') || field.keyCh.includes('pH') || field.keyCh.includes('氧')) return 1
      if (field.keyCh.includes('氮')) return 2
      if (field.keyCh.includes('尾')) return 0
      return 2
    },

    /** 获取占位符 */
    getPlaceholder(field) {
      return `请输入${field.keyCh}`
    },

    /** 判断字段是否禁用 */
    isFieldDisabled(field) {
      // 存活数据中的成活率字段自动计算，设为禁用
      if (this.activeTabLabel === '存活数据' && (field.keyCh.includes('成活率') || field.keyEn === 'survivalRate')) {
        return true;
      }
      return false;
    },

    /** 获取数字值 */
    getNumericValue(val) {
      if (val === '' || val === null || val === undefined) {
        return 0
      }
      return parseFloat(val) || 0
    },

    /** 字段值变化 */
    handleFieldChange() {
      // 触发计算字段的重新计算
      this.$nextTick(() => {
        this.calculateFields()
      })
    },

    /** 计算所有计算字段 */
    calculateFields() {
      // 需求2：存活数据特殊计算逻辑
      if (this.activeTabLabel === '存活数据') {
        const mortalityField = this.fields.find(f => f.keyCh.includes('死亡量') || f.keyEn === 'mortality');
        const survivalRateField = this.fields.find(f => f.keyCh.includes('成活率') || f.keyCh.includes('存活率') || f.keyEn === 'survivalRate');
        
        if (mortalityField && survivalRateField) {
          const initCount = Number(this.poolInfo && this.poolInfo.initFishNum) || 0;
          if (initCount > 0) {
            const newMortality = parseFloat(mortalityField.val) || 0;
            const currentCount = Math.max(0, initCount - this.cumulativeMortality - newMortality);
            survivalRateField.val = parseFloat(((currentCount / initCount) * 100).toFixed(2));
          }
        }
      }

      this.fields.forEach((field, fieldIndex) => {
        if (field.type === 'input_calculate' && field.type_remark) {
          try {
            let formula = field.type_remark.trim()
            
            // 首先替换 keyEn 变量名（优先级更高，因为更具体）
            this.fields.forEach(f => {
              if (f.type !== 'input_calculate' && f.keyEn && f.val !== '' && f.val !== null && f.val !== undefined) {
                const value = parseFloat(f.val) || 0
                // 替换 keyEn 变量名，使用单词边界确保精确匹配
                formula = formula.replace(
                  new RegExp(`\\b${f.keyEn}\\b`, 'g'),
                  value
                )
              }
            })

            // 然后替换 A、B、C 等字母变量（按字段顺序，排除计算字段）
            let letterIndex = 0
            this.fields.forEach((f, index) => {
              if (f.type !== 'input_calculate') {
                const letter = String.fromCharCode(65 + letterIndex) // A, B, C...
                if (f.val !== '' && f.val !== null && f.val !== undefined) {
                  const value = parseFloat(f.val) || 0
                  // 替换字母变量，使用单词边界确保精确匹配
                  formula = formula.replace(
                    new RegExp(`\\b${letter}\\b`, 'g'),
                    value
                  )
                }
                letterIndex++
              }
            })

            // 检查公式中是否还有未替换的变量（排除 Math. 等内置对象）
            const cleanedFormula = formula.replace(/Math\.\w+/g, '')
            const hasUnresolvedVars = /[A-Za-z_][A-Za-z0-9_]*/.test(cleanedFormula)
            if (hasUnresolvedVars) {
              field.val = ''
              return
            }

            // 执行计算
            // eslint-disable-next-line no-eval
            const result = eval(formula)
            if (isNaN(result) || !isFinite(result)) {
              field.val = ''
            } else {
              field.val = parseFloat(result.toFixed(this.getPrecision(field)))
            }
          } catch (error) {
            console.error('计算字段失败:', field.keyCh, field.type_remark, error)
            field.val = ''
          }
        }
      })
    },

    /** 获取表单数据 */
    getFormData() {
      return this.fields;
      
      const data = {}
      this.fields.forEach(field => {
        if (field.keyEn) {
          // 对于数字字段，0 也是有效值
          if (this.isNumericField(field)) {
            if (field.val !== '' && field.val !== null && field.val !== undefined) {
              data[field.keyEn] = parseFloat(field.val) || 0
            }
          } else {
            // 对于非数字字段，空值不包含
            if (field.val !== '' && field.val !== null && field.val !== undefined) {
              data[field.keyEn] = field.val
            }
          }
        }
      })
      // console.log(`this.fields==============>${JSON.stringify(this.fields)}`);
      return data
    },

    /** 重置表单 */
    resetForm() {
      this.fields.forEach(field => {
        const isSurvivalRate = (field.keyCh && (field.keyCh.includes('成活率') || field.keyCh.includes('存活率')))
          || field.keyEn === 'survivalRate'
        if (isSurvivalRate) {
          field.val = ''
        } else if (this.isNumericField(field)) {
          field.val = 0
        } else {
          field.val = ''
        }
      })
      // 重置后重新计算
      this.$nextTick(() => {
        this.calculateFields()
      })
    }
  }
}
</script>

<style scoped lang="scss">
.dynamic-data-form {
  // 确保表单样式继承父组件
}
</style>