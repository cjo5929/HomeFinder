<template>
  <tr>
    <td>{{ deal.dealDate }}</td>
    <td>{{ formatPrice(deal.dealAmount) }}</td>
    <td>{{ deal.exclusiveArea }}</td>
    <td>{{ deal.floor }}</td>
  </tr>
</template>

<script setup>

defineProps({
  deal: {
    type: Object,
    required: true
  }
});

// 가격 변환
const formatPrice = (priceString) => {
  const price = parseInt(priceString.replace(/,/g, '')) * 10000; // 만원 단위를 원 단위로 변환
  if (price >= 100000000) {
    const eok = Math.floor(price / 100000000);
    const remainder = price % 100000000;
    const formattedRemainder = remainder > 0 ? Math.floor(remainder / 10000000) : '';
    return `${eok}${formattedRemainder ? '.' + formattedRemainder : ''}억원`;
  } else if (price >= 10000) {
    return `${(price / 10000).toLocaleString()}만원`;
  } else {
    return `${price.toLocaleString()}원`;
  }
};
</script>

<style scoped>

</style>