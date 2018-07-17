package SqlConnector;

import io.vertx.core.json.JsonObject;
import org.jooq.*;
import org.jooq.impl.DSL;

public class SqlCreator {

    static DSLContext context = DSL.using(SQLDialect.MYSQL);
    public static String createSelectQuery(String table, JsonObject json){

        Object sql = context
                .select().from(table);

        int i = 0;
        for (String key : json.fieldNames()) {
            if (i == 0) {
                sql = ((SelectJoinStep) sql).where("'" +key +"'" + "=" + json.getString(key));
                i++;
            } else {
                sql = ((SelectConditionStep) sql).and(key +"=" +json.getString(key));
            }
        }

        return sql.toString();
    }



    public static void main(String[] args) {
    }
}
