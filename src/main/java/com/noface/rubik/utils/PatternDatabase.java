package com.noface.rubik.utils;

import java.sql.*;
import java.util.BitSet;
import java.util.Map;

public class PatternDatabase {
    private static PatternDatabase patternDatabase;
    private final Connection connection;
    private final PreparedStatement cornerStateInsertStatement;
    private final PreparedStatement getCornerStateDepthStatement;
    private static final int BATCH_SIZE = 10_000;
    private final String jdbcUrl = "jdbc:mysql://localhost:3306/rubik?useSSL=false&serverTimezone=UTC";
    private final String username = "root";
    private final String password = "123456";
    private final PreparedStatement getEdgeStateDepthStatement;
    private final PreparedStatement edgeStateInsertStatement;
    public static PatternDatabase getInstance() throws SQLException {
        if(patternDatabase == null) {
            patternDatabase = new PatternDatabase();
            System.out.println("PatternDatabase created");
        }
        return patternDatabase;
    }
    private PatternDatabase() throws SQLException {

        this.connection = DriverManager.getConnection(jdbcUrl, username, password);
        this.connection.setAutoCommit(false);  // tăng hiệu suất
        createCornerTableIfNotExists();
        createEdgeTableIfNotExists();
        String insertCornerStateSql = "INSERT INTO corner_pattern (id, depth) VALUES (?, ?)";
        this.cornerStateInsertStatement = connection.prepareStatement(insertCornerStateSql);
        String getCornerStateDepthSql = "SELECT depth FROM corner_pattern WHERE state = ?";
        this.getCornerStateDepthStatement = connection.prepareStatement(getCornerStateDepthSql);

        String insertEdgeStateSql = "INSERT INTO edge_pattern (state, depth) VALUES (?, ?)";
        this.edgeStateInsertStatement = connection.prepareStatement(insertEdgeStateSql);

        String getEdgeStateDepthSql = "SELECT depth FROM edge_pattern WHERE state = ?";
        this.getEdgeStateDepthStatement = connection.prepareStatement(getEdgeStateDepthSql);

    }

    private void createEdgeTableIfNotExists() {
        String ddl = """
            CREATE TABLE IF NOT EXISTS edge_pattern (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                state BIGINT NOT NULL,
                depth INT NOT NULL
            );
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(ddl);
            connection.commit();
            System.out.println("✅ Bảng corner_pattern đã được tạo (nếu chưa tồn tại).");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createCornerTableIfNotExists(){
        String ddl = """
            CREATE TABLE IF NOT EXISTS corner_pattern (
                id INT PRIMARY KEY,
                depth INT NOT NULL
            );
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(ddl);
            connection.commit();
            System.out.println("✅ Bảng corner_pattern đã được tạo (nếu chưa tồn tại).");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] bitSetToBytes(BitSet bitSet) {
        return bitSet.toByteArray();
    }
    private BitSet bytesToBitSet(byte[] bytes) {
        return BitSet.valueOf(bytes);
    }
    public void saveEdgePattern(Map<Long, Integer> edgePattern) {
        int count = 0;

        for (Map.Entry<Long, Integer> entry : edgePattern.entrySet()) {
            try {
                edgeStateInsertStatement.setLong(1, entry.getKey());
                edgeStateInsertStatement.setInt(2, entry.getValue());
                edgeStateInsertStatement.addBatch();
                count++;

                if (count % BATCH_SIZE == 0) {
                    edgeStateInsertStatement.executeBatch();
                    connection.commit();
                    edgeStateInsertStatement.clearBatch();
                    System.out.println("✅ Đã chèn " + count + " bản ghi...");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (count % BATCH_SIZE != 0) {
            try {
                edgeStateInsertStatement.executeBatch();
                connection.commit();
                edgeStateInsertStatement.clearBatch();
                System.out.println("✅ Đã chèn lô cuối cùng (" + (count % BATCH_SIZE) + " bản ghi).");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public void saveCornerPattern(int[] visited) throws SQLException {
        int count = 0;

        for (int i = 0; i < visited.length; i++) {
            cornerStateInsertStatement.setInt(1, i);
            cornerStateInsertStatement.setInt(2, visited[i]);
            cornerStateInsertStatement.addBatch();
            count++;

            if (count % BATCH_SIZE == 0) {
                cornerStateInsertStatement.executeBatch();
                connection.commit();
                cornerStateInsertStatement.clearBatch();
                System.out.println("✅ Đã chèn " + count + " bản ghi...");
            }
        }

        if (count % BATCH_SIZE != 0) {
            cornerStateInsertStatement.executeBatch();
            connection.commit();
            cornerStateInsertStatement.clearBatch();
            System.out.println("✅ Đã chèn lô cuối cùng (" + (count % BATCH_SIZE) + " bản ghi).");
        }
    }
    public int getCornerStateDepth(int state) {
        try{
            getCornerStateDepthStatement.setLong(1, state);
            ResultSet rs = getCornerStateDepthStatement.executeQuery();
            if (rs.next()) {
                int depth = rs.getInt("depth");
                return depth;
            } else {
                return Integer.MAX_VALUE;
            }
        }catch (SQLException e){
            e.printStackTrace();
            return Integer.MAX_VALUE;
        }
    }

    public int getEdgeStateDepth(Long state)  {
        try{
            getEdgeStateDepthStatement.setLong(1, state);
            ResultSet rs = getEdgeStateDepthStatement.executeQuery();
            if (rs.next()) {
                int depth = rs.getInt("depth");
                return depth;
            } else {
                return Integer.MAX_VALUE;
            }
        }catch (SQLException e){
            e.printStackTrace();
            return Integer.MAX_VALUE;
        }
    }



    public void close() {
        try {
            if (cornerStateInsertStatement != null) cornerStateInsertStatement.close();
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int[] loadCornerPatternChunked() throws SQLException {
        final int TOTAL_CORNER_STATES = 90000005;
        int[] visited = new int[TOTAL_CORNER_STATES];
        int chunkSize = 1000000; // 1 triệu bản ghi mỗi lần

        System.out.println("Load corner pattern (chunked)");

        for (int offset = 0; offset < TOTAL_CORNER_STATES; offset += chunkSize) {
            String sql = String.format(
                    "SELECT id, depth FROM corner_pattern WHERE id BETWEEN %d AND %d",
                    offset, offset + chunkSize - 1);

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    if (id < TOTAL_CORNER_STATES) {
                        visited[id] = rs.getInt("depth");
                    }
                }
            }
        }

        System.out.println("✅ Đã load toàn bộ trạng thái góc (đã có dữ liệu: " + countNonZero(visited) + ")");
        return visited;
    }
    private int countNonZero(int[] array) {
        int count = 0;
        for (int val : array) {
            if (val != 0) count++;
        }
        return count;
    }


}
