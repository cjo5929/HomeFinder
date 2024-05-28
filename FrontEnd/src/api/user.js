import {localAxios} from "@/util/http-commons.js";

const local = localAxios();

async function userConfirm(param, success, fail){
    await local.post(`/member/login`, null, {
        params : {
            userId : param.userId,
            userPassword : param.userPassword
        }
    }).then(success).catch(fail);
}

async function registUser(param, success, fail){
    console.log("axios = ",param);
    await local.post("/member", param).then(success).catch(fail);
}

async function registToken(param, success, fail){
    local.defaults.headers["Authorization"] = sessionStorage.getItem("accessToken");
    delete local.defaults.headers["regenerateToken"];
    await local.post(`/member/token/`+param.userId, null, {
        params :{
            refreshToken : param.refreshToken,
            userId : param.userId
        }
    }).then(success).catch(fail);
}

async function findById(param, success, fail){
    local.defaults.headers["Authorization"] = sessionStorage.getItem("accessToken");
    delete local.defaults.headers["regenerateToken"];
    console.log("last param = ",param);
    await local.get('/member/'+param).then(success).catch(fail);
}

async function regenerateToken(param, success, fail) {
    local.defaults.headers["regenerateToken"] = sessionStorage.getItem("refreshToken");
    try {
        const response = await local.post("/member/refresh", param);
        console.log("axios의 =,",response);
        success(response);
    } catch (error) {
        fail(error);
    }
}

async function logout(param,success, fail){
    local.defaults.headers["Authorization"] = sessionStorage.getItem("accessToken");
    delete local.defaults.headers["regenerateToken"];
    await local.get('/member/logout/'+param).then(success).catch(fail);
}

async function changeImg(param,success,fail){
    local.defaults.headers["Authorization"] = sessionStorage.getItem("accessToken");
    delete local.defaults.headers["regenerateToken"];
    await local.post("/member/image/"+param.get("userId"), param, {
        headers : {
            "Content-Type" : "multipart/form-data"
        }
    }).then(success).catch(fail);
}

async function deleteImg(param, success, fail){
    local.defaults.headers["Authorization"] = sessionStorage.getItem("accessToken");
    delete local.defaults.headers["regenerateToken"];
    await local.delete("/member/image/"+param).then(success).catch(fail);
}

async function getImg(param,accessToken,success,fail){
    local.defaults.headers["Authorization"] = accessToken;
    delete local.defaults.headers["regenerateToken"];
    await local.get("/member/image/"+param).then(success).catch(fail);
}

async function updateUser(param, success, fail){
    local.defaults.headers["Authroization"] = sessionStorage.getItem("accessToken");
    delete local.defaults.headers["regenerateToken"];
    console.log("api user.js updateObject = ",param);
    await local.put("/member/"+param.userId, param).then(success).catch(fail);
}

async function deleteUser(param, success, fail){
    local.defaults.headers["Authorization"] = sessionStorage.getItem("accessToken");
    delete local.defaults.headers["regenerateToken"];
    await local.delete("/member/"+param.userId, {data:{userId:param.userId, userPassword:param.userPassword}}).then(success).catch(fail);
}

async function forceLogout(param, success, fail){
    await local.get("/member/force/logout/"+param).then(success).catch(fail);
}

async function duplicatedCheck(userId, success, fail){
    await local.get(`/member/check/${userId}`).then(success).catch(fail);
}
async function mailValidation(email, success, fail){
    await local.get(`/member/mail/${email}`).then(success).catch(fail);
}

async function resetPassword(sendObject, success, fail){
    await local.post(`/member/reset/${sendObject.userId}`, {userPassword : sendObject.userPassword}).then(success).catch(fail);
}

export{userConfirm, findById, regenerateToken, logout, registToken, changeImg,deleteImg, getImg, updateUser, deleteUser, forceLogout, registUser, duplicatedCheck, mailValidation, resetPassword};