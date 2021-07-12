```java
package com.rtnet.eyas.handler;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rtnet.eyas.common.CommonHelper;
import com.rtnet.eyas.data.dao.MonitoringDAOImpl;


public class MainWebSocketHandler extends TextWebSocketHandler {
	
	//로그인 한 전체
	//public static List<WebSocketSession> sessions = new ArrayList<WebSocketSession>();
	// 1대1
	public static Map<String, WebSocketSession> userSessionsMap = new ConcurrentHashMap<String, WebSocketSession>(); // <user_cd, webSocketSession>
	
	public static int filterFlag = 0; /// 실시간 필터 설정 flag
	
	private CommonHelper commonHelper = new CommonHelper();
	
	private List<WebSocketSession> mainWebSocketsessions = new ArrayList<>();
	
	private Map<String, List<String>> chatRooms = new ConcurrentHashMap<String, List<String>>();
	
	@Inject
	@Autowired
	private MonitoringDAOImpl monitoringDAOImpl;
		
	//서버에 접속이 성공 했을때
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		if (session == null)
			return;

		System.out.println("afterConnectionEstablished " + session);
		
		String senderUserCd = getUserCd(session);
		userSessionsMap.put(senderUserCd , session); ///// user_cd, session을 userSessionMap에 담습니다.
		
		//메인웹소켓세션 저장
		mainWebSocketsessions.add(session);
			
	}
	
	//소켓에 메세지를 보냈을때
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//		String senderUserCd = getUserCd(session);
		//모든 유저에게 보낸다 - 브로드 캐스팅
//		for (WebSocketSession sess : sessions) {
//			sess.sendMessage(new TextMessage(senderUserCd + ": " +  message.getPayload()));
//		}
		
		try {
			if (session == null || message == null) 
				return;
				
			String json = message.getPayload();
			JsonElement jEle = new Gson().fromJson(json, JsonElement.class);

			if (jEle.isJsonObject()) {
				JsonObject jObj = jEle.getAsJsonObject();

				// 호출 페이지 구분
				String pageType = jObj.get("pageType").getAsString();
				String reqType = jObj.get("reqType").getAsString();
				if(pageType.equals("eventsearch")) { // 이벤트 조회
					WebSocketSession eventUserSession = userSessionsMap.get(jObj.get("user_cd").getAsString()); ///user session
					if ( reqType.equals("eventChange") ) { // update_time 설정
						for ( String key : userSessionsMap.keySet()) {
							if ( userSessionsMap.get(key) == eventUserSession ) { //// userSessionMap의 session과 현재 session 비교
								userSessionsMap.get(key).getAttributes().put("update_time", jObj.get("update_time").getAsString()); // 이벤트 조회 grid 가장 최근 update_time 추가
							}
						}//for
					}
					else if ( reqType.equals("deleteUpdateTime") ) { // 이벤트 조회 -> 다른 페이지
						for ( String key : userSessionsMap.keySet()) {
							if ( userSessionsMap.get(key) == eventUserSession ) { //// userSessionMap의 session과 현재 session 비교
								if ( userSessionsMap.get(key).getAttributes().get("update_time") != null ) {
									userSessionsMap.get(key).getAttributes().remove("update_time");
								}
							}
						}//for
					}
					else if ( reqType.equals("applyFilter") ) { // 실시간 필터 적용
						this.filterFlag = Integer.parseInt(jObj.get("filterFlag").getAsString());
					}
				}
				else if(pageType.equals("errorsearch")) { // 장애 조회
					WebSocketSession errorUserSession = userSessionsMap.get(jObj.get("user_cd").getAsString()); ///user session
					if ( reqType.equals("errorChange") ) { // error_update_time 설정
						for ( String key : userSessionsMap.keySet()) {
							if ( userSessionsMap.get(key) == errorUserSession ) { //// userSessionMap의 session과 현재 session 비교
								userSessionsMap.get(key).getAttributes().put("error_update_time", jObj.get("update_time").getAsString()); // 이벤트 조회 grid 가장 최근 update_time 추가
							}
						}//for
					}
					else if ( reqType.equals("deleteErrorUpdateTime") ) { // 장애 조회 -> 다른 페이지
						for ( String key : userSessionsMap.keySet()) {
							if ( userSessionsMap.get(key) == errorUserSession ) { //// userSessionMap의 session과 현재 session 비교
								if ( userSessionsMap.get(key).getAttributes().get("error_update_time") != null ) {
									userSessionsMap.get(key).getAttributes().remove("error_update_time");
								}
							}
						}//for
					}
				}
				else if(pageType.equals("chat")) { //채팅
					String userName = (String) userSessionsMap.get(getUserCd(session)).getAttributes().get("user_name");
					String userId = (String) userSessionsMap.get(getUserCd(session)).getAttributes().get("user_id");
					
					JSONParser parser = new JSONParser();
					Object obj = parser.parse(message.getPayload().toString());
					JSONObject jsonObj = (JSONObject) obj;
					jsonObj.put("userCd", getUserCd(session));
					jsonObj.put("userName", userName);
					jsonObj.put("userId", userId);
					
					//채팅방 번호
					String chatRoomId = null;
					if(jObj.get("chatRoomId") != null) {
						chatRoomId = jObj.get("chatRoomId").getAsString();
					}
					
					if(chatRoomId != null) {
						//채팅방 생성시 참여유저목록
						String receiver = null;
						if(jObj.get("receiver") != null) {
							receiver = jObj.get("receiver").getAsString();
						}
						
						String receiverList[] = null;
						if(receiver != null)
							receiverList = receiver.split(",");
						
						//chatRooms에 넣는 채팅방 참여유저목록
						List<String> receivers = new ArrayList<String>();
						int check = 0;
						//클라이언트에 보내주기 위한 채팅방 참여유저목록
						List<String> inChatRoomUserList = new ArrayList<String>();
						String msgType = null;
						
						for(String s : chatRooms.keySet()) {
							if(s.equals(chatRoomId))
								check++;
						}
						
						//채팅방 관리
						if(jObj.get("reqType") != null && jObj.get("msgType") != null) {
							msgType = jObj.get("msgType").getAsString();
							String user_id = "";
							if(jObj.get("user_id") != null)
								user_id = jObj.get("user_id").getAsString();
							
							if(!msgType.equals("communication")) {
								//이미 채팅방이 있는 경우
								if(check > 0) {
									//채팅방 관리
									if(msgType.equals("enter")) { //채팅방 입장
										if(receiverList != null && receiverList.length > 0) {
											for(String chatUser : receiverList) {
												for(WebSocketSession ws : mainWebSocketsessions) {
													if(((String) userSessionsMap.get(getUserCd(ws)).getAttributes().get("user_id")).equals(chatUser))
														chatRooms.get(chatRoomId).add(chatUser);
												}
											}
										} else {
											for(WebSocketSession ws : mainWebSocketsessions) {
												if(((String) userSessionsMap.get(getUserCd(ws)).getAttributes().get("user_id")).equals(receiver))
													chatRooms.get(chatRoomId).add(receiver);
											}
										}
									} else if(msgType.equals("leave")) { //채팅방 퇴장
										for(String chatUser : chatRooms.get(chatRoomId)) {
											if(chatUser.equals(user_id)) {
												chatRooms.get(chatRoomId).remove(chatUser);
												break;
											}
										}
									} else if(msgType.equals("invitation")) { //채팅방 초대
										for(WebSocketSession ws : mainWebSocketsessions) {
											for(String receiverUser : receiverList) {
												if(((String) userSessionsMap.get(getUserCd(ws)).getAttributes().get("user_id")).equals(receiverUser))
													chatRooms.get(chatRoomId).add(receiverUser);
											}
										}
									}
								}
								//새로만드는 경우
								else { 
									//채팅방 관리
									for(WebSocketSession sess : mainWebSocketsessions) {
										for(String receiverUser : receiverList) {
											if(((String) userSessionsMap.get(getUserCd(sess)).getAttributes().get("user_id")).equals(receiverUser))
												receivers.add(receiverUser);
										}
									}
								}
								if(check < 1)
									chatRooms.put(chatRoomId, receivers);
							}
						}
						
						//채팅방의 유저가 1명도 없을때 채팅방 삭제
						if(msgType != null && !msgType.equals("reload")) {
							for(String s : chatRooms.keySet()) {
								if(chatRooms.get(s).size() < 1)
									chatRooms.remove(s);
								else {
									int sessionCheck = 0;
									
									if(chatRooms.get(s) != null && chatRooms.get(s).size() > 0) {
										for(String receiverUser : chatRooms.get(s)) {
											for(WebSocketSession sess : mainWebSocketsessions) {
												if(receiverUser.equals((String) userSessionsMap.get(getUserCd(sess)).getAttributes().get("user_id")))
													sessionCheck++;
											}
										}
									}
									if(sessionCheck < 1)
										chatRooms.remove(s);
								}
							}
						}
						
						System.out.println("chatRooms : "+chatRooms);
						System.out.println("inChatRoom :" +chatRooms.get(chatRoomId));
						System.out.println("mainWebSocketsessions : "+mainWebSocketsessions );
						
						if(chatRooms.get(chatRoomId) != null) {
							for(String chatUser : chatRooms.get(chatRoomId)) {
								inChatRoomUserList.add(chatUser);
							}
							jsonObj.put("inChatRoomUserList", inChatRoomUserList);
							
							String caller = null;
							if(jObj.get("caller") != null)
								caller = jObj.get("caller").getAsString();
							
							if(msgType.equals("invitation")) {
								for(String chatUser : chatRooms.get(chatRoomId)) {
									for(String receiverUser : receiverList) {
										if(chatUser.equals(receiverUser)) {
											for(WebSocketSession ws : mainWebSocketsessions) {
												if(((String) userSessionsMap.get(getUserCd(ws)).getAttributes().get("user_id")).equals(receiverUser))
													ws.sendMessage(new TextMessage((CharSequence) jsonObj.toString()));
											}
										}
									}
								}
							} else if(caller != null) {
								//메시지 전송
								for(String chatUser : chatRooms.get(chatRoomId)) {
									for(WebSocketSession ws : mainWebSocketsessions) {
										if(((String) userSessionsMap.get(getUserCd(ws)).getAttributes().get("user_id")).equals(chatUser) && !chatUser.equals(caller))
											ws.sendMessage(new TextMessage((CharSequence) jsonObj.toString()));
									}
								}
							} else {
								//메시지 전송
								for(String chatUser : chatRooms.get(chatRoomId)) {
									for(WebSocketSession ws : mainWebSocketsessions) {
										if(((String) userSessionsMap.get(getUserCd(ws)).getAttributes().get("user_id")).equals(chatUser))
											ws.sendMessage(new TextMessage((CharSequence) jsonObj.toString()));
									}
								}
							}
						}
					} else {
						
					}
				}
				
			}//if
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//연결 해제될때
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

		//RESET MAP USES
		int user_cd = Integer.parseInt(getUserCd(session));
		if(user_cd > 0) {
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("user_cd", user_cd);
			monitoringDAOImpl.resetMapInfo(params);
		}
		
		System.out.println("afterConnectionClosed " + session + ", " + status);
		userSessionsMap.remove(session.getId()); /// userSessionMap에서 session 삭제
		
		mainWebSocketsessions.remove(session);
		
	}
	
	//웹소켓 user_cd 가져오기
	private String getUserCd(WebSocketSession session) {
		Map<String, Object> httpSession = session.getAttributes();
		CommonHelper CommonHelper = new CommonHelper();
		String user_cd = CommonHelper.getUserInfo((String) httpSession.get("user_info"), "user_cd"); // httpSession에서 user_info 속에 있는 user_cd 가져오기
		
		if(user_cd == null) {
			return session.getId();
		} else {
			return user_cd;
		}
	}
	
	/// webSocket return;
	public static Map<String, WebSocketSession> getUserSessionsMap() {
		return userSessionsMap;
	}
	
	/// filterFlag return;
	public static int getFilterFlag() {
		return filterFlag;
	}
}
```
