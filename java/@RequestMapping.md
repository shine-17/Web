![image](https://user-images.githubusercontent.com/37826908/117463141-5157fa80-af8a-11eb-85d7-ca0cfb5d40bc.png)   
@MVC에서 사용하는 주요 어노테이션

## @RequestMapping
URL을 컨트롤러의 메서드와 매핑할 때 사용하는 스프링 프레임워크의 어노테이션   
클래스나 메서드 선언부에 @RequestMapping과 함께 URL을 명시하여 사용한다. URL외에도 HTTP 요청 메서드나 헤더값에 따라 매핑되도록 -0= 옵션을 제공하는데,
메서드 레벨에서 정의한 @RequestMapping은 타입 레벨에서 정의된 @RequestMapping의 옵션을 상속 받는다.   
참고로, 메서드 내에서 viewName을 별도로 설정하지 않으면 @RequestMapping의 path로 설정한 URL이 그대로 viewName으로 설정된다.
url당 하나의 컨트롤러에 매핑되던 다른 핸들러 매핑과 달리 메서드 단위까지 세분화하여 적용할 수 있으며, url 뿐 아니라 파라미터, 헤더 등 더욱 넓은 범위를 적용할 수 있다

### 간단하게 정리하면, 클라이언트는 URL로 요청을 전송하고, 요청 URL을 어떤 메서드가 처리할지   
### 여부를 결정하는 것이 바로 "@RequestMapping" 라고 할 수 있다.   

![image](https://user-images.githubusercontent.com/37826908/117463306-78aec780-af8a-11eb-83eb-0e76dbaa4836.png)

### String[] value
URL 패턴을 지정하는 속성이다. String 배열로 여러개를 지정할 수 있으며, ANT 스타일의 와일드카드를 사용할 수 있다.
