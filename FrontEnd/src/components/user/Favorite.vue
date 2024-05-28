    <template>
        <div class="container-fulid mt-5">
          <FavoriteItem :favoriteList="favoriteList" @deleteFavorite="deleteFavorite"></FavoriteItem>
        </div>
    </template>
  
  <script setup>
  import { ref, onMounted} from 'vue';
  import {findAllUserFavorite} from "@/api/userFavorite.js";
  import {useUserStore} from "@/stores/userStore.js";
  import FavoriteItem from '@/components/user/FavoriteItem.vue';
  import {storeToRefs} from "pinia";
  
  const favoriteList = ref([]);
  onMounted(()=>{
    const store = useUserStore();   
    const {userInfo} = storeToRefs(store);
    console.log(userInfo);
    const userId = userInfo.value.userId;
    console.log("userId", userId);
    findAllUserFavorite(userId, (response)=>{
        console.log("reponse= ",response);
        favoriteList.value = response.data.list;
    }, (error)=>{
        console.log(error);
    })
  })
  const deleteFavorite = ()=>{
    const store = useUserStore();
    const {userInfo} = storeToRefs(store);
    const userId = userInfo.value.userId;
    findAllUserFavorite(userId, (response)=>{
        console.log("reponse= ",response);
        favoriteList.value = response.data.list;
    }, (error)=>{
        console.log(error);
    })
  }
  
  </script>
  
  <style scoped>
  .container {
    background-color: #f8f9fa;
    padding: 20px;
    border-radius: 8px;
  }
  
  .card {
    border: 1px solid #ddd;
  }
  
  .card-img-top {
    width: 100%;
    height: 180px;
    object-fit: cover;
  }
  
  .card-body {
    text-align: left;
  }
  
  .text-center {
    color: #333;
  }
  </style>