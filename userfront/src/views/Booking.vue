<template>
  <div class="container page" v-loading="loading">
    <h1 class="page-title">预约下单</h1>

    <el-card class="booking-card card-shadow">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        size="large"
      >
        <el-form-item label="体检医院" prop="hospitalId">
          <el-select
            v-model="form.hospitalId"
            placeholder="请选择体检医院"
            style="width: 100%"
            @change="onHospitalChange"
          >
            <el-option
              v-for="h in hospitals"
              :key="h.id"
              :label="h.name"
              :value="h.id"
              :disabled="h.status !== 1"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="接待医生" prop="doctorId">
          <el-select
            v-model="form.doctorId"
            placeholder="请选择接待医生"
            style="width: 100%"
            :disabled="!form.hospitalId"
          >
            <el-option
              v-for="d in doctors"
              :key="d.id"
              :label="`${d.realName}（${d.department}）`"
              :value="d.id"
            />
          </el-select>
          <div v-if="form.hospitalId && !doctors.length" class="doctor-tip">
            该医院暂无可预约医生
          </div>
        </el-form-item>

        <el-form-item label="体检套餐" prop="setmealId">
          <el-select
            v-model="form.setmealId"
            placeholder="请选择体检套餐"
            style="width: 100%"
            @change="onSetmealChange"
          >
            <el-option
              v-for="s in availableSetmeals"
              :key="s.id"
              :label="`${s.name}（¥${s.price}）`"
              :value="s.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="体检日期" prop="orderDate">
          <el-date-picker
            v-model="form.orderDate"
            type="date"
            placeholder="请选择体检日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            :disabled-date="disabledDate"
            style="width: 100%"
          />
        </el-form-item>

        <!-- 已选套餐信息预览 -->
        <div v-if="selectedSetmeal" class="setmeal-preview">
          <h4>套餐详情</h4>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="套餐名称">
              {{ selectedSetmeal.name }}
            </el-descriptions-item>
            <el-descriptions-item label="套餐价格">
              <span class="price">¥{{ selectedSetmeal.price }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="套餐描述" :span="2">
              {{ selectedSetmeal.description }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="btn-gradient"
            :loading="submitting"
            @click="onSubmit"
          >
            提交预约
          </el-button>
          <el-button size="large" @click="router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getHospitalList, getHospitalDetail } from '@/api/hospital'
import { getSetmealList, getSetmealDetail } from '@/api/setmeal'
import { getDoctorsByHospital } from '@/api/doctor'
import { createOrder } from '@/api/order'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const submitting = ref(false)

const hospitals = ref([])
const allSetmeals = ref([]) // 当前选中医院的套餐列表
const doctors = ref([]) // 当前选中医院的接待医生
const selectedSetmeal = ref(null)

const form = reactive({
  hospitalId: route.query.hospitalId ? Number(route.query.hospitalId) : undefined,
  setmealId: route.query.setmealId ? Number(route.query.setmealId) : undefined,
  doctorId: undefined,
  orderDate: ''
})

const rules = {
  hospitalId: [{ required: true, message: '请选择体检医院', trigger: 'change' }],
  doctorId: [{ required: true, message: '请选择接待医生', trigger: 'change' }],
  setmealId: [{ required: true, message: '请选择体检套餐', trigger: 'change' }],
  orderDate: [{ required: true, message: '请选择体检日期', trigger: 'change' }]
}

// 可选套餐：若已选医院则展示该医院套餐，否则展示全部
const availableSetmeals = computed(() => {
  if (form.hospitalId) return allSetmeals.value
  return allSetmeals.value
})

// 禁选今天及过去的日期
function disabledDate(date) {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return date.getTime() < today.getTime()
}

async function loadDoctors(hospitalId) {
  doctors.value = []
  form.doctorId = undefined
  if (!hospitalId) return
  try {
    const res = await getDoctorsByHospital(hospitalId)
    if (res && res.code === 200) doctors.value = res.data || []
  } catch (e) {
    // 忽略
  }
}

async function onHospitalChange(hospitalId) {
  allSetmeals.value = []
  selectedSetmeal.value = null
  form.setmealId = undefined
  await loadDoctors(hospitalId)
  if (hospitalId) {
    try {
      const res = await getHospitalDetail(hospitalId)
      if (res && res.code === 200 && res.data) {
        allSetmeals.value = res.data.setmealList || []
      }
    } catch (e) {
      // 忽略
    }
  } else {
    await loadAllSetmeals()
  }
}

async function loadAllSetmeals() {
  const res = await getSetmealList()
  if (res && res.code === 200) allSetmeals.value = res.data || []
}

async function onSetmealChange(setmealId) {
  if (!setmealId) {
    selectedSetmeal.value = null
    return
  }
  try {
    const res = await getSetmealDetail(setmealId)
    if (res && res.code === 200) selectedSetmeal.value = res.data
  } catch (e) {
    // 忽略
  }
}

async function onSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const res = await createOrder({
        hospitalId: form.hospitalId,
        setmealId: form.setmealId,
        doctorId: form.doctorId,
        orderDate: form.orderDate
      })
      if (res && res.code === 200) {
        ElMessage.success('预约成功')
        router.push('/order')
      }
    } finally {
      submitting.value = false
    }
  })
}

async function init() {
  loading.value = true
  try {
    const hRes = await getHospitalList()
    if (hRes && hRes.code === 200) hospitals.value = hRes.data || []

    // 初始化套餐列表和接待医生
    if (form.hospitalId) {
      const [res] = await Promise.all([
        getHospitalDetail(form.hospitalId),
        loadDoctors(form.hospitalId)
      ])
      if (res && res.code === 200 && res.data) {
        allSetmeals.value = res.data.setmealList || []
      }
    } else {
      await loadAllSetmeals()
    }

    // 若有预选套餐，加载详情
    if (form.setmealId) {
      await onSetmealChange(form.setmealId)
    }
  } finally {
    loading.value = false
  }
}

onMounted(init)
</script>

<style scoped>
.booking-card {
  max-width: 720px;
  margin: 0 auto;
  padding: 10px;
}

.setmeal-preview {
  background: #f5f7fa;
  padding: 16px;
  border-radius: 8px;
  margin: 12px 0 24px;
}

.setmeal-preview h4 {
  margin-bottom: 12px;
  color: var(--primary-color);
}

.price {
  color: var(--danger-color);
  font-size: 18px;
  font-weight: 700;
}

.doctor-tip {
  font-size: 12px;
  color: var(--text-secondary, #909399);
  line-height: 1.4;
}
</style>
