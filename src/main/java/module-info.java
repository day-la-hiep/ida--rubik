module com.noface.rubik {
    requires javafx.controls;
    requires java.sql;


    exports com.noface.rubik;
    exports com.noface.rubik.rubikImpl;
    exports com.noface.rubik.generator;
    exports com.noface.rubik.utils;
    exports com.noface.rubik.enums;
    exports com.noface.rubik.solver;

}