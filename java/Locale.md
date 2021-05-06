### Locale 클래스
간단히 말하면 지역의 [언어][나라] 등의 정보를 담고 있는 클래스이다.

Locale 클래스는 3가지의 생성자를 제공한다
* Locale(String language)
* Locale(String language, String country)
* Locale(String language, String country, String variant)

### Locale 클래스의 메서드
```
Locale locale = new Locale("ko", "KR");

locale.getDisplayLanguage : 한국어
locale.getLanguage : ko
locale.getDisplayCountry : 대한민국
locale.getCountry : KR
 ```
