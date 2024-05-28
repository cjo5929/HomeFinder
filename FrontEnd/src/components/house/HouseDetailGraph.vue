<template>
    <div class="deal-chart">
      <line-chart :data="chartData" :options="chartOptions"></line-chart>
    </div>
</template>
  
  <script setup>
  import { ref, computed } from 'vue';
  import { Line as LineChart } from 'vue-chartjs';
  import { Chart as ChartJS, Title, Tooltip, Legend, LineElement, PointElement, LinearScale, CategoryScale } from 'chart.js';


const props = defineProps({
  dealList: {
    type: Array,
    required: true
  }
});

  // 그래프
ChartJS.register(Title, Tooltip, Legend, LineElement, PointElement, LinearScale, CategoryScale);

const chartData = computed(() => {
  const yearAmountMap = {};

  props.dealList.forEach(deal => {
    const year = deal.dealDate.substring(0, 4);
    const amount = parseInt(deal.dealAmount.replace(/,/g, ''));

    if (yearAmountMap[year]) {
      yearAmountMap[year].sum += amount;
      yearAmountMap[year].count++;
    } else {
      yearAmountMap[year] = {
        sum: amount,
        count: 1,
      };
    }
  });

  const years = Object.keys(yearAmountMap).sort();
  const dealAmounts = years.map(year => yearAmountMap[year].sum / yearAmountMap[year].count / 10000);

  return {
    labels: years,
    datasets: [
      {
        label: '평균 거래 금액',
        data: dealAmounts,
        borderColor: 'rgba(75, 192, 192, 1)',
        backgroundColor: 'rgba(75, 192, 192, 0.2)',
        pointBackgroundColor: 'rgba(75, 192, 192, 1)',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: 'rgba(75, 192, 192, 1)',
        fill: true,
        tension: 0.4,
      },
    ],
  };
});
const chartOptions = ref({
  responsive: true,
  maintainAspectRatio: false,
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        callback: value => `${value}억`,
        font: {
          size: 14,
          family: 'Noto Sans KR',
        },
        color: '#888',
      },
      grid: {
        color: 'rgba(0, 0, 0, 0.05)',
      },
    },
    x: {
      ticks: {
        font: {
          size: 14,
          family: 'Noto Sans KR',
        },
        color: '#888',
      },
      grid: {
        display: false,
      },
    },
  },
  plugins: {
    legend: {
      labels: {
        font: {
          size: 16,
          family: 'Noto Sans KR',
        },
        color: '#333',
      },
    },
    title: {
      display: true,
      text: '매물 연도별 거래 금액',
      font: {
        size: 18,
        weight: 'bold',
        family: 'Noto Sans KR',
      },
      color: '#333',
      padding: {
        bottom: 20,
      },
    },
    tooltip: {
      titleFont: {
        size: 14,
        family: 'Noto Sans KR',
      },
      bodyFont: {
        size: 14,
        family: 'Noto Sans KR',
      },
      callbacks: {
        label: context => {
          let label = context.dataset.label || '';
          if (label) {
            label += ': ';
          }
          if (context.parsed.y !== null) {
            label += `${context.parsed.y}억원`;
          }
          return label;
        },
      },
    },
  },
  animation: {
    duration: 1000,
    easing: 'easeOutQuart',
  },
});
// 그래프 끝

  </script>

  <style scoped>
</style>