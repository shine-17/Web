# WebSocket

### 웹소켓은 하나의 TCP 접속에 전이중(양방향) 통신 채널을 제공하는 컴퓨터 통신 프로토콜   

#### 클라이언트의 요청에 대해서만 응답을 보내는 HTTP를 이용한 실시간 통신의 문제를 해결하기 위해 웹소켓이 등장했다.   
* 웹소켓은 실시간 양방향 통신을 지원하며 한번 연결이 수립되면 클라이언트와 서버 모두 자유롭게 데이터를 보낼 수 있다.
* 채팅과 같은 연속적인 통신에 대해 계속 유사한 통신을 반복하지 않게 해주어 통신의 효율성도 개선했다.   

웹소켓은 HTTP와 같은 OSI 모델의 7계층에 위치하는 프로토콜이며, 4계층의 TCP에 의존한다.

| HTTP 프로토콜을 이용할 때 "http"를 이용하는 것처럼,
| | 웹소켓을 이용할 때 "ws"를 이용한다.
