import {localAxios} from "@/util/http-commons.js";

const local = localAxios();

function addFavorite(estateObject, userId, success, fail){
    local.defaults.headers["Authorization"] = sessionStorage.getItem("accessToken");
    delete local.defaults.headers["regenerateToken"];
    console.log("object = ",estateObject, " userId = ",userId);
    local.post("/favorite/"+userId, estateObject).then(success).catch(fail);
}
function findAllUserFavorite(userId, success, fail){
    local.defaults.headers["Authorization"] = sessionStorage.getItem("accessToken");
    delete local.defaults.headers["regenerateToken"];
    console.log("before send param = ",userId);
    local.get("/favorite/"+userId).then(success).catch(fail);
}
function APIdeleteFavorite(userId, itemNo, success, fail){
    local.defaults.headers["Authorization"] = sessionStorage.getItem("accessToken");
    delete local.defaults.headers["regenerateToken"];
    console.log("before send userId = ",userId, " // itemNo = ",itemNo);
    local.delete(`/favorite/${userId}?favoriteNo=${itemNo}`).then(success).catch(fail);
}
export {addFavorite, findAllUserFavorite,APIdeleteFavorite};