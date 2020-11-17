import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;
import com.github.freva.asciitable.ColumnData;
import org.apache.calcite.adapter.jdbc.JdbcSchema;
import org.apache.calcite.schema.Schema;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.sql.*;
import java.util.*;

/**
 * -
 *
 * @date 2020/11/17
 * @author: hao.luo <hao.luo@china.zhaogang.com>
 */
public class TestCalcite {

    @Test
    public void multiSourceTest() {
        Properties config = new Properties();
        config.put("model", TestUtil.resourcePath("schema/test.json"));
        config.put("lex", "MYSQL");
        String sql = "select pkid,                                                  " +
                "        count(1) as cnt,                                           " +
                "        listagg(type) as type,                                     " +
                "        row_number() over() as rn,                                 " +
                "        first_value(pkid) over(partition by pkid) as first,        " +
                "        lead(pkid, 1) over() as n, lag(pkid, 1) over() as p        " +
                "     from (                                                        " +
                "        select pkid,                                               " +
                "            'account_info' as type                                 " +
                "        from x.account_info                                        " +
                "        union                                                      " +
                "        select pkid,                                               " +
                "            'inv_sum_ledger' as type                               " +
                "        from y.inv_sum_ledger                                      " +
                "     ) a group by pkid                                             ";
        try (Connection con = DriverManager.getConnection("jdbc:calcite:", config)) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            List<String[]> items = new ArrayList<>();
            String[] columns;
            while (rs.next()) {
                columns = new String[6];
                columns[0] = rs.getString("rn");
                columns[1] = rs.getString("pkid");
                columns[2] = rs.getString("cnt");
                columns[3] = rs.getString("type");
                columns[4] = rs.getString("n");
                columns[5] = rs.getString("p");
                items.add(columns);
            }
            String[][] rows = new String[items.size()][];
            items.toArray(rows);
            String[] headers = {"rn", "pkid", "cnt", "type", "n", "p"};
            System.out.println(AsciiTable.getTable(headers, rows));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
