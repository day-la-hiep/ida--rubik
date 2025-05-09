package com.noface;

import com.noface.Rubik;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
// ... (các import giữ nguyên)
import javafx.scene.control.TextArea; // THÊM import này
import javafx.scene.control.ScrollPane; // THÊM import này
import javafx.scene.control.Alert; // THÊM import này
import javafx.application.Platform; // THÊM import này

import java.util.List; // THÊM import này
import java.util.concurrent.CompletableFuture; // THÊM import này

public class RubikApp extends Application {
    private static final int CUBIE_SIZE = 40;
    private static final int GAP = 2;
    private static final Color[] FACE_COLORS = {
            Color.WHITE, // U - Up (White)
            Color.ORANGE, // L - Left (Orange)
            Color.GREEN, // F - Front (Green)
            Color.RED, // R - Right (Red)
            Color.BLUE, // B - Back (Blue)
            Color.YELLOW // D - Down (Yellow)
    };

    private Rubik rubik = new Rubik();
    private GridPane upFace = new GridPane();
    private GridPane leftFace = new GridPane();
    private GridPane frontFace = new GridPane();
    private GridPane rightFace = new GridPane();
    private GridPane backFace = new GridPane();
    private GridPane downFace = new GridPane();

    private TextArea solutionArea; // THÊM: Khu vực hiển thị các bước giải
    private Button solveButton; // THÊM: Tham chiếu đến nút Solve

    @Override
    public void start(Stage primaryStage) {
        // Main layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Create the cube net view
        HBox cubeNet = createCubeNetView();
        root.setCenter(cubeNet);

        // Create control panel
        HBox controlPanel = createControlPanel();
        root.setBottom(controlPanel);

        // Create notation legend
        VBox legend = createNotationLegend();
        root.setRight(legend);

        // THÊM: Khu vực hiển thị các bước giải
        solutionArea = new TextArea();
        solutionArea.setEditable(false);
        solutionArea.setWrapText(true);
        solutionArea.setPrefHeight(80);
        ScrollPane scrollPane = new ScrollPane(solutionArea);
        scrollPane.setFitToWidth(true);

        VBox centerContent = new VBox(10, cubeNet, scrollPane); // Nhóm cubeNet và solutionArea
        centerContent.setAlignment(Pos.CENTER);
        root.setCenter(centerContent);

        // Initialize the cube display
        updateCubeVisual();

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("2D Rubik's Cube Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createCubeNetView() {
        HBox netContainer = new HBox(10);
        netContainer.setAlignment(Pos.CENTER);

        // Left face
        VBox leftBox = new VBox();
        leftBox.setAlignment(Pos.CENTER);
        leftBox.getChildren().add(createFacePanel("LEFT", leftFace));

        // Center faces (Up, Front, Down)
        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.getChildren().addAll(
                createFacePanel("UP", upFace),
                createFacePanel("FRONT", frontFace),
                createFacePanel("DOWN", downFace));

        // Right face
        VBox rightBox = new VBox();
        rightBox.setAlignment(Pos.CENTER);
        rightBox.getChildren().add(createFacePanel("RIGHT", rightFace));

        // Back face (placed at the right side)
        VBox backBox = new VBox();
        backBox.setAlignment(Pos.CENTER);
        backBox.getChildren().add(createFacePanel("BACK", backFace));

        netContainer.getChildren().addAll(leftBox, centerBox, rightBox, backBox);
        return netContainer;
    }

    private VBox createFacePanel(String title, GridPane faceGrid) {
        VBox panel = new VBox(5);
        panel.setAlignment(Pos.CENTER);

        Text titleText = new Text(title);
        titleText.setFont(Font.font(14));

        // Configure the face grid
        faceGrid.setHgap(GAP);
        faceGrid.setVgap(GAP);
        faceGrid.setAlignment(Pos.CENTER);

        panel.getChildren().addAll(titleText, faceGrid);
        return panel;
    }

    private HBox createControlPanel() {
        HBox controlPanel = new HBox(10);
        controlPanel.setPadding(new Insets(10));
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.setStyle("-fx-background-color: #f0f0f0;");

        // Up face controls
        VBox upControls = createFaceControls("U", "Up",
                () -> {
                    rubik.moveU();
                    updateCubeVisual();
                },
                () -> {
                    rubik.moveUPrime();
                    updateCubeVisual();
                });

        // Down face controls
        VBox downControls = createFaceControls("D", "Down",
                () -> {
                    rubik.moveD();
                    updateCubeVisual();
                },
                () -> {
                    rubik.moveDPrime();
                    updateCubeVisual();
                });

        // Left face controls
        VBox leftControls = createFaceControls("L", "Left",
                () -> {
                    rubik.moveL();
                    updateCubeVisual();
                },
                () -> {
                    rubik.moveLPrime();
                    updateCubeVisual();
                });

        // Right face controls
        VBox rightControls = createFaceControls("R", "Right",
                () -> {
                    rubik.moveR();
                    updateCubeVisual();
                },
                () -> {
                    rubik.moveRPrime();
                    updateCubeVisual();
                });

        // Front face controls
        VBox frontControls = createFaceControls("F", "Front",
                () -> {
                    rubik.moveF();
                    updateCubeVisual();
                },
                () -> {
                    rubik.moveFPrime();
                    updateCubeVisual();
                });

        // Back face controls
        VBox backControls = createFaceControls("B", "Back",
                () -> {
                    rubik.moveB();
                    updateCubeVisual();
                },
                () -> {
                    rubik.moveBPrime();
                    updateCubeVisual();
                });

        // Shuffle button
        Button shuffleBtn = new Button("Shuffle");
        shuffleBtn.setOnAction(e -> shuffleCube());
        shuffleBtn.setStyle("-fx-font-size: 14; -fx-pref-width: 100;");

        // Reset button
        Button resetBtn = new Button("Reset");
        resetBtn.setOnAction(e -> {
            rubik = new Rubik();
            updateCubeVisual();
        });
        resetBtn.setStyle("-fx-font-size: 14; -fx-pref-width: 100;");

        // THÊM: Nút Solve (IDA*)
        solveButton = new Button("Solve (IDA*)");
        solveButton.setOnAction(e -> solveCubeWithIDA());
        solveButton.setStyle("-fx-font-size: 14; -fx-pref-width: 120;");

        controlPanel.getChildren().addAll(
                upControls, downControls, leftControls,
                rightControls, frontControls, backControls,
                shuffleBtn, resetBtn, solveButton // THÊM solveButton vào đây
        );

        return controlPanel;
    }

    private void solveCubeWithIDA() {
        solutionArea.setText(
                "Solving with IDA*... Please wait.\nThis might take some time depending on the scramble depth and heuristic strength.");
        solveButton.setDisable(true); // Vô hiệu hóa nút trong khi giải

        Rubik currentScrambledRubik = new Rubik(this.rubik); // Tạo bản sao để giải, không ảnh hưởng đến rubik hiện tại
                                                             // trên UI

        // Chạy tác vụ giải trong một luồng riêng để không làm đóng băng UI
        CompletableFuture.supplyAsync(() -> {
            IDASolver solver = new IDASolver();
            return solver.solve(currentScrambledRubik);
        }).thenAcceptAsync(solutionSteps -> Platform.runLater(() -> { // Cập nhật UI trên luồng JavaFX
            if (solutionSteps != null && !solutionSteps.isEmpty()) {
                solutionArea.setText(
                        "Solution found (" + solutionSteps.size() + " moves):\n" + String.join(" ", solutionSteps));
                // Tùy chọn: Tự động áp dụng các bước giải lên Rubik trên UI
                // applySolutionToGUICube(solutionSteps);
            } else {
                solutionArea.setText("No solution found within limits or an error occurred.");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("IDA* Solver");
                alert.setHeaderText(null);
                alert.setContentText(
                        "No solution found within the algorithm's limits, or the cube is already solved/heuristic issue.");
                alert.showAndWait();
            }
            solveButton.setDisable(false); // Kích hoạt lại nút
        })).exceptionally(ex -> { // Xử lý lỗi nếu có
            Platform.runLater(() -> {
                solutionArea.setText("Error during solving: " + ex.getMessage());
                ex.printStackTrace();
                solveButton.setDisable(false);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("IDA* Solver Error");
                alert.setHeaderText("An error occurred while trying to solve the cube.");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            });
            return null;
        });
    }

    // THÊM (Tùy chọn): Phương thức để áp dụng các bước giải lên Rubik trên UI
    // Bạn có thể tạo một nút riêng để "Thực hiện các bước giải"
    private void applySolutionToGUICube(List<String> solutionSteps) {
        if (solutionSteps == null || solutionSteps.isEmpty())
            return;

        // Có thể thực hiện từ từ với một chút delay để người dùng thấy
        Thread applyThread = new Thread(() -> {
            for (String move : solutionSteps) {
                Platform.runLater(() -> {
                    this.rubik.applyMove(move);
                    updateCubeVisual();
                    solutionArea.appendText("\nApplied: " + move);
                });
                try {
                    Thread.sleep(500); // Delay 0.5 giây giữa các bước
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            Platform.runLater(() -> solutionArea.appendText("\nFinished applying solution!"));
        });
        applyThread.setDaemon(true); // Để luồng tự thoát khi ứng dụng đóng
        applyThread.start();
    }

    private VBox createFaceControls(String face, String faceName, Runnable clockwise, Runnable counterClockwise) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);

        Text faceLabel = new Text(faceName);
        faceLabel.setFont(Font.font(14));

        Button cwBtn = new Button(face);
        cwBtn.setOnAction(e -> clockwise.run());
        cwBtn.setStyle("-fx-font-size: 14; -fx-pref-width: 40;");

        Button ccwBtn = new Button(face + "'");
        ccwBtn.setOnAction(e -> counterClockwise.run());
        ccwBtn.setStyle("-fx-font-size: 14; -fx-pref-width: 40;");

        box.getChildren().addAll(faceLabel, cwBtn, ccwBtn);
        return box;
    }

    private VBox createNotationLegend() {
        VBox legend = new VBox(10);
        legend.setPadding(new Insets(20));
        legend.setStyle("-fx-background-color: #f8f8f8; -fx-border-color: #ccc; -fx-border-width: 1;");

        Text title = new Text("Rubik's Cube Notation");
        title.setFont(Font.font(16));

        Text uNotation = new Text("U - Up face (White)");
        Text dNotation = new Text("D - Down face (Yellow)");
        Text lNotation = new Text("L - Left face (Orange)");
        Text rNotation = new Text("R - Right face (Red)");
        Text fNotation = new Text("F - Front face (Green)");
        Text bNotation = new Text("B - Back face (Blue)");

        Text primeNotation = new Text("' - Counter-clockwise\n(e.g., U' = Up face CCW)");
        primeNotation.setWrappingWidth(150);

        legend.getChildren().addAll(title, uNotation, dNotation, lNotation,
                rNotation, fNotation, bNotation, primeNotation);
        return legend;
    }

    private void shuffleCube() {
        int moves = 12;
        for (int i = 0; i < moves; i++) {
            int move = (int) (Math.random() * 12);
            switch (move) {
                case 0:
                    rubik.moveU();
                    break;
                case 1:
                    rubik.moveUPrime();
                    break;
                case 2:
                    rubik.moveD();
                    break;
                case 3:
                    rubik.moveDPrime();
                    break;
                case 4:
                    rubik.moveL();
                    break;
                case 5:
                    rubik.moveLPrime();
                    break;
                case 6:
                    rubik.moveR();
                    break;
                case 7:
                    rubik.moveRPrime();
                    break;
                case 8:
                    rubik.moveF();
                    break;
                case 9:
                    rubik.moveFPrime();
                    break;
                case 10:
                    rubik.moveB();
                    break;
                case 11:
                    rubik.moveBPrime();
                    break;
            }
        }
        updateCubeVisual();
    }

    private void updateCubeVisual() {
        // Clear all faces
        upFace.getChildren().clear();
        leftFace.getChildren().clear();
        frontFace.getChildren().clear();
        rightFace.getChildren().clear();
        backFace.getChildren().clear();
        downFace.getChildren().clear();

        // Update each face
        updateFace(upFace, 0, 0); // Up face
        updateFace(leftFace, 9, 1); // Left face
        updateFace(frontFace, 18, 2); // Front face
        updateFace(rightFace, 27, 3); // Right face
        updateFace(backFace, 36, 4); // Back face
        updateFace(downFace, 45, 5); // Down face
    }

    private void updateFace(GridPane faceGrid, int startIndex, int faceIndex) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int index = startIndex + row * 3 + col;
                char colorChar = rubik.cube.get(index);
                Color color = getColorForFace(faceIndex, index);

                Rectangle cubie = new Rectangle(CUBIE_SIZE, CUBIE_SIZE, color);
                cubie.setStroke(Color.BLACK);
                cubie.setStrokeWidth(1);

                faceGrid.add(cubie, col, row);
            }
        }
    }

    private Color getColorForFace(int face, int index) {
        char colorChar = rubik.cube.get(index);
        switch (colorChar) {
            case 'U':
                return FACE_COLORS[0]; // White
            case 'L':
                return FACE_COLORS[1]; // Orange
            case 'F':
                return FACE_COLORS[2]; // Green
            case 'R':
                return FACE_COLORS[3]; // Red
            case 'B':
                return FACE_COLORS[4]; // Blue
            case 'D':
                return FACE_COLORS[5]; // Yellow
            default:
                return Color.BLACK;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}