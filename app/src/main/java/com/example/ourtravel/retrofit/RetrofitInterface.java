package com.example.ourtravel.retrofit;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RetrofitInterface {

    String RETROFIT_URL = "http://3.39.168.165/";

    // 회원가입
    @FormUrlEncoded
    @POST("userinfo/register.php")
    Call<String> getUserRegist(
            @Field("email") String email,
            @Field("nick") String nick,
            @Field("password") String password

    );

    // 동행글 업로드
    @Multipart
    @POST("home/upload.php")
    Call<CompanyData> Upload(
            @Part MultipartBody.Part uploaded_file,
            @Query("type") String type,
            @Query("upload_date") String upload_date,
            @Query("time") String time,
            @Query("startdate") String startdate,
            @Query("finishdate") String finishdate,
            @Query("company") String people,
            @Query("mainspot") String mainspot,
            @Query("subspot") String subspot,
            @Query("title") String title,
            @Query("content") String content,
            @Query("user_email") String user_email,
            @Query("mainpos") int mainpos,
            @Query("subpos") int subpos

    );


    // 동행글 수정
    @Multipart
    @POST("home/updateAll.php")
    Call<CompanyData> updateAll(

            @Part MultipartBody.Part uploaded_file,
            @Query("type") String type,
            @Query("startdate") String startdate,
            @Query("finishdate") String finishdate,
            @Query("company") String people,
            @Query("mainspot") String mainspot,
            @Query("subspot") String subspot,
            @Query("title") String title,
            @Query("content") String content,
            @Query("user_email") String user_email,
            @Query("mainpos") int mainpos,
            @Query("subpos") int subpos,
            @Query("id") int id

    );

    // 다이어리 리스트 불러오기
    @GET("diary/diarylist.php")
    Call<List<DiaryData>> diarylist(
            @Query("user_email") String user_email
    );

    // 다이어리 업로드 되어있는 사진 불러오기
    @FormUrlEncoded
    @POST("diary/diaryimg.php")
    Call<List<DiaryData>> setdiaryimg(
      @Field("id") int id
    );



    // 다이어리 업로드
    @Multipart
    @POST("diary/diaryupload.php")
    Call<String> diaryupload(
            @Part ArrayList<MultipartBody.Part> files,
            @Query("content") String content,
            @Query("date") long date,
            @Query("user_email") String user_email,
            @Query("range") int range

            );

    // 다이어리 수정 (사진이 섞여있는 경우 )
    @Multipart
    @POST("diary/updateMixed.php")
    Call<String> updateMixed(
            @Part ArrayList<MultipartBody.Part> files, // 경로 포함하고 있는 사진들
            @Query("photo_list[]") ArrayList<String> photo_list,
            @Query("content") String content, //수정 내용
            @Query("date") long date, // 수정 시간
            @Query("id") int id, // 일기 고유 id
            @Query("user_email") String user_email // 사진 db에 저장할 때 쓸 유저 이메일

    );



    // 다이어리 사진 없이 업로드하기.
    @FormUrlEncoded
    @POST("diary/withoutpic.php")
    Call<DiaryData> withoutpic(
            @Field("content") String content,
            @Field("date") long date,
            @Field("user_email") String user_email,
            @Field("range") int range

    );

    // 다이어리 사진 없이 수정하기.
    @FormUrlEncoded
    @POST("diary/Upwithoutpic.php")
    Call<DiaryData> Upwithoutpic(
            @Field("content") String content,
            @Field("date") long date,
            @Field("id") int id,
            @Field("check") int check


    );



    //다이어리 수정
    @Multipart
    @POST("diary/diaryupdate.php")
    Call<String> diaryupdate(
            @Part ArrayList<MultipartBody.Part> files,
            @Query("content") String content,
            @Query("user_email") String user_email,
            @Query("date") long date,
            @Query("id") int id
    );


    //===============================================친구 관련=================================================

    //친구 신청
    @GET("Friend/ApplyFriend.php")
    Call<FriendData> ApplyFriend(
            @Query("from") String from, // 누가
            @Query("to") String to // 누구에게 친구신청을 보냈는지
    );

    //친구 신청 확인
    @GET("Friend/CheckApply.php")
    Call<FriendData> CheckApply(
            @Query("from") String from, // 누가
            @Query("to") String to // 누구에게 친구신청을 보냈는지
    );

    //친구 신청 취소
    @GET("Friend/CancleFriend.php")
    Call<FriendData> CancleFriend(
            @Query("from") String from, // 누가
            @Query("to") String to // 누구에게 친구신청을 보냈는지
    );


    //친구 리스트 있는지 확인하기
    @GET("Friend/CheckFriendList.php")
    Call<FriendData> CheckFriendList(
            @Query("now_user_email") String now_user_email // 현재 로그인한 유저 이메일
    );


    //받은 친구 요청 있는지 확인하기.
    @GET("Friend/CheckApplyList.php")
    Call<FriendData> CheckApplyList(
            @Query("now_user_email") String now_user_email // 현재 로그인한 유저 이메일
    );

    //보낸 친구 요청 있는지 확인하기.
    @GET("Friend/CheckRequest.php")
    Call<FriendData> CheckRequest(
            @Query("now_user_email") String now_user_email // 현재 로그인한 유저 이메일
    );


    // 보낸 친구 요청 리스트 불러오기
    @GET("Friend/RequestList.php")
    Call<List<OngoingData>> RequestList(
            @Query("now_user_email") String now_user_email // 현재 로그인한 유저 이메일
    );


    // 받은 친구 요청 리스트 불러오기
    @GET("Friend/ResponseList.php")
    Call<List<OngoingData>> ResponseList(
            @Query("now_user_email") String now_user_email // 현재 로그인한 유저 이메일
    );

    //보낸 친구 요청 취소하기.
    @GET("Friend/DeleteRequest.php")
    Call<FriendData> DeleteRequest(
            @Query("now_user_email") String now_user_email, // 친구 요청 보낸 사람 - 현재 로그인한 사람
            @Query("user_email") String user_email, // 친구 요청 받은 사람
            @Query("border") int border // 단순 이웃 신청인지 서로 이웃 신청인지 구분하기 위함.

    );

    //받은 친구 요청 수락하기.
    @GET("Friend/AcceptFriend.php")
    Call<FriendData> AcceptFriend(
            @Query("now_user_email") String now_user_email, // 친구 요청 보낸 사람 - 현재 로그인한 사람
            @Query("user_email") String user_email, // 친구 요청 받은 사람
            @Query("border") int border // 단순 이웃 신청인지 서로 이웃 신청인지 구분하기 위함.

    );

    //친구 요청 수락 했는지 친구요청 취소 전에 확인하기
    @GET("Friend/CheckAccept.php")
    Call<FriendData> CheckAccept(
            @Query("now_user_email") String now_user_email, // 친구 요청 보낸 사람 - 현재 로그인한 사람
            @Query("user_email") String user_email // 친구 요청 받은 사람

    );

    // 이웃 신청할때 신청하는 유저의 정보 불러오기
    @FormUrlEncoded
    @POST("Friend/SetUserInfo.php")
    Call<UserData> SetUserInfo(
            @Field("user_email") String user_email

    );

    //이웃 신청
    @GET("Neighbor/ApplyNeighbor.php")
    Call<NeighborData> ApplyNeighbor(
            @Query("from") String from, // 누가
            @Query("to") String to, // 누구에게 친구신청을 보냈는지
            @Query("border") int border // 단순 이웃 신청인지 서로 이웃 신청인지 구분하기 위함.
    );

    // 친구 리스트 불러오기
    @GET("Neighbor/FriendList.php")
    Call<List<FriendData>> FriendList(
            @Query("now_user_email") String now_user_email // 현재 로그인한 유저 이메일
    );

    //서로이웃 요청에서 그냥 이웃 으로 전환
    @GET("Friend/ChangeBfToNeighbor.php")
    Call<FriendData> ChangeBfToNeighbor(
            @Query("from") String now_user_email, // 친구 요청 보낸 사람 - 현재 로그인한 사람
            @Query("to") String user_email, // 친구 요청 받은 사람
            @Query("border") int border // 단순 이웃 신청인지 서로 이웃 신청인지 구분하기 위함.

    );


    //=======================================================================================================

    //================================================댓글 관련=================================================

    //댓글 작성
    @FormUrlEncoded
    @POST("DiaryComment/sendcomment.php")
    Call<CommentData> sendcomment(
            @Field("content") String content,
            @Field("date") long date,
            @Field("id") int id,
            @Field("user_email") String user_email

    );

    //대댓글 작성
    @FormUrlEncoded
    @POST("DiaryComment/sendrecomment.php")
    Call<CommentData> sendrecomment(
            @Field("content") String content,
            @Field("date") long date,
            @Field("id") int id,
            @Field("user_email") String user_email,
            @Field("top") int top

    );

    // 댓글 수정
    @FormUrlEncoded
    @POST("DiaryComment/upcomment.php")
    Call<CommentData> updatecomment(
            @Field("content") String content, // 댓글 수정 내용
            @Field("comment_id") int id // 댓글 id

    );

    // 댓글 삭제
   @FormUrlEncoded
    @POST("DiaryComment/deletecomment.php")
    Call<CommentData> deletecomment(
            @Field("level") int level, // 댓글 level
            @Field("comment_id") int comment_id, // 댓글 id
            @Field("child") int child, // 댓글 child
            @Field("diary_id") int diary_id, // 댓글이 달린 일기의 id
            @Field("top") int top // 댓글 top

    );

   // 댓글 삭제시 상태 변화
   @FormUrlEncoded
   @POST("DiaryComment/deletestatus.php")
   Call<CommentData> deletestatus(
           @Field("level") int level, // 댓글 수정 내용
           @Field("comment_id") int comment_id, // 댓글 id
           @Field("child") int child, // 댓글 child
           @Field("diary_id") int diary_id, // 댓글이 달린 일기의 id
           @Field("top") int top // 댓글 top

   );

    // 댓글 좋아요 선택
    // 유저가 댓글의 좋아요를 선택했을때 댓글의 좋아요 갯수가 1만큼 상승해야 되며 db에도 저장이 되어야 함.
    // 현재 댓글의 id, 좋아요 누른 유저의 이메일 보내야함.
    @FormUrlEncoded
    @POST("DiaryComment/commentlike.php")
    Call<CommentData> clickcommentlike(
            @Field("now_user_email") String now_user_email,
            @Field("comment_id") int comment_id,
            @Field("status") boolean status

    );


    //현재 작성하고 있는 유저의 정보 불러오기
    @FormUrlEncoded
    @POST("DiaryComment/nowuser.php")
    Call<DiaryData> nowuser(
            @Field("user_email") String user_email

    );

    //댓글 불러오기
    // 댓글 리스트 불러오기
    @GET("DiaryComment/commentlist.php")
    Call<List<CommentData>> commentlist(
            @Query("diary_id") int id, //게시글 id
            @Query("now_user_email") String now_user_email

    );

    //=================================================================================================

    // 동행 종료 업데이트
    // 동행글 상세보기
    @GET("home/CompanyFinish.php")
    Call<List<CompanyData>> CompanyFinish(
            @Query("my_user_email") String my_user_email
    );

    // 동행글 업데이트
    @FormUrlEncoded
    @POST("home/update.php")
    Call<CompanyData> update(
            @Field("type") String type,
            @Field("startdate") String startdate,
            @Field("finishdate") String finishdate,
            @Field("company") String people,
            @Field("mainspot") String mainspot,
            @Field("subspot") String subspot,
            @Field("title") String title,
            @Field("content") String content,
            @Field("photo") String photo,
            @Field("user_email") String user_email,
            @Field("mainpos") int mainpos,
            @Field("subpos") int subpos,
            @Field("id") int id
    );




    //basicpic.php
    // 동행글 업로드시 기본이미지로 업로드 할 때
    @FormUrlEncoded
    @POST("home/basicpic.php")
    Call<CompanyData> basicpic(
            @Field("type") String type,
            @Field("upload_date") String upload_date,
            @Field("time") String time,
            @Field("startdate") String startdate,
            @Field("finishdate") String finishdate,
            @Field("company") String people,
            @Field("mainspot") String mainspot,
            @Field("subspot") String subspot,
            @Field("title") String title,
            @Field("content") String content,
            @Field("basic") String basic,
            @Field("user_email") String user_email,
            @Field("mainpos") int mainpos,
            @Field("subpos") int subpos

    );

    // 동행글 리스트 불러오기 (필터 적용).
    @GET("home/list.php")
    Call<List<CompanyData>> list(
            @Query("filter") int filter
    );

    // 동행글 상세보기
    @GET("home/setdata.php")
    Call<CompanyData> setdata(
            @Query("id") int id // 게시글 id
    );

    // 동행글 모집 완료 여부 확인
    @GET("home/CheckComplete.php")
    Call<ChatRoomData> CheckComplete(
            @Query("roomNum") int id // 동행글 id = 채팅방 숫자
    );

    // 동행글 모집 완료 여부 확인
    @GET("home/CompleteOrResume.php")
    Call<ChatRoomData> CompleteOrResume(
            @Query("roomNum") int id, // 동행글 id = 채팅방 숫자
            @Query("status") String status // 동행 모집하는지 동행 재개하는지 확인
    );

    // 동행글 요청 전에 동행 모집 완료인지 모집 중인지 이미 참여인원이 다 찼는지 확인
    @GET("home/CheckCompany.php")
    Call<ChatRoomData> CheckCompany(
            @Query("roomNum") int id // 동행글 id = 채팅방 숫자

    );



    //==============================채팅 관련================================================

    // 채팅방 들어갔을 때 이전에 저장된 채팅데이터 불러오기
    @GET("chat/ChatData.php")
    Call<List<ChatData>> ChatData(
            @Query("roomNum") int roomNum // 채팅방 구분하기 위한 숫자
    );

    // 동행글 만들자 마자 채팅방 생성하기
    @GET("chat/chat_room_list.php")
    Call<List<ChatRoomData>> chat_room_list(
            @Query("user_email") String user_email // 채팅방 만든 사람
    );

    // 동행글 참여하기 - 방장이 아닌 다른 유저
    @GET("chat/ApplyCompany.php")
    Call<ChatRoomData> ApplyCompany(
            @Query("user_email") String user_email, // 동행 요청한 사람 이메일
            @Query("roomNum") int roomNum // 동행 요청한 동행글 id

    );

    // 채팅방 사이드 메뉴 - 동행글 상세 정보
    @GET("chat/ChatInfo.php")
    Call<CompanyData> ChatInfo(
            @Query("roomNum") int roomNum // 동행 요청한 동행글 id

    );

    // 채팅방 참여 인원 불러오기
    @GET("chat/ChatUserInfo.php")
    Call<List<ChatUser>> ChatUserInfo(
            @Query("roomNum") int roomNum // 채팅방 구분하는 숫자
    );

    // 현재 로그인한 유저가 해당 채팅방에 있는지 없는지 확인해서 동행 요청 버튼 비활성화
    @GET("chat/CheckUserList.php")
    Call<ChatUser> CheckUserList(
            @Query("roomNum") int roomNum, // 채팅방 구분하는 숫자
            @Query("user_email") String user_email // 동행 요청한 사람 이메일
    );

    // 채팅방 정보 불러오기 위함.
    @GET("chat/ChatRoominfo.php")
    Call<ChatRoomData>ChatRoominfo (
            @Query("roomNum") int roomNum // 채팅방 구분하는 숫자


    );

    // 채팅방 들어갔을 때 이전에 저장된 채팅데이터 불러오기
    @GET("chat/GetChatImg.php")
    Call<List<ChatData>> GetChatImg(
            @Query("roomNum") int roomNum, // 채팅방 구분하기 위한 숫자
            @Query("unitime") String unitime // 채팅 사진 보낸 시간
    );

    // 채팅 사진 업로드
    @Multipart
    @POST("chat/ChatPhotoUpload.php")
    Call<String> ChatPhotoUpload(
            @Part ArrayList<MultipartBody.Part> files,
            @Query("user_email") String user_email,
            @Query("roomNum") int roomNum // 동행글 id = 채팅방 숫자


    );

    // 채팅방 인원 내보내기
    @GET("chat/KickOutUser.php")
    Call<ChatUser> KickOutUser(
            @Query("roomNum") int roomNum, // 채팅방 구분하는 숫자
            @Query("user_email") String user_email // 내쫒기는 유저 이메일
    );

    // 채팅방에서 내쫒겨졌는지 아닌지 확인
    @GET("chat/KickOutCheck.php")
    Call<ChatUser> KickOutCheck(
            @Query("roomNum") int roomNum, // 채팅방 구분하는 숫자
            @Query("user_email") String user_email // 내쫒기는 유저 이메일
    );






    //=======================================================================================

    //=========================================검색 관련========================================
    // 검색어 입력하기
    @GET("HomeSearch/search.php")
    Call<ResearchData> Research(
            @Query("research") String research,
            @Query("user_email") String user_email
    );

    // 최근 검색어 리스트
    @GET("HomeSearch/searchlist.php")
    Call<List<ResearchData>> SetRecentSearch(
            @Query("user_email") String user_email
    );

    // 최근 검색어 삭제하기
    @GET("HomeSearch/delete.php")
    Call<ResearchData> DeleteSearch(
            @Query("id") int id
    );

    // 모든 최근 검색어 삭제하기
    @GET("HomeSearch/deleteAll.php")
    Call<ResearchData> DeleteAll(
            @Query("user_email") String user_email
    );

    // 검색어 결과 가져오기
    @GET("HomeSearch/searchResult.php")
    Call<List<CompanyData>> SetSearchResult(
            @Query("research") String research
    );

    //=======================================================================================

    //=========================================프로필 내 글 모아보기 관련========================================

    // 동행글 리스트 불러오기 ( 내 프로필에서 내가 쓴 동행글 불러오기 )
    @GET("home/listofuser.php")
    Call<List<CompanyData>> listofuser(
            @Query("user_email") String User_email
    );

    // 다이어리 리스트 쿨러오기 ( 내 프로필에서 내가 쓴 일기 불러오기 )
    @GET("diary/diaryofuser.php")
    Call<List<DiaryData>> diaryofuser(
            @Query("user_email") String User_email
    );

    // 다이어리 리스트 쿨러오기 ( 나와 이웃인 사람들 다이어리 )
    @GET("diary/FriendDiaryList.php")
    Call<List<DiaryData>> FriendDiaryList(
            @Query("user_email") String User_email
    );

    // 다이어리 리스트 쿨러오기 ( 서로이웃 사람들 다이어리 구분 )
    @GET("diary/BestFriendDiaryList.php")
    Call<List<DiaryData>> BestFriendDiaryList(
            @Query("user_email") String User_email
    );




    //=================================================================================================

    //============================동행 점수 남기기 관련=====================================================

    // 동행이 종료되었는지 & 같이 참여했는지 확인 ( 현재 유저와 프로필 유저)
    @GET("home/CheckCompanyFinish.php")
    Call<CompanyData> CheckCompanyFinish(
            @Query("my_user_email") String my_user_email,
            @Query("other_user_email") String other_user_email
    );


    // 동행이 종료되고 같이 참여했던 동행 리스트 불러오기
    @FormUrlEncoded
    @POST("home/FinishCompanyList.php")
    Call<List<ScoreData>> FinishCompanyList(
            @Field("room_array[]") List<Integer> room_array,
            @Field("user_email") String user_email

    );

    // 동행 점수 업데이트
    @GET("userinfo/UpdateScore.php")
    Call<ScoreData> UpdateScore(
            @Query("my_user_email") String my_user_email,
            @Query("other_user_email") String other_user_email,
            @Query("room_num") int room_num,
            @Query("score") float score
    );

    // 동행이 종료되고 같이 참여했던 동행 리스트 불러오기
    @FormUrlEncoded
    @POST("userinfo/profile.php")
    Call<UserData> BasicScore(

            @Field("user_email") String user_email

    );




    //=====================================북마크 관련=================================================
    // 북마크 체크 여부 알아보기
    @FormUrlEncoded
    @POST("home/checkbookmark.php")
    Call<List<CompanyData>> checkbookmark(

            @Field("user_email") String User_email

    );


    // 북마크 체크 하기
    @FormUrlEncoded
    @POST("home/bookmark.php")
    Call<CompanyData> clickbookmark(
            @Field("company_id") int id,
            @Field("user_email") String User_email,
            @Field("status") boolean status

    );

    // 북마크 유지 시키기
    @FormUrlEncoded
    @POST("home/staybookmark.php")
    Call<CompanyData> staybookmark(
            @Field("company_id") int id,
            @Field("user_email") String User_email

    );


    //=================================================================================================

    //============================================프로필 관련=====================================================

    // 프로필 사진 업로드 할 때
    @Multipart
    @POST("userinfo/withpic.php")
    Call<UserData> UpUserInfoWith(
            @Part MultipartBody.Part uploaded_file,
            @Query("user_nick") String nick,
            @Query("user_produce") String produce,
            @Query("user_email") String user_email
    );

    // 기본 이미지로 변경할 때 or 프로필 띄우기.
    @FormUrlEncoded
    @POST("userinfo/profile.php")
    Call<UserData> UpUserInfo(
            @Field("user_email") String user_email

    );

    // 휴대전화 인증하기
    @FormUrlEncoded
    @POST("userinfo/phoneNum.php")
    Call<UserData> phoneNum(
            @Field("user_email") String user_email, // 유저 이메일
            @Field("user_phone") String user_phone // 유저 전화번호

    );

    //=================================================================================================

    // 일기 게시글 삭제
    @FormUrlEncoded
    @POST("home/delete.php")
    Call<CompanyData> deletecontent(
            @Field("content_id") int id

    );

    // 일기 게시글 삭제
    @FormUrlEncoded
    @POST("diary/UpdiarySet.php")
    Call<DiaryData> setdiary(
            @Field("diary_id") int id

    );


    // 동행글 삭제
    @FormUrlEncoded
    @POST("diary/diarydelete.php")
    Call<DiaryData> deletediary(
            @Field("diary_id") int id

    );

    // 좋아요 선택
    @FormUrlEncoded
    @POST("diary/like.php")
    Call<DiaryData> clicklike(
            @Field("like_cnt") int like_cnt, // 다이어리 테이블에 넣을 좋아요 갯수
            @Field("diary_id") int id, // 게시글 고유 id
            @Field("user_email") String user_email, // 좋아요 누른 유저 이메일
            @Field("status") boolean status

    );

    // 수정하기 버튼 누르고 작성했던 데이터들 뿌려주기
    @FormUrlEncoded
    @POST("home/updateSet.php")
    Call<CompanyData> updateset(
            @Field("content_id") int id

    );



    // 유저 정보 업데이트 하기 사진 포함.
    @FormUrlEncoded
    @POST("userinfo/Uuserinfo.php")
    Call<UserData> UpdateUserInfo(
            @Field("user_email") String user_email,
            @Field("user_nick") String user_nick,
            @Field("user_produce") String user_produce,
            @Field("user_photo") String user_photo

    );



    // 사진 X , 닉네임, 자기소개 수정할때
    @FormUrlEncoded
    @POST("userinfo/withoutpic.php")
    Call<UserData> withoutpic(
            @Field("user_email") String user_email,
            @Field("user_nick") String user_nick,
            @Field("user_produce") String user_produce
    );

   //===============회원가입, 로그인========================================

    // 닉네임 중복 검사
    @FormUrlEncoded
    @POST("userinfo/nick.php")
    Call<String> getUserNick(
            @Field("user_nick") String user_nick

    );

    // 이메일 중복 검사
    @FormUrlEncoded
    @POST("userinfo/email.php")
    Call<String> getUserEmail(
            @Field("user_email") String user_email

    );

    // 비번 찾기
    @FormUrlEncoded
    @POST("userinfo/updatepass.php")
    Call<String> getUserpass(
            @Field("user_pass") String user_pass,
            @Field("user_email") String user_email

    );

    // 로그인
    @FormUrlEncoded
    @POST("userinfo/login.php")
    Call<String> getUserLogin(
            @Field("id") String id,
            @Field("password") String password
    );



    //==========================================================================

}
