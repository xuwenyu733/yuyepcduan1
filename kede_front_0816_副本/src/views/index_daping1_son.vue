<template>
  <div class="card model-card flex-1">
    <h2 class="card-title">
      <div class="icon-box emerald-box">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 6c.6.5 1.2 1 2.5 1C7 7 7 5 9.5 5c2.6 0 2.4 2 5 2 2.5 0 2.5-2 5-2 1.3 0 1.9.5 2.5 1"></path><path d="M2 12c.6.5 1.2 1 2.5 1 2.5 0 2.5-2 5-2 2.6 0 2.4 2 5 2 2.5 0 2.5-2 5-2 1.3 0 1.9.5 2.5 1"></path><path d="M2 18c.6.5 1.2 1 2.5 1 2.5 0 2.5-2 5-2 2.6 0 2.4 2 5 2 2.5 0 2.5-2 5-2 1.3 0 1.9.5 2.5 1"></path></svg>
      </div>
      厂区3D模型
    </h2>
    <div class="model-view">
      <div class="door">厂区大门</div>
      <div class="road"></div>
      <div class="road-horizontal r-left"></div>
      <div class="road-horizontal r-right"></div>

      <div v-for="(workshop, index) in workshopList" :key="workshop.workshopId"
        :class="['workshop', getWorkshopStyle(index).workshopClass]"
        @mouseenter="prefetchWorkshopCnc"
        @click="toPage(`/keyshow/workshopCNC?workshop=${workshop.workshopId}&workshopName=${encodeURIComponent(workshop.workshopName || '')}&system=1`)">
        <div class="w-head">
          <p class="w-title">{{ workshop.workshopName }}</p>
          <p class="w-sub">运行中 • {{ workshop.pondCount }}个养殖池</p>
        </div>
        <div class="w-grid">
          <div v-for="i in Math.min(workshop.pondCount, 8)" :key="workshop.workshopId + '-' + i"
            :class="['pool', getWorkshopStyle(index).poolClass]">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2.69l5.66 5.66a8 8 0 1 1-11.31 0z"></path></svg>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as ZkWorkshopApi from '@/api/fish/zkWorkshop';

export default {
  name: 'IndexDaping1Son',
  data() {
    return {
      workshopList: [],
    };
  },
  mounted() {
    this.getWorkshopList();
  },
  methods: {
    prefetchWorkshopCnc() {
      if (this._cncPrefetched) return;
      this._cncPrefetched = true;
      import(/* webpackChunkName: "index-daping4" */ '@/views/index_daping4.vue').catch(() => {});
      import(/* webpackChunkName: "echarts" */ 'echarts').catch(() => {});
    },
    toPage(path) {
      this.prefetchWorkshopCnc();
      this.$router.push({ path }).catch(() => {});
    },
    async getWorkshopList() {
      try {
        const res = await ZkWorkshopApi.getZkWorkshopDashboardList();
        this.workshopList = res.data;
      } catch (error) {
        console.error('获取车间列表失败:', error);
      }
    },
    getWorkshopStyle(index) {
      const schemes = [
        { workshopClass: 'w-1', poolClass: 'blue-pool' },
        { workshopClass: 'w-2', poolClass: 'emerald-pool' },
        { workshopClass: 'w-3', poolClass: 'purple-pool' },
      ];
      return schemes[index % schemes.length];
    },
  },
};
</script>

<style lang="scss" scoped>
.card {
  background: linear-gradient(to bottom right, rgba(30, 41, 59, 0.6), rgba(15, 23, 42, 0.6));
  backdrop-filter: blur(4px);
  border-radius: 0.75rem;
  border: 1px solid rgba(59, 130, 246, 0.2);
  padding: 1.25rem;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  color: white;
}

.flex-1 {
  flex: 1;
  min-height: 0;
}

.model-card {
  .card-title {
    font-size: 1.5rem;
    font-weight: 700;
    color: white;
    margin-bottom: 1rem;
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .icon-box {
    width: 2rem;
    height: 2rem;
    border-radius: 0.5rem;
    display: flex;
    align-items: center;
    justify-content: center;

    svg {
      width: 1.25rem;
      height: 1.25rem;
      color: white;
    }

    &.emerald-box {
      background: linear-gradient(to bottom right, #10b981, #2dd4bf);
    }
  }

  .model-view {
    position: relative;
    height: 100%;
    min-height: 20rem;
    background: linear-gradient(to bottom right, rgba(15, 23, 42, 0.8), rgba(23, 37, 84, 0.8));
    border-radius: 0.5rem;
    border: 1px solid rgba(96, 165, 250, 0.3);
    overflow-y: auto;
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    align-content: flex-start;
    gap: 2rem;
    padding: 6rem 2rem 2rem;

    .door {
      position: absolute;
      top: 0;
      left: 50%;
      transform: translateX(-50%);
      width: 8rem;
      height: 4rem;
      background: linear-gradient(to bottom, #334155, #475569);
      border: 2px solid #64748b;
      border-bottom: none;
      border-radius: 0.5rem 0.5rem 0 0;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1rem;
      font-weight: 700;
      color: white;
      box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
      z-index: 20;
    }

    .road {
      position: absolute;
      top: 4rem;
      left: 50%;
      transform: translateX(-50%);
      width: 5rem;
      height: 100%;
      background: rgba(51, 65, 85, 0.3);
      border-left: 2px dashed rgba(234, 179, 8, 0.3);
      border-right: 2px dashed rgba(234, 179, 8, 0.3);
      z-index: 1;
    }

    .road-horizontal {
      display: none;
    }

    .workshop {
      position: relative;
      width: 16rem;
      height: 12rem;
      border-radius: 0.75rem;
      border: 2px solid;
      overflow: hidden;
      cursor: pointer;
      transition: all 0.3s;
      z-index: 10;

      &:hover {
        transform: scale(1.05);
        z-index: 15;
      }

      &.w-1 {
        background: linear-gradient(to bottom right, rgba(37, 99, 235, 0.8), rgba(6, 182, 212, 0.8));
        border-color: #60a5fa;
        box-shadow: 0 15px 30px -10px rgba(59, 130, 246, 0.5);
      }

      &.w-2 {
        background: linear-gradient(to bottom right, rgba(5, 150, 105, 0.8), rgba(20, 184, 166, 0.8));
        border-color: #34d399;
        box-shadow: 0 15px 30px -10px rgba(16, 185, 129, 0.5);
      }

      &.w-3 {
        background: linear-gradient(to bottom right, rgba(147, 51, 234, 0.8), rgba(236, 72, 153, 0.8));
        border-color: #c084fc;
        box-shadow: 0 15px 30px -10px rgba(168, 85, 247, 0.5);
      }

      .w-head {
        padding: 0.75rem;
        border-bottom: 1px solid rgba(255, 255, 255, 0.3);
        background: rgba(0, 0, 0, 0.2);

        .w-title {
          color: white;
          font-weight: 700;
          margin-bottom: 0.125rem;
        }

        .w-sub {
          color: rgba(255, 255, 255, 0.8);
          font-size: 1rem;
        }
      }

      .w-grid {
        padding: 0.75rem;
        display: grid;
        grid-template-columns: repeat(4, 1fr);
        gap: 0.5rem;

        .pool {
          aspect-ratio: 1 / 1;
          border-radius: 0.25rem;
          display: flex;
          align-items: center;
          justify-content: center;
          border: 1px solid rgba(255, 255, 255, 0.3);

          svg {
            width: 1rem;
            height: 1rem;
          }

          &.blue-pool {
            background: rgba(96, 165, 250, 0.3);
            svg { color: #bfdbfe; }
          }

          &.emerald-pool {
            background: rgba(52, 211, 153, 0.3);
            svg { color: #a7f3d0; }
          }

          &.purple-pool {
            background: rgba(192, 132, 252, 0.3);
            svg { color: #e9d5ff; }
          }
        }
      }
    }
  }
}

::-webkit-scrollbar {
  width: 0;
  background: transparent;
}
</style>
