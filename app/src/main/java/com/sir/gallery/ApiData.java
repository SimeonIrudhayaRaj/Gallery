package com.sir.gallery;
public class ApiData {
    private String url;

    ApiData(String id,String owner,String secret,String server,String farm,String title,String ispublic,String isfriend,String isfamily,String url_s,String https,String  height_s,String width_s)
    {
        this.url=https;
    }
    String geturl()
    {
        return url;
    }
}
