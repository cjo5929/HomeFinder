import { localAxios } from "@/util/http-commons.js";

const local = localAxios();

async function uploadItem(param, success, fail){
    local.defaults.headers["Authorization"] = sessionStorage.getItem("accessToken");
    delete local.defaults.headers["regenerateToken"];
    console.log("param = ",param)
    await local.post("/estate/item", param, {
        headers : {
            "Content-Type" : "multipart/form-data"
        }
    }).then(success).catch(fail);
}

async function findAllItem(param, success, fail){
    local.defaults.headers["Authorization"] = sessionStorage.getItem("accessToken");
    delete local.defaults.headers["regenerateToken"];
    console.log("param = "+param);
    await local.get("/estate/item/"+param).then(success).catch(fail);
}

async function deleteItem(param, success, fail){
    local.defaults.headers["Authorization"] = sessionStorage.getItem("accessToken");
    delete local.defaults.headers["regenerateToken"];
    console.log("param =",param);
    await local.delete(`/estate/item`, { params: { item_no: param } }).then(success).catch(fail);
}

async function updateItem(param, success, fail) {
    local.defaults.headers["Authorization"] = sessionStorage.getItem("accessToken");
    delete local.defaults.headers["regenerateToken"];
    console.log("before send update", param);
    console.log(param.get("item_no"));
    console.log(param.get("file_no"));
    await local.put("/estate/item/" + param.get("item_no"), param, {
        headers: {
            "Content-Type": "multipart/form-data"
        }
    }).then(success).catch(fail);
}

export {uploadItem, findAllItem, deleteItem, updateItem};