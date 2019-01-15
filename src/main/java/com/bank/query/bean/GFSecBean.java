package com.bank.query.bean;

import java.util.List;

public class GFSecBean {

    /**
     * ec : 0
     * em :
     * syn : 0
     * cd : {"iApplyProgressListTemp":[{"userName":"钱*","certNo":"320683199311133353","applyCardName":"普通臻尚白金卡","processResult":"D","noPassReason":"综合评核未达开卡要求","decideDate":"","barcode":"2018年04月07日","barcode2":"00362018040701036978","resetApply":"0"},{"userName":"钱*","certNo":"320683199311133353","applyCardName":"极客虚拟卡","processResult":"D","noPassReason":"综合评核未达开卡要求","decideDate":"","barcode":"2017年02月08日","barcode2":"00362017020801040738","resetApply":"0"},{"userName":"钱*","certNo":"320683199311133353","applyCardName":"广发携程联名卡","processResult":"D","noPassReason":"综合评核未达开卡要求","decideDate":"","barcode":"2016年10月16日","barcode2":"00362016101601006696","resetApply":"0"},{"userName":"钱*","certNo":"320683199311133353","applyCardName":"普通臻尚白金卡","processResult":"D","noPassReason":"综合评核未达开卡要求","decideDate":"","barcode":"2016年06月28日","barcode2":"00362016062801030870","resetApply":"0"},{"userName":"钱*","certNo":"320683199311133353","applyCardName":"广发淘宝联名卡","processResult":"D","noPassReason":"","decideDate":"","barcode":"2016年03月21日","barcode2":"00362016032101014481","resetApply":"0"}]}
     */

    private String ec;
    private String em;
    private String syn;
    private CdBean cd;

    public String getEc() {
        return ec;
    }

    public void setEc(String ec) {
        this.ec = ec;
    }

    public String getEm() {
        return em;
    }

    public void setEm(String em) {
        this.em = em;
    }

    public String getSyn() {
        return syn;
    }

    public void setSyn(String syn) {
        this.syn = syn;
    }

    public CdBean getCd() {
        return cd;
    }

    public void setCd(CdBean cd) {
        this.cd = cd;
    }

    public static class CdBean {
        private List<IApplyProgressListTempBean> iApplyProgressListTemp;

        public List<IApplyProgressListTempBean> getIApplyProgressListTemp() {
            return iApplyProgressListTemp;
        }

        public void setIApplyProgressListTemp(List<IApplyProgressListTempBean> iApplyProgressListTemp) {
            this.iApplyProgressListTemp = iApplyProgressListTemp;
        }

        public static class IApplyProgressListTempBean {
            /**
             * userName : 钱*
             * certNo : 320683199311133353
             * applyCardName : 普通臻尚白金卡
             * processResult : D
             * noPassReason : 综合评核未达开卡要求
             * decideDate :
             * barcode : 2018年04月07日
             * barcode2 : 00362018040701036978
             * resetApply : 0
             */

            private String userName;
            private String certNo;
            private String applyCardName;
            private String processResult;
            private String noPassReason;
            private String decideDate;
            private String barcode;
            private String barcode2;
            private String resetApply;

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public String getCertNo() {
                return certNo;
            }

            public void setCertNo(String certNo) {
                this.certNo = certNo;
            }

            public String getApplyCardName() {
                return applyCardName;
            }

            public void setApplyCardName(String applyCardName) {
                this.applyCardName = applyCardName;
            }

            public String getProcessResult() {
                return processResult;
            }

            public void setProcessResult(String processResult) {
                this.processResult = processResult;
            }

            public String getNoPassReason() {
                return noPassReason;
            }

            public void setNoPassReason(String noPassReason) {
                this.noPassReason = noPassReason;
            }

            public String getDecideDate() {
                return decideDate;
            }

            public void setDecideDate(String decideDate) {
                this.decideDate = decideDate;
            }

            public String getBarcode() {
                return barcode;
            }

            public void setBarcode(String barcode) {
                this.barcode = barcode;
            }

            public String getBarcode2() {
                return barcode2;
            }

            public void setBarcode2(String barcode2) {
                this.barcode2 = barcode2;
            }

            public String getResetApply() {
                return resetApply;
            }

            public void setResetApply(String resetApply) {
                this.resetApply = resetApply;
            }
        }
    }
}
