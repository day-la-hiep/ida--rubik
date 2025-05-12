package com.noface.rubik;

import com.noface.rubik.enums.RubikFace;
import com.noface.rubik.enums.RubikMove;
import com.noface.rubik.rubikImpl.Rubik;
import com.noface.rubik.rubikImpl.Rubik2;
import com.noface.rubik.rubikImpl.Rubik3;
import com.noface.rubik.solver.BFSSolver;
import com.noface.rubik.solver.IDASolver;
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
    private CompletableFuture task;
//    private IDASolver solver = new IDASolver();
    BFSSolver solver = new BFSSolver();
    private Rubik rubik;
    private GridPane upFace = new GridPane();
    private GridPane leftFace = new GridPane();
    private GridPane frontFace = new GridPane();
    private GridPane rightFace = new GridPane();
    private GridPane backFace = new GridPane();
    private GridPane downFace = new GridPane();

    private TextArea solutionArea; // THÊM: Khu vực hiển thị các bước giải
    private Button solveBtn; // THÊM: Tham chiếu đến nút Solve
    private Button stopSolveBtn;
    private Button shuffleBtn;
    private Button resetBtn;
    @Override
    public void start(Stage primaryStage) {
        try{

            int cnt = 0;
//            int[] depths = PatternDatabaseHeuristic.getInstance().getDepths();
//            for(int i = 0; i < depths.length; i++){
//                if(depths[i] > 3) cnt++;
//            }
            System.out.println(cnt);
        }catch (Exception e){

        }

        // Main layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Create the cube net view
        HBox cubeNet = createCubeNetView();
        root.setCenter(cubeNet);

        // Create control panel
        HBox controlPanel = createRubikMoveControl();
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
        rubik = new Rubik2();
        updateCubeVisual();

        Scene scene = new Scene(root, 900, 900);
        primaryStage.setTitle("2D Rubik3's Cube Simulation");
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

    private HBox createRubikMoveControl() {
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



        controlPanel.getChildren().addAll(
                upControls, downControls, leftControls,
                rightControls, frontControls, backControls
        );





        return controlPanel;
    }

    // THÊM (Tùy chọn): Phương thức để áp dụng các bước giải lên Rubik3 trên UI
    // Bạn có thể tạo một nút riêng để "Thực hiện các bước giải"
    private void applySolutionToGUICube(List<String> solutionSteps) {
        if (solutionSteps == null || solutionSteps.isEmpty())
            return;

        // Có thể thực hiện từ từ với một chút delay để người dùng thấy
        Thread applyThread = new Thread(() -> {
            for (RubikMove move : RubikMove.values()) {
                Platform.runLater(() -> {
                    this.rubik.applyMove(move);
                    updateCubeVisual();
                    solutionArea.appendText("\nApplied: " + move.name());
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

        Text title = new Text("Control panel");
        title.setFont(Font.font(16));

        // Shuffle button
        shuffleBtn = new Button("Shuffle");
        shuffleBtn.setOnAction(e -> {
            rubik.shuffle(10000);
            updateCubeVisual();
        });
        shuffleBtn.setStyle("-fx-font-size: 14; -fx-pref-width: 100;");

        // Reset button
        resetBtn = new Button("Reset");
        resetBtn.setOnAction(e -> {
            rubik.reset();
            updateCubeVisual();
        });
        resetBtn.setStyle("-fx-font-size: 14; -fx-pref-width: 100;");

        // THÊM: Nút Solve (IDA*)
        solveBtn = new Button("Solve (IDA*)");
        solveBtn.setOnAction(e -> {
            stopSolveBtn.setDisable(false);
            shuffleBtn.setDisable(true);
            resetBtn.setDisable(true);
            solveBtn.setDisable(true);
            solutionArea.setText(
                    "Solving with IDA*... Please wait.\nThis might take some time depending on the scramble depth and heuristic strength.");
            task = CompletableFuture.supplyAsync(() -> {
                long start = System.nanoTime();

                List<String> res = solver.solve(this.rubik);
                long end = System.nanoTime();
                long duration = end - start; // thời gian tính bằng nanosecond

                System.out.println("Tổng thời gian: " + duration + " ns");
                System.out.println("Tương đương khoảng: " + (duration / 1_000_000.0) + " ms");
                return res;
            }).thenAcceptAsync(solutionSteps -> Platform.runLater(() -> {

                if (solutionSteps != null && !solutionSteps.isEmpty()) {

                    solutionArea.setText(
                            "Solution found (" + solutionSteps.size() + " moves):\n" + String.join(" ", solutionSteps));
                    // Tùy chọn: Tự động áp dụng các bước giải lên Rubik3 trên UI
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
                stopSolveBtn.setDisable(true);
                shuffleBtn.setDisable(false);
                resetBtn.setDisable(false);
                solveBtn.setDisable(false);
            })).exceptionally(ex -> { // Xử lý lỗi nếu có
                Platform.runLater(() -> {
                    solutionArea.setText("Error during solving: " + ex.getMessage());
                    ex.printStackTrace();
                    solveBtn.setDisable(false);
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("IDA* Solver Error");
                    alert.setHeaderText("An error occurred while trying to solve the cube.");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                });
                return null;
            });

        });
        solveBtn.setStyle("-fx-font-size: 14; -fx-pref-width: 120;");

        stopSolveBtn = new Button("Stop");
        stopSolveBtn.setOnAction(e -> {
            stopSolveBtn.setDisable(true);
            shuffleBtn.setDisable(true);
            resetBtn.setDisable(true);
            solver.stopSolving();
        });
        stopSolveBtn.setDisable(true);
        stopSolveBtn.setStyle("-fx-font-size: 14; -fx-pref-width: 120;");

        legend.getChildren().addAll(title, shuffleBtn, resetBtn, solveBtn, stopSolveBtn);
        return legend;
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
        int size = rubik.getSize();
        updateFace(upFace, 0, 0); // Up face
        updateFace(leftFace, size * size, 1); // Left face
        updateFace(frontFace, size * size * 2, 2); // Front face
        updateFace(rightFace, size * size * 3, 3); // Right face
        updateFace(backFace, size * size * 4, 4); // Back face
        updateFace(downFace, size * size * 5, 5); // Down face

    }

    private void updateFace(GridPane faceGrid, int startIndex, int faceIndex) {
        for (int row = 0; row < rubik.getSize(); row++) {
            for (int col = 0; col < rubik.getSize(); col++) {
                int index = startIndex + row * rubik.getSize() + col;
                char colorChar = rubik.getState()[index];
                Color color = RubikFace.valueOf(String.valueOf(colorChar)).getColor();

                Rectangle cubie = new Rectangle(CUBIE_SIZE, CUBIE_SIZE, color);
                cubie.setStroke(Color.BLACK);
                cubie.setStrokeWidth(1);

                faceGrid.add(cubie, col, row);
            }
        }
    }




    public static void main(String[] args) {
        launch(args);
    }
}