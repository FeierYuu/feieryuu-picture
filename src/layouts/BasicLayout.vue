<script setup lang="ts">
import GlobalHeader from '@/components/GlobalHeader.vue'
import GlobalSider from '@/components/GlobalSider.vue'
</script>

<template>
  <div id="basicLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header">
        <GlobalHeader></GlobalHeader>
      </a-layout-header>
      <a-layout>
        <global-sider class="sider"></global-sider>
        <a-layout-content class="content">
          <router-view />
        </a-layout-content>
      </a-layout>

      <a-layout-footer class="footer">
        <a href="https://github.com/FeierYuu" target="_blank"> 云图库 by 程序员FeierYuu </a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<style scoped>
#basicLayout .header {
  background-color: white;
  padding-inline: 16px;
  margin-bottom: 1px;
  color: unset;
}

#basicLayout .footer {
  background-color: #efefef;
  padding: 16px;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
  font-size: 16px;
}

#basicLayout .content {
  background: linear-gradient(to right, #fefefe, #fff);
  margin-bottom: 28px;
  padding: 28px;

}

#basicLayout .sider {
  background: #fff;
  border-right: 0.5px solid #eee;
  padding-top: 10px;
}

#basicLayout :deep(.ant-menu-root) {
  border-bottom: none !important;
  border-inline-end: none !important;
}

</style>

