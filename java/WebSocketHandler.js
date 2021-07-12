var mainWebSocket; // 웹소켓
var isMainWebSocketConnected = false; // 연결 여부
var _WebSocketGetDataCallback = null;
var chatRoomId = ""; // 채팅방ID
var userInfo; // 발신자 정보
var communicationUserList = []; //전체유저목록

function mainWebSocketConnect() {
	if (mainWebSocket === undefined && isMainWebSocketConnected === false) {
		var ip = location.host;
		mainWebSocket = new WebSocket("ws://" + ip + "/mainWebSocketHandler");
	
		mainWebSocket.onopen = function() {
	        if (window.location.hostname == 'localhost')
	            console.log("WebSocket Connected.");
			isMainWebSocketConnected = true;
		}

		mainWebSocket.onclose = function() {
	        if (window.location.hostname == 'localhost')
	            console.log("WebSocket Closed.");
			isMainWebSocketConnected = false;
			mainWebSocket = undefined;
		}

		mainWebSocket.onerror = function() {
			console.log("WebSocket Occured error.");
		}

		mainWebSocket.onmessage = function(event) {
			var data = JSON.parse(event.data);
		
			if (data !== null) {
				// 서버와 연결 후 이벤트
				if (data.message == 'CONNECTED') {
					// 실시간 이벤트 세션 추가 (소켓 연결)
                    //if (typeof addNoticeEventSocketList === 'function')
                    //    addNoticeEventSocketList();
				}
				else if(data.message == 'eventsearch'){ //이벤트 조회 - 그리드
					eventDataCallback(data);
				}	
				else if(data.message == 'eventchart'){ //이벤트 조회 - 차트
					eventChartCallback(data);
				}
				else if(data.reqType == "start"){ //채팅
					communicationUserList = data.communicationUserList;
					chatRoomId = data.chatRoomId;
					userInfo = data;
					showPopup("/chat", "chat");
					
					if(data.msgType == "invitation") {
						var param = {};
				    	param.pageType = "chat";
				        param.reqType = "message";
				        param.msgType = "enter";
				        param.msg = $userinfo.user_name + getLanguage('hasentered');
				        param.user_id = $userinfo.user_id;
				        param.chatRoomId = data.chatRoomId;
				        param.communicationUserList = data.communicationUserList;
				        param.loginUserList = data.loginUserList;
				        
				        if (mainWebSocket !== undefined && isMainWebSocketConnected === true) {
				    		mainWebSocket.send(JSON.stringify(param));
				    	}
					}
				}
				else if(data.reqType == "message") {
					for (var i = 0; i < chatPopArr.length; i++) {
						if (chatPopArr[i] == null) continue;
						
						chatRoomId = chatPopArr[i].name;
						
			            if(chatRoomId == data.chatRoomId) {
			            	if(data.msgType == "enter") { //채팅방 입장
			                	if(data.user_id != $userinfo.user_id) {
			                		chatPopArr[i].document.getElementById('chatroom').innerHTML += '<div class="chatBox chatConnection">'+data.msg+'</div>';
			                		setCommunicationUserList(data, chatPopArr[i]);
			                	}
			                } else if (data.msgType == "communication") { //채팅
			                	if(data.userId == $userinfo.user_id) {
			                		chatPopArr[i].document.getElementById('chatroom').innerHTML += '<div class="chatBox"><div class="chatContents chatCaller">'+data.msg+'</div></div>';
			                    } else {
			                    	chatPopArr[i].document.getElementById('chatroom').innerHTML += '<div class="chatBox"><div class="chatName">'+data.userName+'</div><div class="chatContents">'+data.msg+'</div></div>';
			                    }
			                } else if (data.msgType == "leave") { //채팅방 퇴장
			                	chatPopArr[i].document.getElementById('chatroom').innerHTML += '<div class="chatBox chatConnection">'+data.msg+'</div>';
			                	setCommunicationUserList(data, chatPopArr[i]);
			                } else if (data.msgType == "reload") { //로그인유저 갱신
			                	setCommunicationUserList(data, chatPopArr[i]);
			                }
			            	
			            	//메시지 수신,발신 시 스크롤 맨밑 항상 유지
			            	if(chatPopArr[i].document.getElementById('chatroom') != null)
			            		chatPopArr[i].document.getElementById('chatroom').scrollTop = chatPopArr[i].document.getElementById('chatroom').scrollHeight;
			            }//if
					}//for
					
				}
			}
		}
	}
}

function sendMessageToSocket(obj) {
	if (mainWebSocket !== undefined && isMainWebSocketConnected === true) {
		mainWebSocket.send(JSON.stringify(obj));
	}
}

function closeMainWebSocket() {
	if (mainWebSocket !== undefined) {
		mainWebSocket.close();
		isMainWebSocketConnected = true;
		mainWebSocket = undefined;
	}
}

var callbackTimer;
function callbackCountdown(count) {
    if (callbackTimer) {
        clearInterval(callbackTimer);
    }
    
    var count1 = count;
    callbackTimer = setInterval(function() {
        if (document.getElementById("callbackCountdown") != null) {
            document.getElementById("callbackCountdown").innerHTML = count1;
            count1--;
            if (count1 <= 0) {
                clearInterval(callbackTimer);
            }
        }
        else {
            clearInterval(callbackTimer);
        }
    }, 1000);
}

//채팅방 초대목록
function setCommunicationUserList(data, chatPopArr) {
	
	var communicationUserList = data.communicationUserList;
	var loginUserList = data.loginUserList;
	var inChatRoomUserList = data.inChatRoomUserList;
	var chatRoomUserList = [];
	
	for(var i=0; i<communicationUserList.length; i++) {
		var check = 0;
		
		for(var j=0; j<inChatRoomUserList.length; j++) {
			if(communicationUserList[i].user_id == inChatRoomUserList[j])
				check++;
		}
		if(check < 1)
			chatRoomUserList.push(communicationUserList[i]);
	}
	   
	// FALCON-EYAS:USERS:ID -> ID 가공
	for(var i=0; i<loginUserList.length; i++) {
		var item = loginUserList[i];
		loginUserList[i] = item.substring(item.indexOf(':',9)+1,item.length); 
	}
		
	var communicationUserListWrapper = chatPopArr.document.getElementById('communicationUserList');
	var temp="";
	//목록 비우기
	$(communicationUserListWrapper).empty();
	
	for(var i=0; i<chatRoomUserList.length; i++) {
		if(chatRoomUserList[i].user_id != $userinfo.user_id) {
			var communicationUser = '<li class="communicationUser"><div class="statusWrapper">';
			var check = 0;
			
			for(var j=0; j<loginUserList.length; j++) {
				if(chatRoomUserList[i].user_id == loginUserList[j]) {
					communicationUser += '<i class="fa fa-circle" ></i>';
					check++;
				}
			}
			
			communicationUser += '</div><div class="userWrapper">'+
			'<span class="userName" data-userid="'+chatRoomUserList[i].user_id+'">'+chatRoomUserList[i].user_name+'</span><span class="userInfo">'+
			chatRoomUserList[i].corp_name+' / '+chatRoomUserList[i].dept_name+' / '+
			chatRoomUserList[i].position_name+'</span></div>'+
			'<div class="userCheck"><label class="cb-checkbox"><input type="checkbox" class="selectUser"></label></div></li>';
			
			//온라인 사용자 목록 먼저 append
			if(check > 0) 
				communicationUserListWrapper.innerHTML += communicationUser;
			else
				temp += communicationUser;
		}
	}
	
	//오프라인 사용자 목록 append
	communicationUserListWrapper.innerHTML += temp;
	//checkBo()
	var checkbox = chatPopArr.document.querySelector('.cb-checkbox');
	for(var i=0; i<$(checkbox).length; i++) {
		if(!$(checkbox).eq(i).children().hasClass("cb-inner"))
			$(communicationUserListWrapper).checkBo();
	}
	
	//채팅방 정보 변경
	var userName = chatPopArr.document.getElementById('userName');
	var userInfo = chatPopArr.document.getElementById('userInfo');
	var groupChat = chatPopArr.document.getElementById('groupChat');
	var groupChatLabel = chatPopArr.document.getElementById('groupChatLabel');
	var inChatUserList = chatPopArr.document.getElementById('inChatUserList');
	var inChatUserListWrapper = chatPopArr.document.getElementById('inChatUserListWrapper');
	var inChatRoomInfo = [];
	
	for(var i=0; i<communicationUserList.length; i++) {
		for(var j=0; j<inChatRoomUserList.length; j++) {
			if(communicationUserList[i].user_id == inChatRoomUserList[j])
				inChatRoomInfo.push(communicationUserList[i]);
		}
	}
	
	//대화상대가 1명일 때
	if(inChatRoomInfo.length < 3) {
		if(inChatRoomInfo.length > 1 && inChatRoomInfo[0].user_id == user_info[0].user_id) {
			userName.innerText = inChatRoomInfo[1].user_name;
			userInfo.innerText = inChatRoomInfo[1].corp_name + " / "+inChatRoomInfo[1].dept_name + " / "+inChatRoomInfo[1].position_name;
		} else {
			userName.innerText = inChatRoomInfo[0].user_name;
			userInfo.innerText = inChatRoomInfo[0].corp_name + " / "+inChatRoomInfo[0].dept_name + " / "+inChatRoomInfo[0].position_name;
		}
		$(userName).css("display", "block");
		$(userInfo).css("display", "block");
		$(groupChat).css("display", "none");
		$(groupChatLabel).css("display", "none");
		$(inChatUserListWrapper).addClass("inChatOn");
	}
	//대화상대가 2명이상일 때(단체채팅방)
	else {
		var chatRoomInfo = chatPopArr.document.getElementById('chatRoomInfo');
		
		if($(chatRoomInfo).find('#groupChat').css("display") != "block" && $(groupChatWrapper).length < 4) {
			var groupChatWrapper = '<div class="groupChatImageWrapper"><div class="groupChatImageBox"><img src="../resources/imgs/main/logout.png" width="12px" height="12px"></div></div>';
			$(groupChat).empty();
			$(groupChat).append(groupChatWrapper.repeat(4));
			$(groupChatLabel).text(getLanguage('groupconversation'));
			
			$(userName).css("display", "none");
			$(userInfo).css("display", "none");
			$(groupChat).css("display", "block");
			$(groupChatLabel).css("display", "block");
		}
		
		$(inChatUserList).empty();
		
		//채팅방 참여유저 목록
		for(var i=0; i<inChatRoomInfo.length; i++) {
			var inChatUser = '<li class="inChatUser">';
			
			inChatUser += '<label class="inChatUserName">'+inChatRoomInfo[i].user_name+'</label>';
			
			if(inChatRoomInfo[i].user_info == undefined) {
				inChatUser += '<label class="inChatUserInfo">'+inChatRoomInfo[i].corp_name+' / '+inChatRoomInfo[i].dept_name+' / '+inChatRoomInfo[i].position_name+'</label>';
			} else {
				inChatUser += '<label class="inChatUserInfo">'+inChatRoomInfo[i].user_info+'</label>';
			}
			inChatUser += '</li>';
			
			$(inChatUserList).append(inChatUser);
			
			if(i == (inChatRoomInfo.length-1))
				$(inChatUserList).children().eq(i).css("border-bottom", "none");
		}
	}
	
	//유저 호버,클릭이벤트
	addEventUserList(chatPopArr);
	//채팅방초대 유저목록 검색
	searchInvitationUserList(chatPopArr);
}

//유저 호버,클릭이벤트
function addEventUserList(chatPopArr) {
	var communicationUser = chatPopArr.document.getElementById("communicationUserList");
	//클릭이벤트
	$(communicationUser).unbind("click");
	$(communicationUser).bind("click", function (e){
		clickCommunicationUser(e);
	});
	
	//호버이벤트
	communicationUser.addEventListener("mouseover", mouseoverCommunicationUser);
	communicationUser.addEventListener("mouseout", mouseoutCommunicationUser);
	
	function clickCommunicationUser(e) {
		if(e.target.closest('.communicationUser').getElementsByClassName('cb-checkbox')[0].classList.contains("checked"))
			e.target.closest('.communicationUser').getElementsByClassName('cb-checkbox')[0].classList.remove("checked");
		else
			e.target.closest('.communicationUser').getElementsByClassName('cb-checkbox')[0].classList.add("checked");
	}
	function mouseoverCommunicationUser(e) {
		if(e.target.closest('.communicationUser').classList.contains('on') == false && (e.target.closest('.communicationUser') != undefined || e.target.closest('.communicationUser') != null)) {
			e.target.closest('.communicationUser').classList.add('hover');
		}
	}
	function mouseoutCommunicationUser(e) {
		if(e.target.closest('.communicationUser') != undefined || e.target.closest('.communicationUser') != null) {
			e.target.closest('.communicationUser').classList.remove('hover');
		}
	}
}

//채팅방초대 유저목록 검색
function searchInvitationUserList(chatPopArr) {
	var communicationUser = chatPopArr.document.querySelectorAll(".communicationUser");
	var searchUser = chatPopArr.document.querySelector("#searchUser");
	
	//대화목록 검색기능
	$(searchUser).on("keyup", function() {
		if($(searchUser).val().length > 0) {
			for(var i=0; i<$(communicationUser).length; i++) {
				if($(communicationUser).eq(i).find(".userName").text().indexOf($(searchUser).val()) > -1)
					$(communicationUser).eq(i).css("display","flex");
				else
					$(communicationUser).eq(i).css("display","none");
			}
		} else {
			for(var i=0; i<$(communicationUser).length; i++)
				$(communicationUser).eq(i).css("display","flex");
		}
	})
}

