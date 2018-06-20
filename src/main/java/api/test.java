package api;

import SqlConnector.ConnectDatabase;
import SqlConnector.SqlCommonDb;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.vertx.core.json.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class test {


    public static void main(String[] args) throws UnirestException, SQLException {



        ConnectDatabase db = new ConnectDatabase();
        SqlCommonDb sql = new SqlCommonDb();

        try {
            Scanner sn = new Scanner(new File("D:\\account.csv"));
            sn.nextLine();
            while (sn.hasNextLine()){
                String line[] = sn.nextLine().split(",");
                JsonObject obj  = new JsonObject();
                if(line.length>0){
                    obj.put("native_test_account", line[0]);
                    obj.put("password", line[1]);
                    obj.put("isBook", 0+"");
                    db.autoInsert("native_test", obj, sql.connectDb());
                }

            }




        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

//        String select = "Select id, phone from contact";
//        ConnectDatabase db = new ConnectDatabase();
//        SqlCommonDb sql  = new SqlCommonDb();
//        PreparedStatement stmt = sql.connectDb().prepareStatement(select);
//
////        ResultSet set = stmt.executeQuery();
////        while (set.next()){
////            String id = set.getString("id");
//            String phone = "916488495";//set.getString("phone");
//            HttpResponse response = Unirest.post("http://45.124.95.15/api/contact/info")
//                    .header("key", "dHV5ZW5wdjJ0b3BpY2FAdG9waWNhQDEyMy5lZHUu")
//                    .header("content-type", "application/json")
//                    .header("authorization", "Basic dHV5ZW5wdjI6dG9waWNhLnR1eWVucHYy")
//                    .body("{\n \"phone\": \"" + phone + "\"\n}\n").asString();
//            String[] str = response.getBody().toString().split("\n");
//            try {
//                JsonObject object = new JsonObject(str[str.length - 1].substring(6));
//                String studentId = object.getJsonObject("data").getString("studentId");
//                System.out.println(studentId);
//                String isVip = object.getJsonObject("data").getString("isVip");
//                JsonObject obj = new JsonObject();
//                obj.put("studentId", studentId);
//                obj.put("isVip", isVip);
////                obj.put("id", id);
////                db.updateQuery("contact", id, obj, sql.connectDb());
//            }
//            catch (Exception e){
//                e.printStackTrace();
//            }
////        }




    }

}
