package com.ssafy.happyhouse.user.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.happyhouse.user.model.dto.UserDto;
import com.ssafy.happyhouse.user.model.dto.UserImageDto;
import com.ssafy.happyhouse.user.model.service.UserService;
import com.ssafy.happyhouse.util.JWTUtil;
import com.ssafy.happyhouse.util.MailSender;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Slf4j
@CrossOrigin(origins = { "*" }, methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.POST} , maxAge = 6000)
public class UserController {
	private final UserService service;
	private final JWTUtil jwtUtil;
	private final ResourceLoader resourceLoader;
	private final MailSender mailSender;

	@GetMapping("/dummy")
	public ResponseEntity<?> tokenTest(String id){
		System.out.println("call dummy");
		return null;
	}
	
	@PostMapping
	// 회원가입 메서드
	// 동작 순서
	// 1. 사용자의 입력 아이디가 이미 DB에 존재하는지 검증한다.
	// 2. 중복이 되지 않는다면 DB에 저장한다.
	// 3. 이후 HttpStatus를 반환한다.
	public ResponseEntity<?> regist(@RequestBody UserDto dto){
		log.debug("client to server data : {}", dto);
		int result = service.regist(dto);
		Map<String, Object> user = null;
		if(result == 1 ) {
			user = new HashMap<>();
			user.put("loginUser", dto);
			return new ResponseEntity<Map<String,Object>>(user, HttpStatus.OK);
		}
		else {
			return new ResponseEntity<Map<String,Object>>(user, HttpStatus.BAD_REQUEST);
		}
	}
	
	/**
	 * 
	 * @param userEmail
	 * @return
	 * 회원가입, 비밀번호 찾기에 사용되는 이메일 인증 메서드
	 * 이메일로 인증번호를 발송하는 기능을 담당
	 */
	@GetMapping("/mail/{userEmail}")
	public ResponseEntity<?> mailCheck(@PathVariable("userEmail") String userEmail){
		String authNumber = mailSender.joinEmail(userEmail);
		Map<String, Object>map = new HashMap<>();
		map.put("authNumber", authNumber);
		return new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
	}
	
	/**
	 * 
	 * @param userId
	 * @param dto
	 * @return
	 * 비밀번호 찾기 메서드.
	 * 이메일 인증 이후에 실행됨.
	 */
	@PostMapping("/reset/{userId}")
	public ResponseEntity<?> resetPassword(@PathVariable("userId")String userId, @RequestBody UserDto dto){
		System.out.println("send dto = "+dto);
		boolean flag = service.findById(userId);
		dto.setUserId(userId);
		Map<String, Object> map = new HashMap<>();
		HttpStatus status = HttpStatus.ACCEPTED;
		if(flag) {
			map.put("message", "해당 ID로 등록된 사용자가 존재하지 않습니다.");
			return new ResponseEntity<Map<String,Object>>(map, status);
		}
		else {
			service.resetPassword(dto);
			status = HttpStatus.OK;
			return new ResponseEntity<Map<String,Object>>(map, status);
		}
	}
	
		
	@PostMapping("/token/{userId}")
	// 로그인 성공 시, user의 refreshToken을 DB에 저장하기 위한 메서드
	// Filter단에서 JWT 토큰에 대한 인증을 진행하기 때문에, 관심사의 분리를 위해서는 Filter에 UserService를 주입하는 것은 옳지 않음.
	// 또한 직접 넣고 해보니까 순환참조 걸렸음
	public ResponseEntity<?> registToken(@PathVariable("userId")String userId, @RequestParam("refreshToken")String refreshToken){
		UserDto user = UserDto.builder().userId(userId).refreshToken(refreshToken).build();
		int result = service.updateRefreshToken(user);
		Map<String, Object>map = new HashMap<>();
		HttpStatus status = HttpStatus.ACCEPTED;
		if(result == 1) {
			status = HttpStatus.OK;
		}else {
			status = HttpStatus.UNAUTHORIZED;
		}
		return new ResponseEntity<Map<String, Object>>(map, status);
	}
	
	
	@GetMapping("/{userId}")
	// 유저 Id를 기반으로 유저의 전체 정보를 가져오는 메서드
	// 동작 순서
	// 1. FrontEnd 단에서 AccessToken을 Decode하여 PayLoad 부분에 담긴 userId 정보를 가져온다.
	// 2. Http Request Header에 "Authorization"이라는 key를 ,value로는 AccessToken을 담아 서버로 보낸다.
	// 3. 서버에서는 jwtUtil을 통해 AccessToken의 유효기간 체크와, 전체적으로 올바른 Token인지를 검증한다.
	// 4. Token 검증에 성공했다면 DB에서 id를 기반으로 User의 전체 정보를 조회하여 Map에 담아 Client에게 보낸다.
	public ResponseEntity<?> findById(@PathVariable("userId") String userId, HttpServletRequest request){
		System.out.println("userId = "+userId);
		Map<String, Object> result = new HashMap<>();
		HttpStatus status = HttpStatus.ACCEPTED;
		if(jwtUtil.checkToken(request.getHeader("Authorization"))) {
			UserDto user = service.findUserById(userId);
			System.out.println("user = "+user);
			
			if(user!=null) {
				result.put("user", user);
				status = HttpStatus.OK;
			}
			else {
				result.put("message", "id 기반 유저 정보 조회 실패.");
				status = HttpStatus.BAD_REQUEST;
			}
		}
		else {
			result.put("message",  "유효하지 않은 토큰입니다.");
			status = HttpStatus.UNAUTHORIZED;
		}
		return new ResponseEntity<Map<String,Object>>(result, status);
	}
	
	/**
	 * 
	 * @param userId
	 * @param dto
	 * @return
	 * 
	 * User의 프로필 내용 ( 아이디, 비밀번호, 주소, 핸드폰번호 )을 업데이트하는 메서드
	 */
	@PutMapping("/{userId}")
	public ResponseEntity<?> updateUser(@PathVariable("userId")String userId, @RequestBody UserDto dto){
		service.update(dto);
		HttpStatus status = HttpStatus.OK;
		return new ResponseEntity<String>("", status);
	}
	
	/**
	 * 
	 * @param userId
	 * @param dto
	 * @return
	 * 
	 * User 내용 삭제 메서드
	 * User Table과 참조되어 있는 테이블은
	 * 1. file_info - 프로필사진
	 * 2. favorite_item - 찜 목록
	 * 3. estate_item - 부동산 업체의 등록 매물
	 * 4. chatroom - 채팅방
	 * 5. chatmessage - 채팅메시지
	 */
	
	@DeleteMapping("/{userId}")
	public ResponseEntity<?> deleteUser(@PathVariable("userId")String userId, @RequestBody UserDto dto){
		System.out.println("received data = " + dto);
		service.delete(userId);
		HttpStatus status = HttpStatus.OK;
		return new ResponseEntity<String>("", status);
	}
	
	@PostMapping("/refresh")
	// AccessToken의 유효기간이 만료되었을 때, AccessToken의 재발급을 진행하는 Method
	// 동작 순서
	// 1. FrontEnd에서는 UserInfo의 정보와 RefreshToken을 Server로 보내야 한다.
	// 2. UserInfo는 Login때 전역변수로 설정되어 있기 때문에 이를 가져오고, refreshToken은 Http Requset Header에 "regenerateToken"이라는 key와
	//    value로는 refreshToken을 전송한다.
	// 3. refreshToken의 유효기간 검증과 전체 토큰 검사를 시작한다.
	// 3-1. 만약 refreshToken 역시 유효기간이 만료하였다면, FrontEnd에게 Logout을 지시한다.
	// 4. 새로운 AccessToken을 생성하여 Map에 담아 FrontEnd로 전송한다.
	public ResponseEntity<?> refresh(@RequestBody UserDto dto, HttpServletRequest request){
		log.debug("user = {}", dto);
		Map<String, Object> result = new HashMap<>();
		HttpStatus status = HttpStatus.ACCEPTED;
		if(jwtUtil.checkToken(request.getHeader("regenerateToken"))) {
			String accessToken = jwtUtil.createAccessToken(dto.getUserId(), dto.getUserRole());
			result.put("accessToken", accessToken);
			status = HttpStatus.OK;
		}
		else {
			result.put("message", "Refresh Token의 내용도 만료되었습니다.");
			status = HttpStatus.UNAUTHORIZED;
		}
		return new ResponseEntity<Map<String,Object>>(result, status);
	}
	
	@GetMapping("/logout/{userId}")
	// 로그아웃 하는 메서드
	// 동작 순서
	// 1. FrontEnd에서는 이미 저장되어있는 userId의 값을 파라미터로 Logout 요청을 서버로 보낸다.
	// 2. 서버에서는 id를 기반으로 DB에 저장된 refreshToken값을 제거하고, 성공 여부를 Client로 전송한다.
	// 3. 로그아웃에 성공했다면 Client는 Login 관련 변수들을 다 초기화하고, SessionStorage의 내용을 다 삭제한다.
	public ResponseEntity<?> logout(@PathVariable("userId") String userId){
		service.updateRefreshToken(UserDto.builder().userId(userId).build());
		HttpStatus status = HttpStatus.ACCEPTED;
		Map<String, Object> result = new HashMap<>();
		result.put("message",  "로그아웃 성공");
		status = HttpStatus.OK;
		return new ResponseEntity<Map<String,Object>>(result, status);
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 * 
	 * AccessToken의 유효기간이 만료되었을 때 사용자를 강제로 로그아웃 시키는 메서드
	 * Security Config와 JWT Filter에서 permitAll을 통해 별도의 인증을 거치지 않고 실행
	 */
	
	@GetMapping("/force/logout/{userId}")
	public ResponseEntity<?> forceLogout(@PathVariable("userId") String userId){
		service.removeRefreshToken(UserDto.builder().userId(userId).build());
		HttpStatus status = HttpStatus.ACCEPTED;
		Map<String, Object> result = new HashMap<>();
		result.put("message", "강제 로그아웃 및 refreshToken 삭제 성공");
		status = HttpStatus.OK;
		return new ResponseEntity<Map<String,Object>>(result, status);
	}
	
	/**
	 * 
	 * @param userId
	 * @param file
	 * @return
	 * 
	 * 사용자의 이미지를 업데이트하는 메서드
	 */
	
	@PostMapping("/image/{userId}")
	public ResponseEntity<?> changeProfileImage(@PathVariable("userId")String userId,
			@RequestParam("image") MultipartFile file){
		Map<String,Object> map = new HashMap<>();
		HttpStatus status = HttpStatus.ACCEPTED;
		try {
			if(file!=null) {
				System.out.println("call file is nnot null");
				String today = new SimpleDateFormat("yyMMdd").format(new Date());
				String uploadDir = "C:/upload/img";
				String imgDirPath = uploadDir + File.separator+today;
                File folder = new File(imgDirPath);
                if (!folder.exists()) folder.mkdirs();
                log.debug("img path : {}",imgDirPath);
                if (!folder.exists()) folder.mkdirs();
				UserImageDto dto = new UserImageDto();
				String originalFileName = file.getOriginalFilename();
				if(!originalFileName.isEmpty()) {
					String fixedFileName = UUID.randomUUID().toString()+
							originalFileName.substring(originalFileName.lastIndexOf("."));
					dto.setOriginalName(originalFileName);
					dto.setFixedName(fixedFileName);
					dto.setSaveFolder(today);
					log.debug("실제 이름 : {} , 수정 이름 : {}",originalFileName,fixedFileName);
					file.transferTo(new File(folder, fixedFileName));
				}
				UserDto user = UserDto.builder().userId(userId).userImageDto(dto).build();
				user.getUserImageDto().setType("profile");
				service.changeImage(user);
				status = HttpStatus.OK;
				map.put("imageName", originalFileName);
			}
		}
		catch(Exception e) {
			System.out.println("error call");
			e.printStackTrace();
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<Map<String,Object>>(map, status);
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 * 
	 * 사용자의 이미지를 삭제하는 메서드
	 */
	@DeleteMapping("/image/{userId}")
	public ResponseEntity<?> deleteProfileImage(@PathVariable("userId")String userId) {
		int result = service.deleteUserImage(userId);
		Map<String,Object> map = new HashMap<>();
		HttpStatus status = HttpStatus.ACCEPTED;
		if(result == 1) {
			status = HttpStatus.OK;
		}else {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<Map<String,Object>>(map, status);
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 * 
	 * 사용자의 이미지를 가져오는 메서드
	 */
	
	@GetMapping("/image/{userId}")
	public ResponseEntity<?> getProfileImage(@PathVariable("userId")String userId){
		UserDto user = service.selectUserImage(userId);
		if(user==null) {
			user = UserDto.builder()
					.userId(userId)
					.userImageDto(UserImageDto.builder()
							.saveFolder("")
							.fixedName("default.png").build())
					.build();
		}
		String uploadDir = "C:/upload/img";
		Path filePath = Paths.get(uploadDir, user.getUserImageDto().getSaveFolder(), user.getUserImageDto().getFixedName());
		log.error(filePath.toString());
		File file = new File(filePath.toString());
		try {
			String base64Image = service.encodeFileToBase64(file);
			return ResponseEntity.ok(base64Image);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 * 
	 * 아이디 중복조회 메서드
	 * 회원가입 메서드 호출 이전에 아이디 중복조회가 필수적으로 이루어져야 회원가입 가능
	 */
	
	@GetMapping("/check/{userId}")
	public ResponseEntity<?> duplicateCheck(@PathVariable("userId")String userId){
		System.out.println("call check method");
		Map<String, Object> map = new HashMap<>();
		boolean flag = service.findById(userId);
		String message = "이미 사용중인 아이디입니다.";
		HttpStatus status = HttpStatus.ACCEPTED;
		if(flag) {
			message = "사용 가능한 아이디입니다.";
			status = HttpStatus.OK;
		}
		map.put("message", message);
		return new ResponseEntity<Map<String,Object>>(map, status);
	}

	
}
