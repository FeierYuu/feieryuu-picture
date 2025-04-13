<template>
  <a-layout class="error-layout">
    <a-layout class="content-layout">
      <a-layout-content class="content">
        <div class="error-container">
          <!-- 圆形背景容器 -->
          <div class="circle-container">
            <!-- 404数字 -->
            <div class="error-code">404</div>

          </div>

          <!-- 文字内容 -->
          <div class="error-content">
            <h2>找不到页面</h2>
            <p class="description">
              我们无法找到您要访问的页面，它可能已漂移到未知轨道
            </p>
            <a-button
              type="primary"
              class="home-btn"
              @click="goHome"
            >
              <template #icon>
                <RocketOutlined />
              </template>
              返回主页
            </a-button>
          </div>
        </div>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { RocketOutlined } from '@ant-design/icons-vue'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<style scoped lang="less">
.error-layout {
  height: 100vh;
  background: #fff;

  .content-layout {
    background: linear-gradient(135deg, #f5f7fa 0%, #ffffff 100%);

    .content {
      display: flex;
      align-items: center;
      justify-content: center;
      height: 100vh;
    }
  }
}

.error-container {
  position: relative;
  max-width: 800px;
  width: 100%;
  padding: 40px;
  text-align: center;
}

.circle-container {
  position: relative;
  width: 300px;
  height: 300px;
  margin: 0 auto 40px;
  border-radius: 50%;
  background: linear-gradient(145deg, #ffffff, #e6e9ed);
  box-shadow: 0 0 0 10px rgba(0, 123, 255, 0.1),
  0 10px 30px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  animation: float 6s ease-in-out infinite;

  .error-code {
    position: absolute;
    font-size: 7em;
    font-weight: 800;
    color: rgba(0, 123, 255, 0.15);
    z-index: 1;
  }
}


@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-15px);
  }
}

@keyframes armSwing {
  0%, 100% {
    transform: rotate(-10deg);
  }
  50% {
    transform: rotate(10deg);
  }
}

@keyframes legSwing {
  0%, 100% {
    transform: rotate(-5deg);
  }
  50% {
    transform: rotate(5deg);
  }
}

@keyframes blink {
  0% {
    opacity: 0.5;
  }
  100% {
    opacity: 1;
  }
}

.error-content {
  position: relative;
  z-index: 3;

  h2 {
    color: #2c3e50;
    font-size: 1.8em;
    margin-bottom: 20px;
    font-weight: 600;
  }

  .description {
    color: #7f8c8d;
    max-width: 500px;
    margin: 0 auto 30px;
    line-height: 1.6;
    font-size: 1.1em;
  }
}

.home-btn {
  height: 48px;
  padding: 0 36px;
  font-size: 1.1em;
  font-weight: 500;
  border-radius: 24px;
  background: #007bff;
  border: none;
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(0, 123, 255, 0.4);
    background: #0069d9;
  }

  :deep(.anticon) {
    margin-right: 8px;
  }
}

@media (max-width: 768px) {
  .circle-container {
    width: 250px;
    height: 250px;

    .error-code {
      font-size: 5em;
    }
  }

  .astronaut {
    transform: scale(0.8);
  }

  h2 {
    font-size: 1.5em !important;
  }
}
</style>
