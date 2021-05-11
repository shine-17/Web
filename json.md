# JSON (JavaScript Object Notation)

#### 서버와 클라이언트 또는 애플리케이션에서 처리할 데이터를 저장하거나 전송할 때 많이 사용되는 경량의 DATA 교환 형식

#### 특징

* 서버와 클라이언트 간의 교류에서 일반적으로 많이 사용된다
* JSON 문서 형식은 자바스크립트 객체의 형식을 기반으로 만들어졌다
* 자바스크립트의 문법과 굉장히 유사하지만 텍스트 형식일 뿐이다
* 다른 프로그래밍 언어를 이용해서도 쉽게 만들 수 있다

#### 사용이유
* json은 특정 언어에 종속되지 않는다
* xml보다 최소한의 용량으로 데이터 전송이 가능하다
   * xml은 HTML과 같이 태그 구조의 형식으로 데이터를 표현하는데 구조가 복잡해지고 용량이 커지기 때문
* xml보다 구조 정의의 용이성과 가독성이 뛰어나다

#### JSON 구조
* name-value 형식 또는 키-값 형식의 쌍으로 이루어져 있다
* JSON 데이터는 쉼표(,)로 나열된다
* 객체(object)는 중괄호 {}로 둘러쌓아 표현한다
* 배열(array)은 대괄호 []로 둘러쌓아 표현한다
```json
{
  "employees": [
    {
      "name": "Surim",
      "lastName": "Son"
    },
    {
      "name": "Someone",
      "lastName": "Huh"
    },
    {
      "name": "Someone else",
      "lastName": "Kim"
    } 
  ]
}
```


#### JSON의 문제점
AJAX 는 단순히 데이터만이 아니라 JavaScript 그 자체도 전달할 수 있다. 이 말은 JSON데이터라고 해서 받았는데 단순 데이터가 아니라 JavaScript가 될 수도 있고, 그게 실행 될 수 있다는 것이다. (데이터인 줄 알고 받았는데 악성 스크립트가 될 수 있다.)   
위와 같은 이유로 받은 내용에서 순수하게 데이터만 추출하기 위한 JSON 관련 라이브러리를 따로 사용하기도 한다.

#### JSON 형식 텍스트를 Javascript Object로 변환
```javascript
var jsonText = '{ "name": "Someone else", "lastName": "Kim" }';  // JSON 형식의 문자열
var realObject = JSON.parse(jsonText);
var jsonText2 = JSON.stringify(realObject);

console.log(realObject);
console.log(jsonText2);
```
* JSON.parse( JSON으로 변환할 문자열 ) : JSON 형식의 텍스트를 자바스크립트 객체로 변환한다.
* JSON.stringify( JSON 문자열로 변환할 값 ) : 자바스크립트 객체를 JSON 텍스트로 변환한다.
