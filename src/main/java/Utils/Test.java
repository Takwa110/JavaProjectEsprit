
package Utils;

import java.sql.Connection;

public class Test {
    public Test() {
    }

    public static void main(String[] args) {
        DataSource data1 = DataSource.getInstance();
        DataSource data2 = DataSource.getInstance();
        Connection con1 = DataSource.getConnection();
        Connection con2 = DataSource.getConnection();
        System.out.println("Connection 1: " + String.valueOf(con1));
        System.out.println("Connection 2: " + String.valueOf(con2));
        data1.isConnectionValid();
        data1.closeConnection();
    }
}
