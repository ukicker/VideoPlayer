package com.connectsdk.external;

/**
 * @PackageName : com.connectsdk.as
 * @File : CCastBean.java
 * @Date : 2021/12/30 2021/12/30
 * @Author : K
 * @E-mail : vip@devkit.vip
 * @Version : V 1.0
 * @Describe ：
 */
public class CustomCastBean {


    private String path;
    private String mimeType;
    private @MediaType int mediaType;
    private String title;
    private String description;
    private boolean loopMode;

    public String getPath() {
        return path;
    }

    public String getMimeType() {
        return mimeType;
    }


    public int getMediaType() {
        return mediaType;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isLoopMode() {
        return loopMode;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLoopMode(boolean loopMode) {
        this.loopMode = loopMode;
    }

    public static  class Builder {
        private String path;
        private String mimeType;
        private int mediaType;
        private String title;
        private String description;
        private boolean loopMode;

        public Builder() {
        }

        public static Builder aCustomCastBean() {
            return new Builder();
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Builder setMimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public Builder setMediaType(@MediaType int mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setLoopMode(boolean loopMode) {
            this.loopMode = loopMode;
            return this;
        }

        public CustomCastBean build() {
            CustomCastBean customCastBean = new CustomCastBean();
            customCastBean.mediaType = this.mediaType;
            customCastBean.mimeType = this.mimeType;
            customCastBean.title = this.title;
            customCastBean.path = this.path;
            customCastBean.description = this.description;
            customCastBean.loopMode = this.loopMode;
            return customCastBean;
        }
    }
}
