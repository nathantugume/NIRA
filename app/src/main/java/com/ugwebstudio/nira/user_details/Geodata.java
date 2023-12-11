package com.ugwebstudio.nira.user_details;

import java.util.List;

public class Geodata {
    private String DISTRICT;
    private String CONSTITUENCY;
    private String SUBCOUNTY;
    private String PARISH;

    public class GeodataContainer {
        private List<Geodata> data;

        public List<Geodata> getData() {
            return data;
        }
    }
    @Override
    public String toString() {
        return "Geodata{" +
                "DISTRICT='" + DISTRICT + '\'' +
                ", CONSTITUENCY='" + CONSTITUENCY + '\'' +
                ", SUBCOUNTY='" + SUBCOUNTY + '\'' +
                ", PARISH='" + PARISH + '\'' +
                '}';
    }

    public String getDISTRICT() {
        return DISTRICT;
    }

    public void setDISTRICT(String DISTRICT) {
        this.DISTRICT = DISTRICT;
    }

    public String getCONSTITUENCY() {
        return CONSTITUENCY;
    }

    public void setCONSTITUENCY(String CONSTITUENCY) {
        this.CONSTITUENCY = CONSTITUENCY;
    }

    public String getSUBCOUNTY() {
        return SUBCOUNTY;
    }

    public void setSUBCOUNTY(String SUBCOUNTY) {
        this.SUBCOUNTY = SUBCOUNTY;
    }

    public String getPARISH() {
        return PARISH;
    }

    public void setPARISH(String PARISH) {
        this.PARISH = PARISH;
    }
}

