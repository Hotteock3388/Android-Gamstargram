# Gamstargram

2학년 1학기 앱 프로그래밍 과목의 수행평가를 위해 'Firebase로 안드로이드 SNS 앱 만들기'라는 책을 보고 따라 만든 SNS 앱 입니다. 

<img src="https://user-images.githubusercontent.com/57486593/121766896-04ba9d00-cb90-11eb-8109-e8e4cdaaf968.png" width="350" height="500">
<br><br>



## 기능
- 회원가입/로그인 (+ Facebook, Google 소셜 로그인)
- 팔로우한 사용자의 게시물 시간순으로 메인에 표시(댓글, 좋아요)
- 앱의 모든 사용자가 올린 게시물 모아보기
- 게시물 올리기
- 좋아요, 댓글 달리면 알림
- 프로필 사진 변경, 게시물, 팔로워, 팔로잉 수 표시

<br><br>



<!-- 로그인 -->
<img src="https://user-images.githubusercontent.com/57486593/121769691-0a1fe380-cba0-11eb-84b1-1b5a89ffeb36.gif" width="240" >
로그인 화면입니다 <br><br><br>


<!-- 메인화면 좋아요 + 댓글 -->
<img src="https://user-images.githubusercontent.com/57486593/121769797-9205ed80-cba0-11eb-80a1-af104f162610.gif" width="240" >
팔로우한 사용자의 게시물을 시간순으로 보여주고 댓글, 좋아요 등의 상호작용을 할 수 있는 Home 화면입니다. <br><br>

<!-- 사진 업로드 -->
<img src="https://user-images.githubusercontent.com/57486593/121769988-9e3e7a80-cba1-11eb-8059-7389622d836c.gif" width="240" >
갤러리에서 사진을 업로드 할 수 있는 Photo 화면입니다. <br><br><br>


<img src="https://user-images.githubusercontent.com/57486593/121770093-45231680-cba2-11eb-8da0-b385488abf66.png" width="220" ><img src="https://user-images.githubusercontent.com/57486593/121770072-2f155600-cba2-11eb-8aa4-16b9f37875f1.png" width="220" ><img src="https://user-images.githubusercontent.com/57486593/121770077-3472a080-cba2-11eb-834a-63cce5f4e20d.png" width="220" >
<br><br> 사진 모아보기, 알림 페이지, 마이 페이지 입니다.



## 📂 Library
Firebase-Auth - Firebase 자체 로그인 <br>
Firebase-Storage - Firebase에 이미지 저장 <br>
Firebase-FireStore - 유저, 게시물 정보 등을 저장하는 Firebase Database <br>

com.google.android.gms:play-services-auth - Google 로그인 <br>
com.facebook.android:facebook-android-sdk - Facebook 로그인 <br>

[Glide](https://github.com/bumptech/glide) - 이미지 로딩 <br>



