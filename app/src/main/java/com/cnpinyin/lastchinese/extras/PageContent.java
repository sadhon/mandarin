package com.cnpinyin.lastchinese.extras;

/**
 * Created by User on 10/29/2017.
 */

public class PageContent {

    private String cnpinyin;
    private String engword;
    private String cnchar;
    private String soundfile;


    public PageContent(String cnpinyin, String engword, String cnchar, String soundfile){

        this.setCnpinyin(cnpinyin);
        this.setEngword(engword);
        this.setCnchar(cnchar);
        this.setSoundfile(soundfile);


    }

    public String getSoundfile() {
        return soundfile;
    }

    public void setSoundfile(String soundfile) {
        this.soundfile = soundfile;
    }

    public String getCnchar() {
        return cnchar;
    }


    public void setCnchar(String cnchar) {
        this.cnchar = cnchar;
    }

    public String getCnpinyin() {
        return cnpinyin;
    }

    public void setCnpinyin(String cnpinyin) {
        this.cnpinyin = cnpinyin;
    }

    public String getEngword() {
        return engword;
    }

    public void setEngword(String engword) {
        this.engword = engword;
    }
}
