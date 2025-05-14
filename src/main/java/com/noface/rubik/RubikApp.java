package com.noface.rubik;

import com.noface.rubik.enums.RubikFace;
import com.noface.rubik.enums.RubikMove;
import com.noface.rubik.heuristic.*;
import com.noface.rubik.rubikImpl.Rubik2;
import com.noface.rubik.solver.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
// ... (các import giữ nguyên)
import javafx.application.Platform; // THÊM import này

import java.util.List; // THÊM import này
import java.util.concurrent.CompletableFuture; // THÊM import này
import java.util.function.Function;

enum SolverType {
    IDA_STAR("IDA*", IDASolver.getInstance()),
    BFS("BFS", BFSSolver.getInstance()),
    DFS("DFS", DFSSolver.getInstance()),;

    private final String name;
    private Solver solver;

    SolverType(String name, Solver solver) {
        this.name = name;
        this.solver = solver;
    }
    public Solver getSolver(){
        return solver;
    }
    public String getName() {
        return name;
    }
}

enum HeuristicType {
    MANHATTAN("Manhattan", ManhattanHeuristic::getValue),
    WRONG_ORIENTATION("Wrong orientation", WrongOrientationHeuristic::wrongOrientationOnly),
    MISPLACED_CORNER("Misplaced Corner", MisplacedCornersHeuristic::misplacedCorners),
    MAX_POSITION_ORIENTATION("Max position orientation", MaxPositionOrientationHeuristic::maxOfPositionAndOrientation),
    HAMMING_DISTANCE("Hamming Distance", HammingDistanceHeuristic::hammingDistance),
    ;

    private final String name;
    private Function<Rubik2, Integer> heuristicFunction;
    HeuristicType(String name, Function<Rubik2, Integer> heuristicFunction) {
        this.name = name;
        this.heuristicFunction = heuristicFunction;
    }
    public Function<Rubik2, Integer> getHeuristicFunction() {
        return heuristicFunction;
    }
    public String getName() {
        return name;
    }
}

public class RubikApp extends Application {
    private static final int CUBIE_SIZE = 40;
    private static final int GAP = 2;
    private CompletableFuture task;
    private Solver solver = IDASolver.getInstance();
    private Rubik2 rubik;
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
    private Button applySolutionBtn;
    private Button cancelApplySolutionBtn;

    private ComboBox<String> solverComboBox;
    private ComboBox<String> heuristicComboBox;
    private List<RubikMove> solutions;
    private long durationSolved ;
    private long memoryUsed ;
    private Thread applySolutionThread;

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

        // Create control panel
        HBox rubikMoveControl = createRubikMoveControl();
        VBox leftBox = new VBox();
        leftBox.getChildren().addAll(cubeNet, rubikMoveControl);


        // Create notation legend
        VBox legend = createRightSideBar();
        root.setRight(legend);

        // THÊM: Khu vực hiển thị các bước giải
        solutionArea = new TextArea();
        solutionArea.setEditable(false);
        solutionArea.setWrapText(true);
        solutionArea.setPrefHeight(80);
        solutionArea.setFont(new Font("Arial", 20));
        VBox.setVgrow(solutionArea, Priority.ALWAYS);

        // Right side bar
        VBox centerContent = new VBox(10, cubeNet, solutionArea, rubikMoveControl);
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
                });

        // Right face controls
        VBox rightControls = createFaceControls("R", "Right",
                () -> {
                    rubik.moveR();
                    updateCubeVisual();
                });

        // Front face controls
        VBox frontControls = createFaceControls("F", "Front",
                () -> {
                    rubik.moveF();
                    updateCubeVisual();
                });




        controlPanel.getChildren().addAll(
                upControls,
                rightControls, frontControls
        );





        return controlPanel;
    }

    // THÊM (Tùy chọn): Phương thức để áp dụng các bước giải lên Rubik3 trên UI
    // Bạn có thể tạo một nút riêng để "Thực hiện các bước giải"
    private void applySolutionToGUICube(List<RubikMove> solutionSteps) {
        if (solutionSteps == null || solutionSteps.isEmpty())
            return;
        hideButton(shuffleBtn);
        hideButton(resetBtn);
        hideButton(solveBtn);
        hideButton(applySolutionBtn);
        showButton(cancelApplySolutionBtn);
        // Có thể thực hiện từ từ với một chút delay để người dùng thấy
        applySolutionThread = new Thread(() -> {
            for (RubikMove move : solutionSteps) {
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
            Platform.runLater(() -> {
                solutionArea.appendText("\nFinished applying solution!");
                solutions = null;
                hideButton(applySolutionBtn);
            });
        });
        applySolutionThread.setDaemon(true); // Để luồng tự thoát khi ứng dụng đóng
        applySolutionThread.start();
    }

    private VBox createFaceControls(String face, String faceName, Runnable clockwise) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);

        Text faceLabel = new Text(faceName);
        faceLabel.setFont(Font.font(14));

        Button cwBtn = new Button(face);
        cwBtn.setOnAction(e -> clockwise.run());
        cwBtn.setStyle("-fx-font-size: 14; -fx-pref-width: 40;");


        box.getChildren().addAll(faceLabel, cwBtn);
        return box;
    }
    private void solveCube() {
        showButton(stopSolveBtn);
        hideButton(resetBtn);
        hideButton(solveBtn);
        hideButton(shuffleBtn);
        solutionArea.setText(
                "Solving with " + solverComboBox.getValue() + "... Please wait.");
        task = CompletableFuture.supplyAsync(() -> {

            SolutionResult res = solver.solve(this.rubik);
            return res;
        }).thenAcceptAsync(solution -> Platform.runLater(() -> {

            if (solution != null) {
                if(solution.getMoves().size() == 0) {
                    solutionArea.setText("Already solved");
                } else {
                    showButton(applySolutionBtn);
                    hideButton(stopSolveBtn);
                    solutionArea.setText(
                            "Solution found (" + solution.getMoves().size() + " moves):\n" + String.join(" ", solution.getMoves().stream().map(
                                    rubikMove -> rubikMove.getNotation()
                            ).toList()));
                    solutionArea.appendText("\nTotal time: " + solution.getTimeUsed() / 1_000_000.0 + " ms");
                    solutionArea.appendText("\nMemory used: " + solution.getMemoryUsed() / (1024 * 1024) + "MB");
                    solutionArea.appendText("\nNode openned: " + solution.getNodeOpened());
                    solutionArea.appendText("\nMaximmum node hold: " + solution.getMaximmumNodeHold());
                }
            } else {
                solutionArea.setText("No solution found within limits or an error occurred.");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Solver Error");
                alert.setHeaderText(null);
                alert.setContentText(
                        "No solution found within the algorithm's limits, or the cube is already solved/heuristic issue.");
                alert.showAndWait();
            }
            hideButton(stopSolveBtn);
            showButton(solveBtn);
            showButton(resetBtn);
            showButton(shuffleBtn);
        })).exceptionally(ex -> {
            Platform.runLater(() -> {
                solutionArea.setText("Error during solving: " + ex.getMessage());
                ex.printStackTrace();
                solveBtn.setDisable(false);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Solver Error");
                alert.setHeaderText("An error occurred while trying to solve the cube.");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            });
            return null;
        });
    }

    private VBox createRightSideBar() {
        VBox legend = new VBox(10);
        legend.setPadding(new Insets(20));
        legend.setStyle("-fx-background-color: #f8f8f8; -fx-border-color: #ccc; -fx-border-width: 1;");
        createSolverAndHeuristicDropDowns(legend);
        Text title = new Text("Control panel");

        // Shuffle button
        shuffleBtn = new Button("Shuffle");
        shuffleBtn.setOnAction(e -> {
            hideButton(solveBtn);
            hideButton(resetBtn);
            hideButton(applySolutionBtn);
            solutionArea.setText("");
            rubik.shuffle(100);
            updateCubeVisual();
            showButton(solveBtn);
            showButton(resetBtn);
        });
        shuffleBtn.setStyle("-fx-font-size: 14; -fx-pref-width: 100;");

        // Reset button
        resetBtn = new Button("Reset");
        resetBtn.setOnAction(e -> {
            rubik.reset();
            solver.stopSolving();
            if(applySolutionThread != null){
                applySolutionThread.interrupt();

            }
            solutionArea.setText("");
            hideButton(applySolutionBtn);
            updateCubeVisual();
        });
        resetBtn.setStyle("-fx-font-size: 14; -fx-pref-width: 100;");

        // THÊM: Nút Solve (IDA*)
        solveBtn = new Button("Solve");
        solveBtn.setOnAction(e -> {

            solveCube();
        });
        solveBtn.setStyle("-fx-font-size: 14; -fx-pref-width: 120;");

        stopSolveBtn = new Button("Stop");
        stopSolveBtn.setOnAction(e -> {
            hideButton(stopSolveBtn);
            showButton(shuffleBtn);
            showButton(solveBtn);
            solver.stopSolving();
        });
        hideButton(stopSolveBtn);
        stopSolveBtn.setStyle("-fx-font-size: 14; -fx-pref-width: 120;");

        cancelApplySolutionBtn = new Button("Cancel");
        hideButton(cancelApplySolutionBtn);
        cancelApplySolutionBtn.setOnAction(e -> {
            if(applySolutionThread != null){
                applySolutionThread.interrupt();
            }
            showButton(resetBtn);
            showButton(shuffleBtn);
            showButton(solveBtn);
            hideButton(cancelApplySolutionBtn);
        });
        hideButton(cancelApplySolutionBtn);

        applySolutionBtn = new Button("Apply");
        applySolutionBtn.setOnAction(e -> {
            applySolutionToGUICube(solutions);
        });
        hideButton(applySolutionBtn);

        legend.getChildren().addAll(title, shuffleBtn, resetBtn, solveBtn,
                stopSolveBtn, applySolutionBtn, cancelApplySolutionBtn);
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
    private void hideButton(Button button) {
        button.setManaged(false);
        button.setVisible(false);
    }
    private void showButton(Button button) {
        button.setManaged(true);
        button.setVisible(true);
    }
    private void createSolverAndHeuristicDropDowns(VBox legend) {
        // ComboBox cho phương pháp giải
        solverComboBox = new ComboBox<>();
        solverComboBox.getItems().addAll(SolverType.IDA_STAR.getName(), SolverType.BFS.getName(),
                SolverType.DFS.getName());
        solverComboBox.setValue(SolverType.IDA_STAR.getName()); // Mặc định chọn IDA*

        // ComboBox cho hàm heuristic
        heuristicComboBox = new ComboBox<>();
        for(HeuristicType type : HeuristicType.values()) {
            heuristicComboBox.getItems().add(type.getName());
        }
        heuristicComboBox.setValue(HeuristicType.MANHATTAN.getName()); // Mặc định chọn Manhattan

        // Thêm các ComboBox vào legend
        VBox solverBox = new VBox(10, new Text("Choose Solver:"), solverComboBox);
        VBox heuristicBox = new VBox(10, new Text("Choose Heuristic:"), heuristicComboBox);
        solverComboBox.setOnAction(e -> {
            String selectedSolver = solverComboBox.getValue();
            for(SolverType type : SolverType.values()) {
                if(selectedSolver.equals(type.getName())) {
                    solver = type.getSolver();
                }
            }
        });
        heuristicComboBox.setOnAction(e -> {
            if(solver instanceof IDASolver) {
                IDASolver idaSolver = (IDASolver) solver;
                String selectedHeuristic = heuristicComboBox.getValue();
                for(HeuristicType type : HeuristicType.values()) {
                    if(selectedHeuristic.equals(type.getName())) {
                        idaSolver.setHeuristicFunction(type.getHeuristicFunction());
                    }
                }
            }
            String selectedHeuristic = heuristicComboBox.getValue();

        });
        legend.getChildren().addAll(solverBox, heuristicBox);
    }
    public void hideComboBox(ComboBox<String> comboBox) {
        comboBox.setDisable(true);
    }

    public void showComboBox(ComboBox<String> comboBox) {
        comboBox.setDisable(false);
    }
    public static void main(String[] args) {
        launch(args);
    }
}