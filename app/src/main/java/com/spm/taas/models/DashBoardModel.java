package com.spm.taas.models;

import com.spm.taas.R;

/**
 * Created by saikatpakira on 10/10/16.
 */

public class DashBoardModel {

    private String subjectName = "",subjectCount="0", headerName = "",headerCount="0";
    private boolean isHeader = false;
    private int headerColor = R.color.problems_new;


    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectCount() {
        return subjectCount;
    }

    public void setSubjectCount(String subjectCount) {
        this.subjectCount = subjectCount;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public String getHeaderCount() {
        return headerCount;
    }

    public void setHeaderCount(String headerCount) {
        this.headerCount = headerCount;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public int getHeaderColor() {
        return headerColor;
    }

    public void setHeaderColor(int headerColor) {
        this.headerColor = headerColor;
    }
}
