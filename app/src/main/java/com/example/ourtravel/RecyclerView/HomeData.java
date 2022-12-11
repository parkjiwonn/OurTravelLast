package com.example.ourtravel.RecyclerView;

import com.example.ourtravel.retrofit.CompanyData;

public class HomeData {

   private String img;
   private String tx_title;
   private String tx_start;
   private String tx_finish;
   private String tx_main ;
   private String tx_sub;
   private String tx_company;
   private String tx_content;
   private int id;
   private String user_email;
   private String upload_date;
   private String attend;



    private int completion;
   private String time; // 채팅방 생성 시간


    private String tx_type;


    public HomeData(String img, String tx_title, String tx_start, String tx_finish, String tx_main, String tx_sub) {
        this.img = img;
        this.tx_title = tx_title;
        this.tx_start = tx_start;
        this.tx_finish = tx_finish;
        this.tx_main = tx_main;
        this.tx_sub = tx_sub;
    }

    public HomeData(CompanyData companyData) {
        this.img = companyData.getPhoto();
        this.tx_title = companyData.getTitle();
        this.tx_start = companyData.getStartdate();
        this.tx_finish = companyData.getFinishdate();
        this.tx_main = companyData.getMainspot();
        this.tx_sub = companyData.getSubspot();
        this.tx_company = companyData.getCompany();
        this.tx_content = companyData.getContent();
        this.tx_type = companyData.getType();
        this.id = companyData.getId();
        this.user_email = companyData.getUser_email();
        this.upload_date = companyData.getUpload_date();
        this.attend = companyData.getAttend();
        this.time =companyData.getTime();
        this.completion = companyData.getCompletion();
    }

    public int getCompletion() {
        return completion;
    }

    public void setCompletion(int completion) {
        this.completion = completion;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAttend() {
        return attend;
    }

    public void setAttend(String attend) {
        this.attend = attend;
    }

    public String getUpload_date() {
        return upload_date;
    }

    public void setUpload_date(String upload_date) {
        this.upload_date = upload_date;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTx_title() {
        return tx_title;
    }

    public void setTx_title(String tx_title) {
        this.tx_title = tx_title;
    }

    public String getTx_start() {
        return tx_start;
    }

    public void setTx_start(String tx_start) {
        this.tx_start = tx_start;
    }

    public String getTx_finish() {
        return tx_finish;
    }

    public void setTx_finish(String tx_finish) {
        this.tx_finish = tx_finish;
    }

    public String getTx_main() {
        return tx_main;
    }

    public void setTx_main(String tx_main) {
        this.tx_main = tx_main;
    }

    public String getTx_sub() {
        return tx_sub;
    }

    public void setTx_sub(String tx_sub) {
        this.tx_sub = tx_sub;
    }

    public String getTx_company() {
        return tx_company;
    }

    public void setTx_company(String tx_company) {
        this.tx_company = tx_company;
    }

    public String getTx_content() {
        return tx_content;
    }

    public void setTx_content(String tx_content) {
        this.tx_content = tx_content;
    }

    public String getTx_type() {
        return tx_type;
    }

    public void setTx_type(String tx_type) {
        this.tx_type = tx_type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }



}
