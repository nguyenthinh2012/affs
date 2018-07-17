package api;

import SqlConnector.ConnectDatabase;
import SqlConnector.SqlCommonDb;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.vertx.core.json.JsonObject;
import ultil.HistoryCallCts;

import java.io.*;
import java.security.GeneralSecurityException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class test {

    public static final String recordLinkPrefix = "http://171.244.3.242/records/";
    static final String App_name = "Java";
    static final String path = "resources";
    static final JacksonFactory factory = JacksonFactory.getDefaultInstance();
    static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    static final String file = "resources/client_secret.json";
    static ConnectDatabase db = new ConnectDatabase();
    static SqlCommonDb sql = new SqlCommonDb();
    static Credential getCredential(final NetHttpTransport transport) throws IOException {

        InputStream is = new FileInputStream(new File(file));
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(factory, new InputStreamReader(is));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(transport, factory, clientSecrets,SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(path))).setAccessType("offline").build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public static void main(String[] args) throws UnirestException, SQLException {

//        String s ="thịnh";
//        System.out.println(s + " " +Normalizer.normalize(s, Normalizer.Form.NFD).length());
//        System.out.println(s + " " +Normalizer.normalize(s, Normalizer.Form.NFC).length());

//        ConnectDatabase db = new ConnectDatabase();
//        SqlCommonDb sql = new SqlCommonDb();
//
//
//        List<HistoryCallCts> list = db.selectQuery(new JsonObject(),"history_call", HistoryCallCts.class, sql.connectDb(), null);
//        System.out.println(list.size());
//        for(HistoryCallCts cts :list){
//            String link = recordLinkPrefix + cts.call_id + ".mp3";
//            JsonObject object = new JsonObject();
//            object.put("link_down_record", link);
//            object.put("call_id", cts.call_id);
//            db.updateQuery("history_call","call_id", object,sql.connectDb());
//        }
        try {

            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            final String spreadsheetId = "1R3ivtGIw_8s_tOMvlRSfrFVo6bKn5kNd5Ovi_05s_s4";
            Sheets service = new Sheets.Builder(HTTP_TRANSPORT, factory, getCredential(HTTP_TRANSPORT))
                    .setApplicationName(App_name)
                    .build();


            List<Request> requests = new ArrayList<>();
            requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setSheetId(1).setIndex(1).setTitle("Real_Time_Import_Cts"))));
            BatchUpdateSpreadsheetRequest res = new BatchUpdateSpreadsheetRequest().setRequests(requests);
            BatchUpdateSpreadsheetResponse response = service.spreadsheets().batchUpdate(spreadsheetId, res).execute();
            System.out.println(response.getReplies());

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



//
//        try {
//            Scanner sn = new Scanner(new File("D:\\account.csv"));
//            sn.nextLine();
//            while (sn.hasNextLine()){
//                String line[] = sn.nextLine().split(",");
//                JsonObject obj  = new JsonObject();
//                if(line.length>0){
//                    obj.put("native_test_account", line[0]);
//                    obj.put("password", line[1]);
//                    obj.put("isBook", 0+"");
//                    db.autoInsert("native_test", obj, sql.connectDb());
//                }
//
//            }
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

    }

}
