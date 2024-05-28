import {ref} from "vue";
import {defineStore} from "pinia";
import {uploadItem, findAllItem, deleteItem} from "@/api/estate.js";
import { useRouter } from "vue-router";
export const useEstateStore = defineStore("estate", ()=>{
    
    const estateItem = ref([]);
    const router = useRouter();
    
    const storeUploadItem = async (item)=>{
        await uploadItem(item, (response)=>{
            alert("물품 등록에 성공했습니다.");
            router.push({name:"management"});
        },(error)=>{

        });
    }

    const storeFindAllItem = async (userId) =>{
        await findAllItem(userId, (response)=>{
            estateItem.value = response.data.list;
            
        }, (error)=>{

        });
    }

    const storeDeleteItem = async (sendDto) =>{

        await deleteItem(sendDto.item_no, async(response)=>{
            alert("물품 삭제에 성공했습니다.");
            await findAllItem(sendDto.userId, (response)=>{
                console.log("다시 받아온 데이터 = ,",response.data);
                estateItem.value = response.data.list;
            })
        },(error)=>{

        })
    }

    return {storeUploadItem, storeFindAllItem, estateItem, storeDeleteItem};
},{
    persist : {
        path : ['isLogin', 'userInfo']
      }
})