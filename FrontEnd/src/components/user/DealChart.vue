<template>
    <div class="chart-container">
      <Line :data="chartData" :options="chartOptions"></Line>
    </div>
  </template>
  
  <script setup>
  import { ref, watch } from 'vue';
  import { Line } from 'vue-chartjs';
  import {
    Chart as ChartJS,
    Title,
    Tooltip,
    Legend,
    LineElement,
    PointElement,
    LinearScale,
    CategoryScale
  } from 'chart.js';
  
  ChartJS.register(Title, Tooltip, Legend, LineElement, PointElement, LinearScale, CategoryScale);
  
  const props = defineProps({
    dealList: {
      type: Array,
      required: true
    },
    title: {
      type: String,
      default: ''
    }
  });
  
  const chartData = ref({
    labels: [],
    datasets: []
  });
  
  const chartOptions = ref({
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top'
      },
      title: {
        display: true,
        text: props.title
      }
    },
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          callback: function (value) {
            return value + '억';
          }
        }
      }
    }
  });
  
  watch(
    () => props.dealList,
    (newVal) => {
      // 연도별 데이터 합산 및 평균 계산
      const yearData = newVal.reduce((acc, deal) => {
        const year = deal.dealDate.split('-')[0];
        const amount = parseFloat(deal.dealAmount.replace(/,/g, ''));
  
        if (!acc[year]) {
          acc[year] = { sum: 0, count: 0 };
        }
        acc[year].sum += amount;
        acc[year].count += 1;
  
        return acc;
      }, {});
  
      const labels = Object.keys(yearData).sort();
      const data = labels.map(year => (yearData[year].sum / yearData[year].count) / 10000); // 억 단위로 변경
      const maxDataValue = Math.max(...data); // 최대값 계산
      const adjustedMax = Math.floor(maxDataValue + 1); // 최대값에서 1억 추가하고 소수점 버림
  
      chartData.value = {
        labels,
        datasets: [
          {
            label: '평균 거래 금액',
            backgroundColor: 'rgba(75, 192, 192, 0.2)',
            borderColor: 'rgba(75, 192, 192, 1)',
            data
          }
        ]
      };
  
      chartOptions.value.scales.y.max = adjustedMax; // y축 최대값 설정
    },
    { immediate: true }
  );
  </script>
  
  <style scoped>
  .chart-container {
    position: relative;
    width: 100%;
    height: 200px; /* 차트의 높이를 조정하여 y축 크기 확장 */
  }
  </style>