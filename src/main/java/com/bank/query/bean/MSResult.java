package com.bank.query.bean;

import java.util.List;

public class MSResult {

    /**
     * retCode : 0000
     * msg : 成功获取列表信息！
     * data : {"FOUT":"1","list":[{"sBusiDept":"8312050238","Add11":"民生中国风主题卡","MscSrc":"拒绝","sLostDate":"2018-11-08","CardNo":"","cMaFlag":"主卡"}]}
     */

    private String retCode;
    private String msg;
    private DataBean data;

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * FOUT : 1
         * list : [{"sBusiDept":"8312050238","Add11":"民生中国风主题卡","MscSrc":"拒绝","sLostDate":"2018-11-08","CardNo":"","cMaFlag":"主卡"}]
         */

        private String FOUT;
        private List<ListBean> list;

        public String getFOUT() {
            return FOUT;
        }

        public void setFOUT(String FOUT) {
            this.FOUT = FOUT;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * sBusiDept : 8312050238
             * Add11 : 民生中国风主题卡
             * MscSrc : 拒绝
             * sLostDate : 2018-11-08
             * CardNo :
             * cMaFlag : 主卡
             */

            private String sBusiDept;
            private String Add11;
            private String MscSrc;
            private String sLostDate;
            private String CardNo;
            private String cMaFlag;

            public String getSBusiDept() {
                return sBusiDept;
            }

            public void setSBusiDept(String sBusiDept) {
                this.sBusiDept = sBusiDept;
            }

            public String getAdd11() {
                return Add11;
            }

            public void setAdd11(String Add11) {
                this.Add11 = Add11;
            }

            public String getMscSrc() {
                return MscSrc;
            }

            public void setMscSrc(String MscSrc) {
                this.MscSrc = MscSrc;
            }

            public String getSLostDate() {
                return sLostDate;
            }

            public void setSLostDate(String sLostDate) {
                this.sLostDate = sLostDate;
            }

            public String getCardNo() {
                return CardNo;
            }

            public void setCardNo(String CardNo) {
                this.CardNo = CardNo;
            }

            public String getCMaFlag() {
                return cMaFlag;
            }

            public void setCMaFlag(String cMaFlag) {
                this.cMaFlag = cMaFlag;
            }
        }
    }
}
