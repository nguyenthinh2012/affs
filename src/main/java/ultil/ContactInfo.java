package ultil;

import java.sql.Date;
import java.sql.Timestamp;

public class ContactInfo {
    int id;
    String name;
    String phone;
    String date_of_birth;
    String email;
    int id_user_manager;
    int id_user_tvts;
    int id_status_call;
    int status;
    int current_level;
    String last_call_schedule;
    String last_comment;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId_user_manager() {
        return id_user_manager;
    }

    public void setId_user_manager(int id_user_manager) {
        this.id_user_manager = id_user_manager;
    }

    public int getId_user_tvts() {
        return id_user_tvts;
    }

    public void setId_user_tvts(int id_user_tvts) {
        this.id_user_tvts = id_user_tvts;
    }

    public int getId_status_call() {
        return id_status_call;
    }

    public void setId_status_call(int id_status_call) {
        this.id_status_call = id_status_call;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCurrent_level() {
        return current_level;
    }

    public void setCurrent_level(int current_level) {
        this.current_level = current_level;
    }
}
