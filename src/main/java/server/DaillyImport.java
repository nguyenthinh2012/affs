package server;

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
import io.vertx.core.json.JsonObject;
import ultil.User;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class DaillyImport {

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
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(transport, factory, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(path))).setAccessType("offline").build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public static int getId(String s) {
        JsonObject obj = new JsonObject();
        obj.put("name", s);
        try {
            List<User> users = db.selectQuery(obj, "user", User.class, sql.connectDb(), null);
            if (users.size() > 0) {
                User u = users.get(0);
                return u.getId();
            } else {
                return -1;
            }
        } catch (Exception e) {
            return 0;
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {

        Properties p = new Properties();
        InputStream is = new FileInputStream(new File("resources/config.properties"));
        p.load(is);
        is.close();
        long time = Long.parseLong(p.getProperty("time"));
//        System.out.println(time);
        while (true) {
            Thread.sleep(10000);
            if (time + 300000 < System.currentTimeMillis()) {
                time = time + 300000;
                p.setProperty("time", time + "");
                OutputStream os = new FileOutputStream("resources/config.properties");
                p.store(os, null);
//                Thread.sleep(10000);
                try {
                    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                    final String spreadsheetId = "1R3ivtGIw_8s_tOMvlRSfrFVo6bKn5kNd5Ovi_05s_s4";
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    Date d = new Date(time);
                    String range = "Real_Time_Import_Cts";
                    Sheets service = new Sheets.Builder(HTTP_TRANSPORT, factory, getCredential(HTTP_TRANSPORT))
                            .setApplicationName(App_name)
                            .build();
//                    System.out.println(range);
                    ValueRange response = service.spreadsheets().values()
                            .get(spreadsheetId, range)
                            .execute();
                    List<List<Object>> values = response.getValues();
                    HashMap<String, Integer> usersID = new HashMap<>();
                    if (values == null || values.isEmpty()) {
                        System.out.println("No data found.");
                    } else {

                        int i = 0;
                        for (List row : values) {
                            if (i == 0) {
                                i++;
                                continue;
                            }
                            String userName = row.get(13).toString().toLowerCase();
                            int userId = getId(userName);
                            if (userId > 0) {
                                usersID.put(userName, userId);
                            } else {
                                if (userId < 0) {
                                    JsonObject obj = new JsonObject();
                                    obj.put("username", userName);
                                    obj.put("name", userName);
                                    obj.put("id_role", "2");
                                    obj.put("password", "topica123");
                                    obj.put("email", userName + "@topica.edu.vn");
                                    try {
                                        db.autoInsert("user", obj, sql.connectDb());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    userId = getId(userName);
                                    if (userId > 0) {
                                        usersID.put(userName, userId);
                                    }

                                }
                            }
                            try {
                                String name = row.get(2).toString();
                                String number = row.get(3).toString();
                                String tvts_n = row.get(13).toString().toLowerCase();
                                String email = row.get(4).toString();
                                String id = row.get(1).toString();
                                JsonObject obj = new JsonObject();
                                obj.put("name", name);
                                obj.put("phone", "84" + number);
                                obj.put("id_user_tvts", usersID.get(tvts_n) + "");
                                obj.put("date_of_birth", "1994-01-01");
                                obj.put("email", email);
                                obj.put("studentId", id);
                                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                obj.put("divided_date", sdfDate.format(time));
//                                obj.put("current_level", 0 + "");
                                obj.put("status", 1 + "");
                                db.autoInsert("contact", obj, sql.connectDb());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        List<Request> requests = new ArrayList<>();
                        requests.add(new Request().setDeleteSheet(new DeleteSheetRequest().setSheetId(1)));
                        BatchUpdateSpreadsheetRequest res = new BatchUpdateSpreadsheetRequest().setRequests(requests);
                        service.spreadsheets().batchUpdate(spreadsheetId, res).execute();

                        requests.clear();

                        requests.add(new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties()
                                .setSheetId(1).setIndex(1).setTitle("Real_Time_Import_Cts"))));
                        res = new BatchUpdateSpreadsheetRequest().setRequests(requests);
                        service.spreadsheets().batchUpdate(spreadsheetId, res).execute();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(new Date(time) + " Khong co contacts!!!! ");
                }

            }

        }
    }
}
