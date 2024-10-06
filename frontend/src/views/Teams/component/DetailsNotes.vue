<template>
  <div>
    <div class="explications">
      <p class="small-text"><strong>Détails des calculs des notes:</strong></p>
    </div>
    <div class="explications">
      <p class="small-text">
        <strong class="black-text">Note d'équipe initiale:</strong> Moyenne de SPCO, TESO, PRMO, SUPR
      </p>
      <p class="small-text">
        <strong class="black-text">Note de travail:</strong> Somme de la note initial et des bonus/malus
      </p>
    </div>
    <div class="explications">
      <p class="small-text">
        <strong class="black-text">Note de présentation:</strong> Moyenne de SSPR(x2), OTPR, TCPR
      </p>
      <p class="small-text">
        <strong class="black-text">Note de sprint:</strong> 70% de la note de travail + 30% de la note de présentation
      </p>
    </div>
    <n-descriptions label-placement="top" bordered :column="9">
      <n-descriptions-item>
        <template #label>
          <div class="icon-title">
            <PeopleOutline class="header-icon"/>
            Note d'Équipe Initiale
          </div>
        </template>
        <div class="popover-container">
          <n-popover
            v-for="(value, key) in initialNotes"
            :key="key"
            trigger="hover"
            placement="top"
          >
            <template #trigger>
              <n-tag
                :bordered="false"
                :round="true"
                :color="{ color: 'var(--onBackground)', textColor: 'var(--textColor)' }"
              >
                {{ key.toString().toUpperCase() }}: {{ value.value ?? '--' }}/20
              </n-tag>
            </template>
            <template #default>
              <h4>{{value.gradeType.description}}</h4>
              <div class="evaluations-table">
                <div class="evaluation-row" v-for="evaluation in value.evaluations" :key="evaluation.id">
                  <span class="evaluator-name">{{ evaluation.evaluator.firstName }} {{ evaluation.evaluator.lastName }}</span>
                  <span class="evaluator-status">
                    <n-icon :component="getIcon(evaluation.status)" :color="getColor(evaluation.status)" />
                  </span>
                </div>
              </div>
            </template>
          </n-popover>
        </div>
      </n-descriptions-item>
      <n-descriptions-item>
        <template #label>
          <div class="icon-title">
            <BMIcon class="header-icon"/>
            Bonus/Malus
          </div>
        </template>
        <div class="popover-container">
          <n-popover
            v-for="(value, key) in bonusMalus"
            :key="key"
            trigger="hover"
            placement="top"
          >
            <template #trigger>
              <n-tag
                :bordered="false"
                :round="true"
                :color="{ color: 'var(--onBackground)', textColor: 'var(--textColor)' }"
              >
              {{ key.toString().toUpperCase() }}: {{ value.value ?? '--' }}/20
              </n-tag>
            </template>
            <template #default>
              <p>{{value.gradeType.description}}</p>
              <div class="evaluations-table">
                <div class="evaluation-row" v-for="evaluation in value.evaluations" :key="evaluation.id">
                  <span class="evaluator-name">{{ evaluation.evaluator.firstName }} {{ evaluation.evaluator.lastName }}</span>
                  <span class="evaluator-status">
                    <n-icon :component="getIcon(evaluation.status)" :color="getColor(evaluation.status)" />
                  </span>
                </div>
              </div>
            </template>
          </n-popover>
        </div>
      </n-descriptions-item>
      <n-descriptions-item>
        <template #label>
          <div class="icon-title">
            <TeamOral class="header-icon"/>
            Note de Présentation
          </div>
        </template>
        <div class="popover-container">
          <n-popover
            trigger="hover"
            placement="top"
          >
            <template #trigger>
              <n-tag
                :bordered="false"
                :round="true"
                :color="{ color: 'var(--onBackground)', textColor: 'var(--textColor)' }"
              >
              SSPR: {{ presentationNotes.sspr.value ?? '--' }}/20
              </n-tag>
            </template>
            <template #default>
              <p>{{presentationNotes.sspr.gradeType.description}}</p>
              <div class="evaluations-table">
                <div class="evaluation-row" v-for="evaluation in presentationNotes.sspr.evaluations" :key="evaluation.id">
                  <span class="evaluator-name">{{ evaluation.evaluator.firstName }} {{ evaluation.evaluator.lastName }}</span>
                  <span class="evaluator-status">
                    <n-icon :component="getIcon(evaluation.status)" :color="getColor(evaluation.status)" />
                  </span>
                </div>
              </div>
            </template>
          </n-popover>

          <n-popover
            trigger="hover"
            placement="top"
          >
            <template #trigger>
              <n-tag
                :bordered="false"
                :round="true"
                :color="{ color: 'var(--onBackground)', textColor: 'var(--textColor)' }"
              >
              OTPR: {{ presentationNotes.otpr.value ?? '--' }}/20
              </n-tag>
            </template>
            <template #default>
              <p>{{presentationNotes.otpr.gradeType.description}}</p>
              <div class="evaluations-table">
                <div class="evaluation-row">
                  <span class="evaluator-name">Notes des autres équipes</span>
                  <span class="evaluator-status">
                    <n-icon :component="getIcon(presentationNotes.otpr.status)" :color="getColor(presentationNotes.otpr.status)" />
                  </span>
                </div>
              </div>
            </template>
          </n-popover>

          <n-popover
            trigger="hover"
            placement="top"
          >
            <template #trigger>
              <n-tag
                :bordered="false"
                :round="true"
                :color="{ color: 'var(--onBackground)', textColor: 'var(--textColor)' }"
              >
              TCPR: {{ presentationNotes.tcpr.value ?? '--' }}/20
              </n-tag>
            </template>
            <template #default>
              <p>{{presentationNotes.tcpr.gradeType.description}}</p>
              <div class="evaluations-table">
                <div class="evaluation-row" v-for="evaluation in presentationNotes.tcpr.evaluations" :key="evaluation.id">
                  <span class="evaluator-name">{{ evaluation.evaluator.firstName }} {{ evaluation.evaluator.lastName }}</span>
                  <span class="evaluator-status">
                    <n-icon :component="getIcon(evaluation.status)" :color="getColor(evaluation.status)" />
                  </span>
                </div>
              </div>
            </template>
          </n-popover>
        </div>
      </n-descriptions-item>
    </n-descriptions>
  </div>
</template>

<script lang="ts">
import { NPopover, NIcon, NTag } from 'naive-ui';
import CheckCircleFill from '@vicons/fluent/CheckmarkCircle12Filled';
import ErrorCircleFill from '@vicons/fluent/ErrorCircle12Filled';
import { defineComponent } from 'vue';
import type { GradeType } from '@/services/types';
import {
  AddSubtractCircle24Regular as BMIcon,
  ProjectionScreen24Regular as TeamOral,
} from '@vicons/fluent';
import PeopleOutline from '@vicons/ionicons5/PeopleOutline';

interface Details {
  [key: string]: {
    id: number,
    value: number,
    gradeType: GradeType,
    status: string,
    evaluations: Array<{
      id: number,
      evaluator: {
        firstName: string,
        lastName: string,
      },
      status: string,
    }>,
  };
}

export default defineComponent({
  components: {
    NPopover,
    NIcon,
    TeamOral,
    BMIcon,
    PeopleOutline,
    NTag,
  },
  props: {
    details: {
      type: Object as () => Details,
      required: true,
    },
  },
  computed: {
    initialNotes(): Details {
      // Extract the first 4 notes
      return Object.fromEntries(Object.entries(this.details).slice(0, 4)) as Details;
    },
    bonusMalus(): Details {
      // Extract the next 2 notes
      return Object.fromEntries(Object.entries(this.details).slice(4, 6)) as Details;
    },
    presentationNotes(): Details {
      // Extract the last 3 notes
      return Object.fromEntries(Object.entries(this.details).slice(6)) as Details;
    },
  },
  methods: {
    getIcon(status: string): typeof CheckCircleFill | typeof ErrorCircleFill {
      console.log('Status', status);
      return status === 'COMPLETED' ? CheckCircleFill : ErrorCircleFill;
    },
    getColor(status: string): string {
      if (status === 'COMPLETED') {
        return 'green';
      } else if (status === 'IN_PROGRESS') {
        return 'orange';
      } else {
        return 'red';
      }
    },
  },
});
</script>

<style scoped>
.popover-container {
  display: flex;
  justify-content: space-between;
}
.explications {
  display: flex;
  justify-content: space-around;
  margin-bottom: 10px;
}
.small-text {
  font-style: italic;
  color: grey;
  font-size: 0.875em;
}
.black-text{
  color: #000000;
}

.icon-title {
  display: flex;
  align-items: center;
  gap: 5px;
}
.header-icon {
  width: 30px;
  height: 30px;
}

.evaluations-table {
  display: flex;
  flex-direction: column;
}
.evaluation-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #ccc;
  padding: 4px 0;
}
.evaluator-name, .evaluator-status {
  display: flex;
  align-items: center;
}
.evaluator-name {
  flex: 1;
}
.evaluator-status {
  justify-content: center;
}
</style>
