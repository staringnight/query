package com.bank.query.bean;

import java.util.List;

public class GFFirstBean {

    /**
     * ec : 0
     * em :
     * syn : 0
     * cd : {"errorCode":"0","errorMsg":"","iApplyProgressListTemp":[{"userName":"钱彬斌","certNo":"320683199311133353","applyCardName":"188","processResult":"D","noPassReason":"","decideDate":"2017-02-08 12:59:09.272238","barcode":"null","resetApply":""},{"userName":"钱彬斌","certNo":"320683199311133353","applyCardName":"116","processResult":"D","noPassReason":"","decideDate":"2016-10-16 07:45:47.439474","barcode":"null","resetApply":""}],"asqEmpSid":"5f1989220da0504b630a1350030561628c52008660661595994309"}
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
        /**
         * errorCode : 0
         * errorMsg :
         * iApplyProgressListTemp : [{"userName":"钱彬斌","certNo":"320683199311133353","applyCardName":"188","processResult":"D","noPassReason":"","decideDate":"2017-02-08 12:59:09.272238","barcode":"null","resetApply":""},{"userName":"钱彬斌","certNo":"320683199311133353","applyCardName":"116","processResult":"D","noPassReason":"","decideDate":"2016-10-16 07:45:47.439474","barcode":"null","resetApply":""}]
         * asqEmpSid : 5f1989220da0504b630a1350030561628c52008660661595994309
         */

        private String errorCode;
        private String errorMsg;
        private String asqEmpSid;
        private List<IApplyProgressListTempBean> iApplyProgressListTemp;

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }

        public String getAsqEmpSid() {
            return asqEmpSid;
        }

        public void setAsqEmpSid(String asqEmpSid) {
            this.asqEmpSid = asqEmpSid;
        }

        public List<IApplyProgressListTempBean> getIApplyProgressListTemp() {
            return iApplyProgressListTemp;
        }

        public void setIApplyProgressListTemp(List<IApplyProgressListTempBean> iApplyProgressListTemp) {
            this.iApplyProgressListTemp = iApplyProgressListTemp;
        }

        public static class IApplyProgressListTempBean {
            /**
             * userName : 钱彬斌
             * certNo : 320683199311133353
             * applyCardName : 188
             * processResult : D
             * noPassReason :
             * decideDate : 2017-02-08 12:59:09.272238
             * barcode : null
             * resetApply :
             */

            private String userName;
            private String certNo;
            private String applyCardName;
            private String processResult;
            private String noPassReason;
            private String decideDate;
            private String barcode;
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

            public String getResetApply() {
                return resetApply;
            }

            public void setResetApply(String resetApply) {
                this.resetApply = resetApply;
            }
        }
    }
}
