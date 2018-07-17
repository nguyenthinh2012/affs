package ultil;

import SqlConnector.ConnectDatabase;
import SqlConnector.SqlCommonDb;
import io.vertx.core.json.JsonObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ContactInfo {
    int id;
    String name;
    String phone;
    String date_of_birth;
    String email;
    int id_user_manager;
    int id_user_tvts;
    String call_id;
    String studentId;
    String divided_date;
    int status;
    String call_schedule;
    String start_time;
    String end_time;
    String answer_time;
    String link_down_record;
    String comment;
    int call_level;
    int current_level;

}
