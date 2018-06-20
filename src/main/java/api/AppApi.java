package api;

import SqlConnector.ConnectDatabase;
import SqlConnector.SqlCommonDb;
import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import ultil.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class AppApi extends AbstractVerticle {

    ConnectDatabase database;
    public static int port = 9090;
    SqlCommonDb db;

    @Override
    public void start() throws Exception {
        super.start();
        database = new ConnectDatabase();
        db = new SqlCommonDb();
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(CookieHandler.create());
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));
        router.route(HttpMethod.POST,"/login").handler(this::login);
        router.route(HttpMethod.POST,"/searchCts").handler(this::searchCts);
        router.route(HttpMethod.POST,"/event").handler(this::event);
        router.route(HttpMethod.POST,"/record").handler(this::record);
        router.route(HttpMethod.POST,"/testEvent").handler(this::testEvent);
        router.route(HttpMethod.GET,"/answer").handler(this::answer);


        router.route(HttpMethod.POST, "/getContact").handler(this::getContact);
        router.route(HttpMethod.POST, "/getLevel").handler(this::getLevel);
        router.route(HttpMethod.POST, "/getDashboard").handler(this::getDashboard);
        router.route(HttpMethod.POST, "/getStatusCall").handler(this::getStatusCall);
        router.route(HttpMethod.POST, "/getHistoryCall").handler(this::getHistoryCall);
        router.route(HttpMethod.POST, "/getPendingCts").handler(this::getPendingCts);
        router.route(HttpMethod.POST, "/getCtsDetails").handler(this::getCtsDetails);
        router.route(HttpMethod.POST, "/getStockInCts").handler(this::getStockInCts);
        router.route(HttpMethod.POST, "/getAllHistoryCall").handler(this::getAllHistoryCall);
        router.route(HttpMethod.POST, "/getContactPerDay").handler(this::getContactPerDay);
        router.route(HttpMethod.POST, "/getCallDetail").handler(this::getCallDetail);
        router.route(HttpMethod.POST, "/getAccountNativeTest").handler(this::getAccountNativeTest);

        router.route(HttpMethod.POST, "/updateLevelContact").handler(this::updateLevelContact);
        router.route(HttpMethod.POST, "/updateUser").handler(this::updateUser);
        router.route(HttpMethod.POST, "/updateHistoryCall").handler(this::updateHistoryCall);
        router.route(HttpMethod.POST, "/updateContact").handler(this::updateContact);


        router.route(HttpMethod.POST, "/insertHistoryCall").handler(this::insertHistoryCall);
        router.route(HttpMethod.POST, "/insertHistoryDivideCts").handler(this::insertHistoryDivideCts);
        router.route(HttpMethod.POST, "/insertStatusCall").handler(this::insertStatusCall);
        router.route(HttpMethod.POST, "/insertHistoryStatusChange").handler(this::insertHistoryStatusChange);
        router.route(HttpMethod.POST, "/insertHistoryLevel").handler(this::insertHistoryLevel);
        router.route(HttpMethod.POST, "/insertUser").handler(this::insertUser);
        router.route(HttpMethod.POST, "/insertLevel").handler(this::insertLevel);
        router.route(HttpMethod.POST, "/insertRole").handler(this::insertRole);
        router.route(HttpMethod.POST, "/insertContact").handler(this::insertContact);

        router.route(HttpMethod.POST, "/bookAccount").handler(this::bookAccount);


        vertx.createHttpServer().requestHandler(router::accept).listen(port);
    }


    public <T> void sendData(List<T> res, RoutingContext r){
        DataResponse data = new DataResponse();
        if(res.size() > 0){
            data.setStatus(1);
            data.setData(res);
        }
        else{
            data.setData(new ArrayList<>());
            data.setStatus(0);
        }
        sendResponse(r, data);
    }

    public void login(RoutingContext r){
        JsonObject json = r.getBodyAsJson();
        String user_name = json.getString("username");
        String pwd = json.getString("password");
        StringBuilder query = new StringBuilder("SELECT * from user where username = ?;");
        ArrayList<Object> params = new ArrayList<>();
        params.add(user_name);
        try {
            List<User> result = database.executeQuery(query, params, User.class, db.connectDb());
            if(result.size() == 1 && pwd.equals(result.get(0).getPassword())){
                DataResponse d = new DataResponse();
                d.setStatus(1);
                d.setData(result.get(0));
                sendResponse(r, d);
            }
            else {
                Message m = new Message();
                m.setMessage("Invalid user or wrong pwd!!");
                m.setStatus(0);
                sendResponse(r, m);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            sendResponse(r, "");
        }
    }

    public void searchCts(RoutingContext r){
        JsonObject json = r.getBodyAsJson();
        Iterator<String> it = json.fieldNames().iterator();
        it.next();
        String field = it.next();
        String query = "SELECT * FROM contact where id_user_tvts = ? and " + field + " like ?;";

        try {
            String s = new String(json.getString(field).getBytes(), "UTF-8");
            List<Object> params = new ArrayList<>();
            params.add(json.getString("id_user_tvts"));
            params.add(s);
            StringBuilder sb = new StringBuilder(query);
            List<ContactInfo> res = database.executeQuery(sb, params, ContactInfo.class, db.connectDb());
            sendData(res, r);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    public void getContactPerDay(RoutingContext r){

        try{

            List<String> op = new ArrayList<>();
            op.add("=");
            op.add("=");
            List<ContactInfo> res = database.selectQuery(r.getBodyAsJson(), "contact", ContactInfo.class, db.connectDb(),op );
            sendData(res, r);
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }
    public Timestamp string2Time(String s){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            Date parsedDate = dateFormat.parse(s);
            Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
            return timestamp;
        }
        catch (Exception e){

        }
        return null;
    }

    public void getAllHistoryCall(RoutingContext r){
        try{
            List<HistoryCallCts> res = database.selectQuery(r.getBodyAsJson(), "history_call", HistoryCallCts.class, db.connectDb(), null);
            res.sort((a,b) ->{

                return string2Time(b.getStart_time()).compareTo(string2Time(a.getStart_time()));

            });
            sendData(res, r);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void getCtsDetails(RoutingContext r){
        try {
            List<ContactInfo> res = database.selectQuery(r.getBodyAsJson(), "contact", ContactInfo.class, db.connectDb(), null);
            sendData(res, r);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getLevel(RoutingContext r){
        JsonObject json = new JsonObject();
        try {
            List<Level> res = database.selectQuery(json, "level", Level.class, db.connectDb(), null);
            sendData(res, r);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getStatusCall(RoutingContext r){
        JsonObject json = new JsonObject();
        try {
            List<StatusCall> res = database.selectQuery(json, "status_call", StatusCall.class, db.connectDb(), null);
            sendData(res, r);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getCallDetail(RoutingContext r){

        JsonObject object = r.getBodyAsJson();
        try{

            List<String> cols = new ArrayList<>();
            cols.add("history_call_schedule");
            cols.add("id_level_new");
            cols.add("comment");
            List<String> joinTable = new ArrayList<>();
            joinTable.add("history_status_change");
            List<String> joinParams = new ArrayList<>();
            joinParams.add("mobile_phone");
            List<CallDetail> res = database.selectAndJoin("history_call", object, joinTable, null,joinParams, db.connectDb(), CallDetail.class);
            sendData(res, r);
        }
        catch (Exception e){
            e.printStackTrace();
            sendResponse(null, r);
        }

    }


    public void insertContact(RoutingContext r){
        try {
            int res = database.autoInsert("contact", r.getBodyAsJson(), db.connectDb());
            sendResponse(r, new Status(res));
        }
        catch (Exception e){
            e.printStackTrace();
            sendResponse(r, new Status(-1));
        }
    }

    public void insertHistoryCall(RoutingContext r){
        try {
            int res = database.autoInsert("history_call", r.getBodyAsJson(), db.connectDb());
            sendResponse(r, new Status(res));
        }
        catch (Exception e){
            e.printStackTrace();
            sendResponse(r, new Status(-1));
        }
    }

    public void insertHistoryDivideCts(RoutingContext r){

        try {
            int res = database.autoInsert("history_divide_contact", r.getBodyAsJson(), db.connectDb());
            sendResponse(r, new Status(res));
        }
        catch (Exception e){
            e.printStackTrace();
            sendResponse(r, new Status(-1));
        }
    }

    public void insertStatusCall(RoutingContext r){
        try {
            int res = database.autoInsert("status_call", r.getBodyAsJson(), db.connectDb());
            sendResponse(r, new Status(res));
        }
        catch (Exception e){
            e.printStackTrace();
            sendResponse(r, new Status(-1));
        }
    }

    public void insertHistoryStatusChange(RoutingContext r){
        try {
            int res = database.autoInsert("history_status_change", r.getBodyAsJson(), db.connectDb());
            sendResponse(r, new Status(res));
        }
        catch (Exception e){
            e.printStackTrace();
            sendResponse(r, new Status(-1));
        }
    }

    public void insertHistoryLevel(RoutingContext r){
        try {
            JsonObject json = r.getBodyAsJson();
            json.put("time_created", new Date(System.currentTimeMillis()).toString());
            int res = database.autoInsert("history_level", json, db.connectDb());
            sendResponse(r, new Status(res));
        }
        catch (Exception e){
            e.printStackTrace();
            sendResponse(r, new Status(-1));
        }
    }

    public void insertLevel(RoutingContext r) {
        try {
            int res = database.autoInsert("level", r.getBodyAsJson(), db.connectDb());
            sendResponse(r, new Status(res));
        }
        catch (Exception e){
            e.printStackTrace();
            sendResponse(r, new Status(-1));
        }
    }

    public void insertRole(RoutingContext r) {
        try {
            int res = database.autoInsert("role", r.getBodyAsJson(), db.connectDb());
            sendResponse(r, new Status(res));
        }
        catch (Exception e){
            e.printStackTrace();
            sendResponse(r, new Status(-1));
        }
    }

    public void insertUser(RoutingContext r){
        JsonObject json = r.getBodyAsJson();
        ArrayList<Object> params = new ArrayList<>();
        params.add(json.getString("name"));
        params.add(json.getString("email"));
        params.add(json.getString("id_role"));
        params.add(json.getString("username"));
        params.add(json.getString("password"));
        params.add(json.getString("statition_default"));
        if(json.containsKey("statition_extend")){
            params.add(json.getString("statition_extend"));
        }
        else{
            params.add(null);
        }

        StringBuilder query = new StringBuilder("INSERT INTO user (name, email, id_role," +
                " username, password, statition_default, statition_extend) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?);");
        try{
            int res = database.insertQuery(query, params, db.connectDb());
            sendResponse(r, new Status(res));
        }
        catch (Exception e){
            e.printStackTrace();
            sendResponse(r, new Status(-1));
        }

    }



    public void getStockInCts(RoutingContext r){

        JsonObject json = r.getBodyAsJson();
        json.put("last_call", "0");
        try{
            List<ContactInfo> res = database.selectQuery(json, "contact", ContactInfo.class, db.connectDb(), null);
            sendData(res, r);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void getAccountNativeTest(RoutingContext r){

        try{
            List<AccountNativeTest> al = database.selectQuery(r.getBodyAsJson(), "native_test", AccountNativeTest.class, db.connectDb(), null );
            sendData(al, r);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getPendingCts(RoutingContext r){

        JsonObject json = r.getBodyAsJson();

        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = dateFormat.parse(json.getString("last_call_schedule"));
            long tomorow = parsedDate.getTime() + 86400000;
            parsedDate = new Date(tomorow);
            Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
            List<String> op = new ArrayList<>();
            StringBuilder sb = new StringBuilder("select * from contact where id_user_tvts = ? and last_call_schedule >= ? and last_call_schedule < ?");
            List<Object> params = new ArrayList<>();
            params.add(json.getString("id_user_tvts"));
            params.add(json.getString("last_call_schedule"));
            params.add(timestamp.toString());
            List<ContactInfo> res = database.executeQuery(sb, params, ContactInfo.class, db.connectDb());
            sendData(res, r);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void getContact(RoutingContext r){
        JsonObject json = r.getBodyAsJson();
        try {
            List<ContactInfo> res = database.selectQuery(json, "contact", ContactInfo.class, db.connectDb(), null);
            sendData(res, r);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getDashboard(RoutingContext r){
        JsonObject json = r.getBodyAsJson();
        try{
            String query = "SELECT count(current_level) as num, current_level FROM contact where id_user_tvts = ? group by current_level;";
            List<Object> params = new ArrayList<>();
            params.add(json.getString("id_user_tvts"));
            StringBuilder builder = new StringBuilder(query);
            List<NumCtsLevel> res = database.executeQuery(builder, params, NumCtsLevel.class, db.connectDb());
            sendData(res, r);
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    public void getHistoryCall(RoutingContext r){
        JsonObject json = r.getBodyAsJson();
        ArrayList<Object> params = new ArrayList<>();
        params.add(json.getString("id_user"));
        params.add(json.getString("id_contact"));
        StringBuilder query = new StringBuilder("Select * from history_call where id_user = ? and id_contact = ?;");
        try {

            List<HistoryCallCts> res = database.executeQuery(query, params, HistoryCallCts.class, db.connectDb());
            sendData(res, r);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateContact(RoutingContext r){
        try{
            int res = database.updateQuery("contact", "id", remove(r.getBodyAsJson()),db.connectDb());
            sendResponse(r, new Status(res));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateUser(RoutingContext r){
        try{
            int res = database.updateQuery("user", "username", r.getBodyAsJson(), db.connectDb());
            sendResponse(r, new Status(res));
        }
        catch (Exception e){
            e.printStackTrace();
            sendResponse(r, new Status(-1));

        }
    }

    public void updateLevelContact(RoutingContext r){
        try{
            JsonObject json = r.getBodyAsJson();
            JsonObject update = new JsonObject();
            String time_change = new Date(System.currentTimeMillis()).toString();
            update.put("id", json.getValue("id_contact"));
            update.put("current_level", json.getValue("id_level_new"));
            update.put("id_status_call", json.getValue("id_status_new"));
            int res = database.updateQuery("contact", "id", update, db.connectDb());
            if(res > 0){
                JsonObject status_change = new JsonObject();
                status_change.put("id_contact", json.getValue("id_contact"));
                status_change.put("id_level_old", json.getValue("id_level_old"));
                status_change.put("id_status_old", json.getValue("id_status_old"));
                status_change.put("id_level_new", json.getValue("id_level_new"));
                status_change.put("id_status_new", json.getValue("id_status_new"));
                status_change.put("comment", json.getValue("comment"));
                status_change.put("id_user_change", json.getValue("id_user_change"));
                status_change.put("time_change",time_change );
                res = database.autoInsert("history_status_change", status_change, db.connectDb());
            }
            else {
                sendResponse(r, new Status(res));
                return;
            }
            if(res > 0){
                if(!json.getString("id_level_old").equals(json.getString("id_level_new"))){
                    JsonObject historyLevel = new JsonObject();
                    historyLevel.put("id_contact", json.getValue("id_contact"));
                    historyLevel.put("id_level", json.getValue("id_level_new"));
                    historyLevel.put("time_created", time_change);
                    res = database.autoInsert("history_level", historyLevel, db.connectDb());
                }
            }
            else{

            }
            sendResponse(r, new Status(res));
        }
        catch (Exception e){
            e.printStackTrace();
            sendResponse(r, new Status(-1));

        }
    }

    public void updateHistoryCall(RoutingContext r){

        JsonObject json = r.getBodyAsJson();
        JsonObject jsonContact = new JsonObject();
        JsonObject jsonHistoryCall = new JsonObject();

        jsonContact.put("id",json.getString("id"));
        jsonContact.put("name",json.getString("name"));
        jsonContact.put("email",json.getString("email"));
        jsonContact.put("current_level",json.getString("call_level"));
        jsonContact.put("last_comment", json.getString("comment"));
        jsonContact.put("last_call_schedule", json.getString("call_schedule"));

        jsonHistoryCall.put("call_id",json.getString("call_id"));
        jsonHistoryCall.put("call_schedule",json.getString("call_schedule"));
        jsonHistoryCall.put("comment",json.getString("comment"));
        jsonHistoryCall.put("call_level",json.getString("call_level"));

        try{
            int res = database.updateQuery("contact", "id", remove(jsonContact),db.connectDb());
            int res1 = database.updateQuery("history_call", "call_id", remove(jsonHistoryCall),db.connectDb());
            if (res > 0 && res1 > 0){
                sendResponse(r, new Status(1));
            }else {
                sendResponse(r, new Status(0));
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }


    }


    public void answer(RoutingContext r){
        HttpServerRequest request = r.request();
        JsonArray res = new JsonArray();
        JsonObject record = new JsonObject();
        JsonObject action = new JsonObject();
        record.put("action", "record");
        record.put("eventUrl", "http://45.124.94.45:9090/record");
        res.add(record);
        action.put("action", "connect");
        action.put("eventUrl", "http://45.124.94.45:9090/event");

        JsonObject number = new JsonObject();
        number.put("type","external");
        number.put("number", request.getParam("from"));
        number.put("alias", request.getParam("from"));
        action.put("from", number);

        number = new JsonObject();
        number.put("type", "external");
        number.put("number",request.getParam("to"));
        number.put("alias", request.getParam("to"));

        action.put("to", number);
        res.add(action);

        HttpServerResponse response = r.response();
        response.putHeader("content-type", "application/json; charset=utf-8");
        response.end(res.toString());

    }

    public void record(RoutingContext r){

        JsonObject json = r.getBodyAsJson();
        JsonObject input = new JsonObject();
        input.put("link_down_record", json.getString("recording_url"));
        input.put("call_id", json.getString("call_id"));
        try{

            int res = database.updateQuery("history_call", "call_id", input, db.connectDb());
            sendResponse(r, res);

        }
        catch (Exception e){
            e.printStackTrace();
            sendResponse(r, 0);
        }

    }

    public void testEvent(RoutingContext r){

        JsonObject json = r.getBodyAsJson();
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("test" + System.currentTimeMillis()));
            if(json != null){
                bw.write(json.toString());
            }
            bw.close();
        }
        catch (Exception e){

        }

        HttpServerResponse response = r.response();
        response.putHeader("content-type", "application/json; charset=utf-8");
        response.end("");


    }

    public void event(RoutingContext r){

        JsonObject json = r.getBodyAsJson();
//        try {
//            BufferedWriter bw = new BufferedWriter(new FileWriter(System.currentTimeMillis()+""));
//            bw.write(json.toString());
//            bw.newLine();
//            bw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        if(json.containsKey("call_status")) {
            String status = json.getString("call_status");
            if (status.equals("answered")) {
                String num = json.getJsonObject("to").getString("number");
                String call_id = json.getString("call_id");
                JsonObject insertCallId = new JsonObject();
                insertCallId.put("mobile_phone", num);
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                insertCallId.put("start_time",sdfDate.format(new Timestamp(json.getLong("timestamp_ms"))));
                insertCallId.put("call_id", call_id);
                try {
                    database.autoInsert("history_call", insertCallId, db.connectDb());
                    JsonObject tmp = new JsonObject();
                    tmp.put("last_call", insertCallId.getString("start_time"));
                    tmp.put("phone", num);
                    database.updateQuery("contact", "phone", tmp, db.connectDb());

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            if(status.equals("ended")){
                String call_id = json.getString("call_id");
                try {
                    String select = "Select call_id from history_call where call_id = ?";
                    PreparedStatement stmt = db.connectDb().prepareStatement(select);
                    stmt.setObject(1, call_id);
                    ResultSet set = stmt.executeQuery();
                    if(set.next()){
                        JsonObject updateCallId = new JsonObject();
                        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        updateCallId.put("end_time",sdfDate.format(new Timestamp(json.getLong("timestamp_ms"))));
                        updateCallId.put("call_id", call_id);
                        database.updateQuery("history_call", "call_id",updateCallId, db.connectDb());
                    }
                    stmt.close();
                    set.close();

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        HttpServerResponse response = r.response();
        response.putHeader("content-type", "application/json; charset=utf-8");
        response.end("");
    }

    public void bookAccount(RoutingContext r){

        JsonObject idcts = r.getBodyAsJson();
        JsonObject obj = new JsonObject();
        obj.put("isBook", 0+"");
        try {
            List<AccountNativeTest> account = database.selectQuery(obj, "native_test", AccountNativeTest.class, db.connectDb(), null);
            if(account.size() > 0){
                AccountNativeTest a = account.get(0);

                JsonObject update = new JsonObject();
                update.put("isBook", 1+"");
                update.put("native_test_account", a.getNative_test_account());
                update.put("id_contact", idcts.getString("id_contact"));
                database.updateQuery("native_test", "native_test_account",update, db.connectDb());
                List<AccountNativeTest> al = new ArrayList<>();
                al.add(a);
                sendData(al, r);
            }
            else {
                JsonObject res = new JsonObject();
                res.put("status", 0);
                res.put("message", "Do not have account test!");
                sendResponse(r, res);
            }
        }
        catch (Exception e){
            e.printStackTrace();

        }


    }

    public JsonObject remove(JsonObject json){
        Set<String> fields = json.fieldNames();
        Object[] arr = fields.toArray();
        for(int i = 0 ; i < arr.length ; i ++){
            if(json.getString(arr[i].toString()).length() <1){
                json.remove(arr[i].toString());
            }
        }
        return  json;
    }

    public void sendResponse(RoutingContext r, Object rs){
        HttpServerResponse response = r.response();
        response.putHeader("content-type", "application/json; charset=utf-8");
        response.end(new Gson().toJson(rs));
    }


    public static void main(String[] args) {
        try {
            Runner.runExample(AppApi.class);
            System.out.println("Server listening at " + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
