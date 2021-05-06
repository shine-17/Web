### Local 클래스
간단히 말하면 지역의 [언어][나라] 등의 정보를 담고 있는 클래스이다.

3가지의 생성자
* Locale(String language)
* Locale(String language, String country)
* Locale(String language, String country, String variant)

```
Locale locale = new Locale("ko", "KR");
 
getDisplayLanguage : 한국어
getLanguage : ko
getDisplayCountry : 대한민국
getCountry : KR
 ```
