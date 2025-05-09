package com.noface;

import java.sql.*;
import java.util.BitSet;
import java.util.Map;

public class DatabaseUtil {

    private final Connection connection;
    private final PreparedStatement insertStatement;
    private static final int BATCH_SIZE = 10_000;
    private final String jdbcUrl = "jdbc:mysql://localhost:3306/rubik?useSSL=false&serverTimezone=UTC";
    private final String username = "root";
    private final String password = "123456";
    public DatabaseUtil() throws SQLException {

        this.connection = DriverManager.getConnection(jdbcUrl, username, password);
        this.connection.setAutoCommit(false);  // tăng hiệu suất
        createTableIfNotExists();

        String sql = "INSERT INTO corner_pattern (state, depth) VALUES (?, ?)";
        this.insertStatement = connection.prepareStatement(sql);
    }

    private void createTableIfNotExists() throws SQLException {
        String ddl = """
            CREATE TABLE IF NOT EXISTS corner_pattern (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                state BLOB NOT NULL,
                depth INT NOT NULL
            );
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(ddl);
            connection.commit();
            System.out.println("✅ Bảng `bitset_data` đã được tạo (nếu chưa tồn tại).");
        }
    }

    private byte[] bitSetToBytes(BitSet bitSet) {
        return bitSet.toByteArray();
    }
    private BitSet bytesToBitSet(byte[] bytes) {
        return BitSet.valueOf(bytes);
    }

    public void saveCornerPattern(Map<BitSet, Integer> visited) throws SQLException {
        int count = 0;

        for (Map.Entry<BitSet, Integer> entry : visited.entrySet()) {
            insertStatement.setBytes(1, bitSetToBytes(entry.getKey()));
            insertStatement.setInt(2, entry.getValue());
            insertStatement.addBatch();
            count++;

            if (count % BATCH_SIZE == 0) {
                insertStatement.executeBatch();
                connection.commit();
                insertStatement.clearBatch();
                System.out.println("✅ Đã chèn " + count + " bản ghi...");
            }
        }

        if (count % BATCH_SIZE != 0) {
            insertStatement.executeBatch();
            connection.commit();
            insertStatement.clearBatch();
            System.out.println("✅ Đã chèn lô cuối cùng (" + (count % BATCH_SIZE) + " bản ghi).");
        }
    }

    public void close() {
        try {
            if (insertStatement != null) insertStatement.close();
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
