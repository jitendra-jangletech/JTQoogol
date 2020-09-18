package com.jangletech.qoogol.model;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

public class MasterResponse {

    private int Response;
    private String Message;

    public int getResponse() {
        return Response;
    }

    public void setResponse(int response) {
        Response = response;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    @SerializedName("61")
    private List<Master> masterList;

    public List<Master> getMasterList() {
        return masterList;
    }

    public void setMasterList(List<Master> masterList) {
        this.masterList = masterList;
    }

    public class Master{

        @SerializedName(Constant.mdt_type_id)
        private String mdt_type_id;

        @SerializedName(Constant.mdt_id)
        private String mdt_id = "273";

        @SerializedName(Constant.mdt_desc)
        private String mdt_desc = "276";

        public String getMdt_type_id() {
            return mdt_type_id;
        }

        public void setMdt_type_id(String mdt_type_id) {
            this.mdt_type_id = mdt_type_id;
        }

        public String getMdt_id() {
            return mdt_id;
        }

        public void setMdt_id(String mdt_id) {
            this.mdt_id = mdt_id;
        }

        public String getMdt_desc() {
            return mdt_desc;
        }

        public void setMdt_desc(String mdt_desc) {
            this.mdt_desc = mdt_desc;
        }
    }
}
