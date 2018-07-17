package api;

import SqlConnector.ConnectDatabase;
import SqlConnector.SqlCommonDb;
import com.google.gson.Gson;
import com.mashape.unirest.http.Unirest;
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

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class AppApi extends AbstractVerticle {

    ConnectDatabase database;
    public static int port = 9090;
    public static final String serverIp = "http://171.244.3.242:9090";
    public static final String recordLinkPrefix = "http://171.244.3.242/records/";
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
        router.route(HttpMethod.GET,"/test").handler(this::test);

        //all Get data route
        router.route(HttpMethod.POST,"/getContact").handler(this::getContact);
        router.route(HttpMethod.POST,"/getStockInCts").handler(this::getStockInCts);
        router.route(HttpMethod.POST,"/getPendingCts").handler(this::getPendingCts);
        router.route(HttpMethod.POST,"/getAllHistoryCall").handler(this::getAllHistoryCall);
        router.route(HttpMethod.POST,"/getAccountNativeTest").handler(this::getAccountNativeTest);
        router.route(HttpMethod.POST,"/bookAccount").handler(this::bookAccount);

        // all Update data route
        router.route(HttpMethod.POST,"/updateContact").handler(this::updateContact);
        router.route(HttpMethod.POST,"/updateHistoryCall").handler(this::updateHistoryCall);


        router.route(HttpMethod.POST, "/scoreEvent").handler(this::getScoreAndInsert);


        vertx.createHttpServer().requestHandler(router::accept).listen(port);
        System.out.println("Listening at " +port);
    }


    public void test(RoutingContext r){
        System.out.println("xcbcvbvbb");
        sendResponse(r, "bvcbvcbvcb");
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
    public void updateHistoryCall(RoutingContext r){
        JsonObject json = r.getBodyAsJson();

        JsonObject contactJson = new JsonObject();
        contactJson.put("id", json.getString("id"));
        contactJson.put("name", json.getString("name"));
        contactJson.put("phone", json.getString("phone"));
        contactJson.put("email", json.getString("email"));
        contactJson.put("call_id", json.getString("call_id"));


        JsonObject historyJson = new JsonObject();
        historyJson.put("call_id", json.getString("call_id"));
        historyJson.put("phone", json.getString("phone"));
        if(json.containsKey("call_schedule")) {
            historyJson.put("call_schedule", json.getString("call_schedule"));
        }
        if(json.containsKey("comment")) {
            historyJson.put("comment", json.getString("comment"));
        }
        if(json.containsKey("call_level")) {
            historyJson.put("call_level", json.getString("call_level"));
            contactJson.put("current_level", json.getString("call_level"));
        }

        try{
            int res1 = database.updateQuery("history_call", "call_id", remove(historyJson),db.connectDb());
            int res = database.updateQuery("contact", "id", remove(contactJson),db.connectDb());
            if (res > 0 && res1 > 0){
                sendResponse(r, new Status(1));
            }else {
                sendResponse(r, new Status(0));
            }
            return;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        sendResponse(r, new Status(0));

    }




    //<editor-fold default-state = "collapsed" desc="????">
    public void getAllHistoryCall(RoutingContext r){

        try{
            List<HistoryCallCts> res = database.selectQuery(r.getBodyAsJson(), "history_call", HistoryCallCts.class, db.connectDb(), null);
            res.sort((a,b) ->{
                return string2Time(b.getStart_time()).compareTo(string2Time(a.getStart_time()));

            });
            sendData(res, r);
            return;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        sendResponse(r, "");

    }
    public void getPendingCts(RoutingContext r){

        try{

            JsonObject json = r.getBodyAsJson();
            List<String> tableJoin = new ArrayList<>();
            tableJoin.add("history_call");
            List<String> joinParam = new ArrayList<>();
            joinParam.add("call_id");
            List<String> op = new ArrayList<>();
            op.add("=");
            op.add("like");
            json.put("call_schedule", json.getString("call_schedule") + "%");
            List<ContactInfo> res = database.selectAndJoin("contact", json, tableJoin,
                    null, joinParam, db.connectDb(), ContactInfo.class, op, 0);
            sendData(res, r);
            return;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        sendResponse(r, "");
    }
    public void getStockInCts(RoutingContext r){

        try{
            List<String> tableJoin = new ArrayList<>();
            tableJoin.add("history_call");
            List<String> joinParam = new ArrayList<>();
            joinParam.add("call_id");
            JsonObject json = r.getBodyAsJson();
            json.put("status", 1 +"");
            List<ContactInfo> res = database.selectAndJoin("contact", json, tableJoin,
                    null, joinParam, db.connectDb(), ContactInfo.class, null, 0);
            sendData(res, r);
            return;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        sendResponse(r, "");
    }
    public void getContact(RoutingContext r){

        List<String> tableJoin = new ArrayList<>();
        tableJoin.add("history_call");
        List<String> joinParam = new ArrayList<>();
        joinParam.add("call_id");
        try {
            List<ContactInfo> cts =  database.selectAndJoin("contact", r.getBodyAsJson(), tableJoin,
                    null, joinParam, db.connectDb(), ContactInfo.class, null,0);
            sendData(cts, r);
            return;

        }
        catch (Exception e){
            e.printStackTrace();
        }

        sendResponse(r, "");


    }
    //<editor-fold>

    //can chinh sua bookAccount chuan hon.
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

    //can chinh sua lai thong tin account
    public void getAccountNativeTest(RoutingContext r){

        try{
            List<AccountNativeTest> al = database.selectQuery(r.getBodyAsJson(), "native_test", AccountNativeTest.class, db.connectDb(), null );
            sendData(al, r);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    // send data to client;
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

    public void getScoreAndInsert(RoutingContext r){

        JsonObject obj = r.getBodyAsJson();
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(System.currentTimeMillis() + "Score"));
            bw.write(obj.toString());
            bw.newLine();
            bw.close();
            JsonObject object = new JsonObject();
            object.put("status", true);
            object.put("msg", "Success");
            sendResponse(r, object);
        }
        catch (Exception e){
            e.printStackTrace();
            JsonObject object = new JsonObject();
            object.put("status", false);
            object.put("msg", "Fail");
            sendResponse(r, object);
        }


    }


    public void answer(RoutingContext r){
        HttpServerRequest request = r.request();
        JsonArray res = new JsonArray();
        JsonObject record = new JsonObject();
        JsonObject action = new JsonObject();
        record.put("action", "record");
        record.put("eventUrl", serverIp + "/record");
        res.add(record);
        action.put("action", "connect");
        action.put("eventUrl", serverIp + "/event");

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
        input.put("link_down_record", recordLinkPrefix +json.getString("call_id") +".mp3");
        input.put("call_id", json.getString("call_id"));
        try{

            int res = database.updateQuery("history_call", "call_id", input, db.connectDb());
            InputStream response = Unirest.post(json.getString("recording_url"))
                    .header("X-STRINGEE-AUTH", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImN0eSI6InN0cmluZ2VlLWFwaTt2PTEifQ.eyJqdGkiOiJTS04yUGZBOFBKNnNucXJwMlRzVnV2ZHo0T2N4NDRiMzRPLTE1Mjg1NDEyNTQiLCJpc3MiOiJTS04yUGZBOFBKNnNucXJwMlRzVnV2ZHo0T2N4NDRiMzRPIiwiZXhwIjoxNTMyMTQxMjU0LCJ1c2VySWQiOiJqaW1teSIsInJlc3RfYXBpIjp0cnVlLCJpYXQiOjE1Mjg1NDEyNTR9.SHCj_EVHrNFktlBlMai35JtX8IAxHfwKqw3l7HZ_6mo")
                    .header("rest_api", "true")
                    .asString().getRawBody();

            byte[] buffer = new byte[response.available()];
            response.read(buffer);
            File f = new File("/var/www/html/records/"+ json.getString("call_id") +".mp3");
            OutputStream out = new FileOutputStream(f);
            out.write(buffer);
            out.close();
            response.close();

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
        System.out.println("have Event");

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
                insertCallId.put("phone", num);
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                insertCallId.put("start_time",sdfDate.format(new Timestamp(json.getLong("timestamp_ms"))));
                insertCallId.put("call_id", call_id);
                try {
                    database.autoInsert("history_call", insertCallId, db.connectDb());
                    JsonObject tmp = new JsonObject();
                    tmp.put("call_id", insertCallId.getString("call_id"));
                    tmp.put("phone", num);
//                    database.updateQuery("contact", "phone", tmp, db.connectDb());

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


    //remove all fields don't have data.
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
    //send status when executed query;
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
