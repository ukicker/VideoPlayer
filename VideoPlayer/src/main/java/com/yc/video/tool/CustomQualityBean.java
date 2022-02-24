package com.yc.video.tool;

/**
 * @PackageName : com.yc.video.tool
 * @File : CustomQueryBean.java
 * @Date : 2021/10/28 2021/10/28
 * @Author : K
 * @E-mail : vip@devkit.vip
 * @Version : V 1.0
 * @Describe ：
 */
public class CustomQualityBean implements IQualityName{

    private String name;
    private String qualityName;
    private boolean select;
    private String url;

    public void setQualityName(String qualityName) {
        this.qualityName = qualityName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public CustomQualityBean(String name, String qualityName, boolean select) {
        this.name = name;
        this.qualityName = qualityName;
        this.select = select;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Override
    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    @Override
    public String getQualityName() {
        return qualityName;
    }
}
