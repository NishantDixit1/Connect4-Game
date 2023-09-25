package com.internshala.connectfour;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    //variable is created to be used to connect controller to main
    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        //connect file to fxml file
       FXMLLoader loader=new FXMLLoader(getClass().getResource("game.fxml"));
       //gridPane is the root node
        GridPane rootGridPane = loader.load();
        //connects controller class to main
        controller =loader.getController();
        //invoke createPlayground method of controller class
        controller.createPlayground();
        //invoke createMenu method in start and assign it to menuBar
        MenuBar menuBar =createMenu();
        //make the length of menuBar equals to Pane
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        //getting pane from gridPane and use 0 as the index of Pane
        //reference to menuPane
        Pane  menuPane = (Pane) rootGridPane.getChildren().get(0);
        //add menuBar to menuPane
        menuPane.getChildren().add(menuBar);
        //setting scene
        Scene scene=new Scene(rootGridPane);
        primaryStage.setScene(scene);
        //set title to stage
        primaryStage.setTitle("connect4");
        primaryStage.show();
    }
    private MenuBar createMenu()
    {
        Menu fileMenu =new Menu("file");
        MenuItem newGameItem =new MenuItem("new game");
        newGameItem.setOnAction(event -> controller.resetGame());
        MenuItem resetGameItem =new MenuItem("reset game");
        resetGameItem.setOnAction(event -> controller.resetGame());
        SeparatorMenuItem separatorMenuItem=new SeparatorMenuItem();
        MenuItem exitGame  =new MenuItem("exit game");
        //using click handler by lambda
        exitGame.setOnAction(event -> exitGame());
        fileMenu.getItems().addAll(newGameItem,resetGameItem,separatorMenuItem,exitGame);
        Menu helpMenu=new Menu("help");
        MenuItem aboutUs=new MenuItem("about game");
        aboutUs.setOnAction(event -> aboutConnect4());
        SeparatorMenuItem separatorMenuItem1=new SeparatorMenuItem();
        MenuItem aboutMe  =new MenuItem("about me");
        aboutMe.setOnAction(event -> aboutDeveloper());
        helpMenu.getItems().addAll(aboutUs,separatorMenuItem1,aboutMe);
        MenuBar menuBar =new MenuBar();
        menuBar.getMenus().addAll(fileMenu,helpMenu);
        return menuBar;
    }

    private void aboutDeveloper()
    {
        Alert alertDialog = new Alert(Alert.AlertType.INFORMATION);
        alertDialog.setTitle("About the Developer");
        alertDialog.setContentText("I love to play around with code and create games"+
                "Connect4 is one of them in free time "+
                "I like to spend time with my nears and dears.");
        alertDialog.setHeaderText("Divyansh Dixit");
        alertDialog.show();
    }

    private void aboutConnect4()
    {
        Alert alertDialog = new Alert(Alert.AlertType.INFORMATION);
        alertDialog.setTitle("Connect4 game");
        alertDialog.setContentText("Connect Four is a two-player connection game in which the players first choose a " +
                "color and then take turns dropping colored discs from the top into a seven-column, six-row vertically suspended grid. " +
                "The pieces fall straight down, occupying the next available space within the column. The objective of the game is " +
                "to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs. Connect Four is a solved game. " +
                "The first player can always win by playing the right moves.");
        alertDialog.setHeaderText("how to play!");
        alertDialog.show();
    }

    private void exitGame()
    {
        Platform.exit();
        System.exit(0);
    }

    private void resetGame()
    {
    	

    }

    @Override
    public void stop() throws Exception
    {
        super.stop();
        System.out.println("stop");
    }
    @Override
    public void init() throws Exception
    {
        super.init();
        System.out.println("init");
    }
    public static void main(String[] args)
    {
    	launch(args);
    }
}

