import { localAxios } from "@/util/http-commons.js";

const local = localAxios();

async function addChattingRoom(senderAndReceiver, success, fail){
    local.defaults.headers["Authorization"] = sessionStorage.getItem("accessToken");
    delete local.defaults.headers["regenerateToken"];
    await local.post("/chat/createRoom", senderAndReceiver).then(success).catch(fail);
}
async function findRoomIdBySenderAndReceiver(dataObject, success, fail){
    local.defaults.headers["Authorization"] = sessionStorage.getItem("accessToken");
    delete local.defaults.headers["regenerateToken"];
    console.log("before send server data = ",dataObject);
    await local.get(`/chat/room`, { params: dataObject }).then(success).catch(fail);
}

async function findAllPrevChatMessage(roomId, success, fail){
    local.defaults.headers["Authroization"] = sessionStorage.getItem("accessToken");
    delete local.defaults.headers["regenerateToken"];
    await local.get(`/chat/${roomId}`).then(success).catch(fail);
}

async function findAllRoomByUserId(userId, success, fail){
    console.log("api userId = ",userId);
    console.log("api token = ",sessionStorage.getItem("accessToken"));
    local.defaults.headers["Authorization"] = sessionStorage.getItem("accessToken");
    delete local.defaults.headers["regenerateToken"];
    await local.get(`/chat/rooms/${userId}`).then(success).catch(fail);
}

async function deleteRoom(userId, roomDto, success, fail){
    local.defaults.headers["Authorization"] = sessionStorage.getItem("accessToken");
    delete local.defaults.headers["regenerateToken"];
    await local.delete(`/chat/room/${userId}`,  { data: roomDto }).then(success).catch(fail);
}

export {addChattingRoom, findRoomIdBySenderAndReceiver, findAllPrevChatMessage, findAllRoomByUserId, deleteRoom};