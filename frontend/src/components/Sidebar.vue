<template>
  <n-space vertical>
    <n-layout-sider
      class="menu"
      bordered
      collapse-mode="width"
      :collapsed-width="64"
      :width="240"
      :collapsed="collapsed"
      show-trigger
      @click="
        collapsed = !collapsed;
        $emit('menu-toggle', !collapsed);
      ">
      <div class="menu-content" v-on:click.stop>
        <div>
          <h3
            :class="[
              'title',
              {
                hidden: collapsed,
              },
            ]">
            Choix du sprint
          </h3>
          <div class="sprint-selector">
            <SprintSelector v-if="!collapsed" />
            <div v-else class="changeSprintDots">
              <n-tooltip placement="right" trigger="hover">
                <template #trigger>
                  <div
                    class="dotsIcon"
                    @click="
                      collapsed = !collapsed;
                      $emit('menu-toggle', !collapsed);
                    ">
                    <n-icon :size="24" :component="EllipsisHorizontalOutline" />
                  </div>
                </template>
                <span> Changer de sprint </span>
              </n-tooltip>
            </div>
          </div>
          <h3 :class="['title', { hidden: collapsed }]">{{ title }}</h3>
        </div>
        <n-menu
          :collapsed="collapsed"
          :collapsed-width="64"
          :collapsed-icon-size="22"
          :options="menuOptions"
          :render-label="renderMenuLabel"
          :render-icon="renderMenuIcon"
          @select="selectTab" />
      </div>
    </n-layout-sider>
  </n-space>
</template>

<script lang="ts">
import { h, ref, defineComponent } from 'vue';
import type { PropType } from 'vue';
import { NIcon } from 'naive-ui';
import type { MenuOption } from 'naive-ui';
import { PeopleOutline, EllipsisHorizontalOutline } from '@vicons/ionicons5';
import { Star28Regular } from '@vicons/fluent';
import SprintSelector from '@/components/SprintSelector.vue';

export default defineComponent({
  components: { SprintSelector },
  props: {
    tabs: {
      type: Array as PropType<string[]>,
      required: true,
    },
    onClick: {
      type: Function as PropType<(tabIndex: number) => void>,
      required: true,
    },
    title: {
      type: String,
      required: true,
    },
    selectedTabIndex: {
      type: Number,
      default: 0,
    },
    myTeamName: {
      type: String as PropType<string | null>,
      default: null,
    },
  },
  setup(props) {
    const menuOptions: MenuOption[] = props.tabs.map((tab, index) => ({
      label: tab,
      key: index.toString(),
    }));

    const selectTab = (key: string) => {
      const index = Number(key);
      props.onClick(index);
    };

    const collapsed = ref(false);
    return {
      menuOptions,
      collapsed,
      // renderMenuLabel(option: MenuOption) {
      //   return option.label as string;
      // },
      renderMenuLabel(option: MenuOption) {
        const isMyTeam = props.myTeamName === option.label;

        return h(
          'div',
          {
            style: {
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
            },
          },
          [
            h('span', option.label as string),
            isMyTeam
              ? h(
                  NIcon,
                  {
                    size: 22,
                  },
                  { default: () => h(Star28Regular) }
                )
              : null,
          ]
        );
      },
      renderMenuIcon() {
        return h(NIcon, null, { default: () => h(PeopleOutline) });
      },
      selectTab,
      EllipsisHorizontalOutline,
      Star28Regular,
    };
  },
});
</script>

<style scoped>
.title {
  font-weight: normal;
  margin-top: 20px;
  margin-bottom: 10px;
  margin-left: 20px;
  color: #8c8c8c;
  font-size: 15px;
  white-space: nowrap;
  opacity: 1;
  transition: opacity 0.3s;
}
/* Apply margin except first element*/
.title:not(:first-child) {
  margin-top: 50px;
}
.my-team-icon {
  color: var(--secondaryColor);
}
.menu {
  position: fixed;
  height: 100vh;
  background-color: var(--onBackground);
}
.sprint-selector {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  height: 30px;
  margin-bottom: 10px;
}
.hidden {
  opacity: 0;
}
.changeSprintDots {
  cursor: pointer;
  z-index: 99;
  transition: 0.2s;
}
.changeSprintDots .dotsIcon {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px;
  margin-top: 55px;
  border-radius: 5px;
}
.changeSprintDots .dotsIcon:hover {
  background-color: var(--backgroundColor);
}
</style>
