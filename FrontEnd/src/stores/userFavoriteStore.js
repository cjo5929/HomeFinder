import {ref} from "vue";
import {defineStore} from "pinia";
import {useRouter} from "vue-router";
import { HttpStatusCode } from "@/util/http-status.js";

export const useUserFavoriteStore = defineStore("userFavorite", ()=>{
    

    return {};
},{
    persist : {
        path : ['isLogin', 'userInfo']
      }
})