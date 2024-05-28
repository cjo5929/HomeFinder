<template>
  <div class="container mt-5 mb-4">
    <div class="row">
      <div class="col-md-12">
        <div class="row mb-3 justify-content-center">
          <div class="col-auto">
            <button class="btn btn-custom" @click="prevMonth">
              <font-awesome-icon :icon="['fas', 'arrow-left']" />
            </button>
          </div>
          <div class="col text-center align-self-center">
            <h2>{{ currentMonthYear }}</h2>
          </div>
          <div class="col-auto">
            <button class="btn btn-custom" @click="nextMonth">
              <font-awesome-icon :icon="['fas', 'arrow-right']" />
            </button>
          </div>
        </div>
        <div class="row">
          <div class="col day-header" v-for="day in daysOfWeek" :key="day">
            <div class="text-center font-weight-bold">{{ day }}</div>
          </div>
        </div>
        <div class="row week" v-for="week in calendar" :key="week[0].date">
          <div class="col day-cell" v-for="day in week" :key="day.date">
            <div :class="{ 'text-muted': !day.currentMonth, 'text-bold': day.currentMonth }">
              {{ day.date.getDate() }}
            </div>
            <div
              v-for="event in day.events"
              :key="event.title"
              class="badge badge-primary my-1 event-title"
              @click="goToEventDetail(event)"
            >
              {{ event.title }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { getApplyHomeDataByMonth } from "@/api/map.js";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap";

const router = useRouter();
const currentMonth = ref(new Date().getMonth());
const currentYear = ref(new Date().getFullYear());
const calendar = ref([]);
const daysOfWeek = ["일", "월", "화", "수", "목", "금", "토"];

const isValidDate = (date) => {
  return !isNaN(Date.parse(date));
};

const fetchEvents = (yearMonth) => {
  return new Promise((resolve, reject) => {
    getApplyHomeDataByMonth(
      yearMonth,
      ({ data }) => {
        if (data) {
          const events = data
            .map((event) => {
              if (isValidDate(event.rcept_BGNDE)) {
                return {
                  no: event.house_MANAGE_NO,
                  title: event.house_NM,
                  start: new Date(event.rcept_BGNDE),
                  end: new Date(event.rcept_BGNDE),
                  address: event.hssply_ADRES,
                  url: event.pblanc_URL,
                  suply: event.tot_SUPLY_HSHLDCO,
                  area: event.subscrpt_AREA_CODE_NM,
                  presnatn: event.przwner_PRESNATN_DE,
                  entrps: event.cnstrct_ENTRPS_NM,
                };
              } else {
                console.warn(`Invalid date: ${event.rcept_BGNDE}`);
                return null;
              }
            })
            .filter((event) => event !== null);
          resolve(events);
        } else {
          resolve([]);
        }
      },
      (error) => {
        console.error(error);
        reject(error);
      }
    );
  });
};

const getCalendarDays = async () => {
  const yearMonth = `${currentYear.value}-${String(currentMonth.value + 1).padStart(2, "0")}`;
  const events = await fetchEvents(yearMonth);

  const startOfMonth = new Date(currentYear.value, currentMonth.value, 1);
  const endOfMonth = new Date(currentYear.value, currentMonth.value + 1, 0);

  const startDate = new Date(startOfMonth);
  startDate.setDate(startDate.getDate() - startDate.getDay());

  const endDate = new Date(endOfMonth);
  endDate.setDate(endDate.getDate() + (6 - endDate.getDay()));

  const calendarData = [];
  let week = [];
  for (let date = new Date(startDate); date <= endDate; date.setDate(date.getDate() + 1)) {
    const dayEvents = events.filter((event) => {
      return date.toISOString().split("T")[0] === event.start.toISOString().split("T")[0];
    });

    week.push({
      date: new Date(date),
      currentMonth: date.getMonth() === currentMonth.value,
      events: dayEvents,
    });

    if (week.length === 7) {
      calendarData.push(week);
      week = [];
    }
  }

  return calendarData;
};

const updateCalendar = async () => {
  calendar.value = await getCalendarDays();
};

const prevMonth = () => {
  if (currentMonth.value === 0) {
    currentMonth.value = 11;
    currentYear.value -= 1;
  } else {
    currentMonth.value -= 1;
  }
  updateCalendar();
};

const nextMonth = () => {
  if (currentMonth.value === 11) {
    currentMonth.value = 0;
    currentYear.value += 1;
  } else {
    currentMonth.value += 1;
  }
  updateCalendar();
};

const goToEventDetail = (event) => {
  router.push({
    name: "EventDetail",
    query: {
      no: event.no,
      title: event.title,
      start: event.start,
      end: event.end,
      address: event.address,
      url: event.url,
      suply: event.suply,
      area: event.area,
      presnatn: event.presnatn,
      entrps: event.entrps,
    },
  });
};

const currentMonthYear = computed(() => {
  return new Date(currentYear.value, currentMonth.value).toLocaleString("default", {
    month: "long",
    year: "numeric",
  });
});

onMounted(updateCalendar);
</script>

<style scoped>
.container {
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 10px;
}

.day-header {
  border-bottom: 2px solid #dee2e6;
}

.week {
  border-bottom: 2px solid #dee2e6;
}

.day-cell {
  border-right: 2px solid #dee2e6;
  padding: 10px;
  min-height: 100px;
}

.day-cell:last-child {
  border-right: none;
}

.day-cell .text-bold {
  font-weight: bold;
}

.day-cell .badge-primary {
  background-color: #5ab2ff;
  cursor: pointer;
}

.day-cell .badge-primary:hover {
  background-color: #0056b3;
}

h2 {
  color: #5ab2ff;
}

.text-muted {
  color: #6c757d !important;
}

.btn-custom {
  background-color: #5ab2ff;
  border-color: #5ab2ff;
  color: white;
}

.btn-custom:hover {
  background-color: #0056b3;
  border-color: #0056b3;
}
</style>
