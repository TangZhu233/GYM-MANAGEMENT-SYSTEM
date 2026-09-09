<template>
  <div class="chat-page">
    <div class="page-header">
      <div>
        <h2>健康助手（AI）</h2>
        <p>结合你的身高体重给出训练 / 饮食 / 恢复建议（仅供参考）</p>
      </div>
      <div class="header-actions">
        <el-button @click="router.push('/user/toHealth')">健康评估</el-button>
        <el-button @click="router.push('/toUserMain')">返回主页</el-button>
      </div>
    </div>

    <el-card class="chat-card" shadow="never">
      <div ref="listRef" class="msg-list">
        <div v-for="(m, i) in messages" :key="i" class="msg-row" :class="m.role">
          <div class="bubble" :class="m.role">{{ m.text }}</div>
        </div>
        <div v-if="waiting" class="msg-row assistant">
          <div class="bubble assistant thinking">思考中…</div>
        </div>
      </div>

      <div v-if="messages.length <= 1" class="chips">
        <el-tag
          v-for="q in quickQuestions"
          :key="q"
          class="chip-tag"
          effect="plain"
          @click="ask(q)"
        >{{ q }}</el-tag>
      </div>

      <div class="input-bar">
        <el-input
          v-model="question"
          type="textarea"
          :rows="2"
          resize="none"
          placeholder="问我：今天练什么？偏瘦怎么增肌？减脂晚餐吃什么？…"
          @keydown.enter.exact.prevent="send"
        />
        <el-button type="primary" :loading="waiting" :disabled="!question.trim()" @click="send">发送</el-button>
      </div>
      <p class="note">AI 输出由大模型生成，内容仅供健康参考，不替代医生诊断。</p>
    </el-card>
  </div>
</template>

<script setup>
import { nextTick, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { postForm } from '../api/client'

const router = useRouter()
const messages = ref([
  { role: 'assistant', text: '你好，我是健康助手～ 我会根据你的身高体重，给你训练、饮食、恢复方面的建议。想问点什么？' }
])
const question = ref('')
const waiting = ref(false)
const listRef = ref(null)

const quickQuestions = [
  '我今天可以练点什么？',
  '偏瘦想增肌该怎么吃？',
  '减脂晚餐有什么推荐？',
  '怎么把运动坚持成习惯？',
  '运动后肌肉酸痛怎么办？'
]

async function scrollBottom() {
  await nextTick()
  if (listRef.value) listRef.value.scrollTop = listRef.value.scrollHeight
}

async function send() {
  const text = question.value.trim()
  if (!text || waiting.value) return
  messages.value.push({ role: 'user', text })
  question.value = ''
  waiting.value = true
  scrollBottom()
  try {
    const resp = await postForm('/api/user/chat', { question: text })
    const data = resp.data || {}
    if (data.success) {
      messages.value.push({ role: 'assistant', text: data.answer || '（没有返回内容）' })
    } else {
      ElMessage.error(data.message || '获取回答失败')
    }
  } catch (e) {
    ElMessage.error('网络异常，请稍后重试')
  } finally {
    waiting.value = false
    scrollBottom()
  }
}

function ask(q) {
  question.value = q
  send()
}

onMounted(scrollBottom)
</script>

<style scoped>
.chat-page {
  padding: 24px;
  max-width: 880px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 18px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

h2,
p {
  margin: 0;
}

h2 {
  color: #0d1b2f;
  font-size: 26px;
  font-weight: 800;
}

.page-header p {
  margin-top: 6px;
  color: #718095;
  font-size: 14px;
}

.chat-card {
  border: 1px solid #e3eaf3;
  border-radius: 14px;
  box-shadow: 0 10px 22px rgba(30, 50, 77, 0.045);
}

.msg-list {
  height: 420px;
  overflow-y: auto;
  padding: 6px 4px 10px;
}

.msg-row {
  display: flex;
  margin-bottom: 12px;
}

.msg-row.user {
  justify-content: flex-end;
}

.bubble {
  max-width: 76%;
  padding: 10px 14px;
  border-radius: 14px;
  font-size: 14px;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}

.bubble.assistant {
  border-top-left-radius: 4px;
  background: #f1f5f9;
  color: #334155;
}

.bubble.user {
  border-top-right-radius: 4px;
  background: #0ea66f;
  color: #ffffff;
}

.bubble.thinking {
  color: #94a3b8;
}

.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 4px 0 14px;
  border-bottom: 1px solid #eef2f7;
  margin-bottom: 12px;
}

.chip-tag {
  cursor: pointer;
}

.chip-tag:hover {
  border-color: #0ea66f;
  color: #0ea66f;
}

.input-bar {
  display: flex;
  gap: 12px;
  align-items: flex-end;
  margin-top: 4px;
}

.input-bar .el-textarea {
  flex: 1;
}

.note {
  margin-top: 10px;
  color: #94a3b8;
  font-size: 12px;
  text-align: right;
}
</style>
