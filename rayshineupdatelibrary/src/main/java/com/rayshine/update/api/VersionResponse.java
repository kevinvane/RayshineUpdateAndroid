package com.rayshine.update.api;

public class VersionResponse {


    /**
     * statusCode : 400
     * data : {"id":34,"versionName":"v1.0.5","versionCode":5,"type":"apk","date":"2019-07-24T01:57:24.000+0000","size":2921820}
     */

    private int statusCode;
    private DataBean data;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 34
         * versionName : v1.0.5
         * versionCode : 5
         * type : apk
         * date : 2019-07-24T01:57:24.000+0000
         * size : 2921820.0
         */

        private int id;
        private String versionName;
        private int versionCode;
        private String type;
        private String date;
        private double size;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public double getSize() {
            return size;
        }

        public void setSize(double size) {
            this.size = size;
        }
    }
}
