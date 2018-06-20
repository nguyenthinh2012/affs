package api;

import SqlConnector.ConnectDatabase;
import SqlConnector.SqlCommonDb;
import io.vertx.core.json.JsonObject;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class ImportCts {

    public static void main(String[] args) throws IOException, SQLException, InvalidFormatException {
        ConnectDatabase db = new ConnectDatabase();
        SqlCommonDb sql = new SqlCommonDb();

        HashMap<String, Integer> hm = new HashMap<>();
        hm.put("quyennt35", 40);
        hm.put("hangntt40", 41);
        hm.put("huonglt16", 42);

        Workbook workbook = WorkbookFactory.create(new File("D:\\cts.xlsx"));
        Iterator<Row> rowIterator = workbook.getSheetAt(0).rowIterator();
        rowIterator.next();
        while (rowIterator.hasNext()){
            Row r = rowIterator.next();
            String number = new java.text.DecimalFormat("0").format(r.getCell(3).getNumericCellValue());
//            String number = r.getCell(5).getStringCellValue();
            String name = r.getCell(2).getStringCellValue();
            String tvts_n = r.getCell(11).getStringCellValue();
            String email = r.getCell(4).getStringCellValue();
            String id = r.getCell(2).getStringCellValue();
//            System.out.println(number + " " + name + " " + tvts_n + " " + email);
            JsonObject obj = new JsonObject();
            obj.put("name", name);
            obj.put("phone", "84" + number);
            obj.put("studentId", id);
            obj.put("id_user_tvts", hm.get(tvts_n)+"");
            obj.put("date_of_birth", "1994-01-01");
            obj.put("email", email);
            obj.put("current_level", 0 + "");
            obj.put("status", 1 + "");
            db.autoInsert("contact", obj, sql.connectDb());
        }
        workbook.close();



    }
}
