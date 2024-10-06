<template>
  <Loading v-if="loading" />
  <div v-else class="full-table">
    <h2 class="title">Notifications</h2>
    <div class="tools">
      <n-tooltip trigger="hover" placement="right" v-if="checkedRowKeys.length">
        <template #trigger>
          <n-icon
            :component="TrashOutline"
            :size="20"
            class="suppSelect"
            @Click="deleteSelectedRows" />
        </template>
        Supprimer {{ checkedRowKeys.length }} ligne{{
          checkedRowKeys.length < 2 ? '' : 's'
        }}
      </n-tooltip>
    </div>
    <n-space vertical :size="12">
      <n-data-table
        ref="dataTableInst"
        :columns="createColumns()"
        :data="notifs"
        :pagination="pagination"
        :row-key="rowKey"
        @update:checked-row-keys="handleCheck" />
    </n-space>
  </div>
</template>

<script lang="ts">
import { h, defineComponent, onMounted, ref } from 'vue';
import { notificationService } from '@/services/notification.service';
import { NButton, type DataTableColumns } from 'naive-ui';
import { useUserStore } from '@/stores/useUserStore';
import type { Notification } from '@/services/types';
import { toast } from 'vue3-toastify';
import Loading from '@/components/Loading.vue';
import { TrashOutline } from '@vicons/ionicons5';
import IconButton from '@/components/IconButton.vue';

const notifs = ref<Notification[]>([]);
const loading = ref(false);
const loadingDelete = ref(false);

const toggleStatus = async (row: Notification, index: number) => {
  const notif = await notificationService.toggleStatus(row.id ?? 0);
  if (notif) {
    const newStatus = row.status === 'READ' ? 'UNREAD' : 'READ';
    notifs.value[index].status = newStatus;
  } else {
    toast.error('Erreur lors du changement de status');
  }
};

const createColumns = (): DataTableColumns<Notification> => [
  {
    type: 'selection',
  },
  {
    title: 'Date',
    key: 'date',
    width: '100px',
    sorter: (row1, row2) =>
      new Date(row2.date).getTime() - new Date(row1.date).getTime(),
    render: (row) => new Date(row.date).toLocaleDateString(),
  },
  {
    title: 'Type',
    key: 'type',
    width: '130px',
    sorter: (row1, row2) => row1.type.localeCompare(row2.type),
  },
  {
    title: 'Emetteur',
    key: 'emitter.email',
    width: '250px',
  },
  {
    title: 'Description',
    key: 'description',
  },
  {
    title: 'Statut',
    defaultSortOrder: false,
    key: 'status',
    width: '100px',
    filterOptions: [
      {
        label: 'Non lu',
        value: 'Non lu',
      },
      {
        label: 'Lu',
        value: 'Lu',
      },
    ],
    filter(value: string, row: Notification) {
      return row.status.indexOf(value) !== -1;
    },
    render(row, index) {
      return h(
        NButton,
        {
          strong: true,
          round: true,
          type: row.status === 'READ' ? 'terciary' : 'primary',
          size: 'small',
          onClick: () => toggleStatus(row, index),
        },
        { default: () => (row.status === 'READ' ? 'Lu' : 'Non lu') }
      );
    },
  },
];
export default defineComponent({
  setup() {
    const checkedRowKeysRef = ref([]);
    const dataTableInstRef = ref(null); // Define data as a ref
    const userId = useUserStore().getUser()?.id;

    onMounted(async () => {
      await getNotifications(); // Update data.value with the result of getNotifications
    });

    const getNotifications = async () => {
      loading.value = true;
      const notifsRes = await notificationService.getAllByUserId(userId ?? 0);

      if (notifsRes) {
        notifsRes.forEach((item) => {
          item.date = new Date(item.date);
        });
        notifs.value = notifsRes.sort(
          (a, b) => new Date(b.date) - new Date(a.date)
        );
      }
      loading.value = false;
    };

    // const deleteSelectedRows = async () => {
    //   const promises = checkedRowKeysRef.value.map(async (key) => {
    //     const row = notifs.value.find((item) => item.id === key);
    //     if (row) {
    //       console.log(row);
    //       await notificationService.deleteById(row.id);
    //     }
    //   });
    //   await Promise.all(promises);

    //   notifs.value = notifs.value.filter(
    //     (item) => !checkedRowKeysRef.value.includes(item.id)
    //   );
    //   checkedRowKeysRef.value = [];
    // };

    const deleteSelectedRows = async () => {
      const deletePromise = new Promise(async (resolve, reject) => {
        try {
          const promises = checkedRowKeysRef.value.map(async (key) => {
            const row = notifs.value.find((item) => item.id === key);
            if (row) {
              console.log(row);
              await notificationService.deleteById(row.id);
            }
          });

          await Promise.all(promises);

          notifs.value = notifs.value.filter(
            (item) => !checkedRowKeysRef.value.includes(item.id)
          );
          checkedRowKeysRef.value = [];

          resolve();
        } catch (error) {
          reject(error);
        }
      });

      toast.promise(deletePromise, {
        pending: 'Suppression des éléments en cours...',
        success: 'Éléments supprimés avec succès!',
        error: {
          render({ data }) {
            return `Erreur lors de la suppression des éléments: ${data.message}`;
          },
        },
      });
    };

    return {
      loading,
      TrashOutline,
      notifs, // Now data is defined in the scope of the setup function
      createColumns,
      dataTableInst: dataTableInstRef,
      pagination: ref({ pageSize: 10 }),
      checkedRowKeys: checkedRowKeysRef,
      rowKey: (row: Notification) => row.id,
      handleCheck(rowKeys) {
        checkedRowKeysRef.value = rowKeys;
      },
      deleteSelectedRows,
    };
  },
});
</script>

<style scoped>
.full-table {
  margin: 50px 50px;
}
.title {
  font-size: 1.4rem;
  color: var(textColor);
  text-align: center;
  margin-top: 100px;
  margin-bottom: 20px;
}

.tools {
  display: flex;
  width: 100%;
  justify-content: flex-start;
  padding: 0px 11px;
  height: 30px;
}
@keyframes fadeinTrash {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
.tools .suppSelect {
  cursor: pointer;
  color: red;
  animation: ease-in-out fadeinTrash 0.2s;
}
</style>
