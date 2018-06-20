package ultil;

import java.sql.Timestamp;

public class HistoryCallCts {
    String call_id;
    String mobile_phone;
    String datetime_response;
    String start_time;
    String end_time;
    int duration;
    int ring_time;
    String link_down_record;
    String comment;
    int call_level;
    String call_schedule;

    public String getStart_time(){
        return  start_time;
    }
    public String getCall_id() {
        return call_id;
    }

    public void setCall_id(String call_id) {
        this.call_id = call_id;
    }

//    public int getId_user() {
//        return id_user;
//    }
//
//    public void setId_user(int id_user) {
//        this.id_user = id_user;
//    }
//
//    public int getId_contact() {
//        return id_contact;
//    }
//
//    public void setId_contact(int id_contact) {
//        this.id_contact = id_contact;
//    }
//
//
//    public String getStation_id() {
//        return station_id;
//    }
//
//    public void setStation_id(String station_id) {
//        this.station_id = station_id;
//    }

    public String getMobile_phone() {
        return mobile_phone;
    }

    public void setMobile_phone(String mobile_phone) {
        this.mobile_phone = mobile_phone;
    }

//    public String getDatetime_response() {
//        return datetime_response;
//    }
//
//    public void setDatetime_response(String datetime_response) {
//        this.datetime_response = datetime_response;
//    }
//
//    public String getStart_time() {
//        return start_time;
//    }
//
//    public void setStart_time(String start_time) {
//        this.start_time = start_time;
//    }
//
//    public String getEnd_time() {
//        return end_time;
//    }
//
//    public void setEnd_time(String end_time) {
//        this.end_time = end_time;
//    }
//
//    public int getDuration() {
//        return duration;
//    }
//
//    public void setDuration(int duration) {
//        this.duration = duration;
//    }
//
//    public int getRing_time() {
//        return ring_time;
//    }
//
//    public void setRing_time(int ring_time) {
//        this.ring_time = ring_time;
//    }

    public String getLink_down_record() {
        return link_down_record;
    }

    public void setLink_down_record(String link_down_record) {
        this.link_down_record = link_down_record;
    }

//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public String getError_code() {
//        return error_code;
//    }
//
//    public void setError_code(String error_code) {
//        this.error_code = error_code;
//    }

}

