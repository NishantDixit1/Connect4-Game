package com.internshala.connectfour;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable
{
	//variables that are rules for the game
	private static final int COLUMNS=7;
	private static final int ROWS=6;
	private static final int CIRCLE_DIA=80;
	private static String PLAYER_ONE="player1";
	private static String PLAYER_TWO="player2";
	private static final String discColor1="#24303E";
	private static final String discColor2="#4CAA88";
	private boolean isPlayerOneTurn=true;
	private boolean isAllowdedToInsert=true;
	//method which is used to invoke createGameStructuralGrid method and then add that to gridPane
	 Disc[][] insertedDiscsArray =new Disc[ROWS][COLUMNS];
	public void createPlayground()
	{
		//invoke createGameStructuralGrid method
		Shape rectangleWithHoles= createGameStructuralGrid();
		//add rectangleWithHoles to gridPane
		rootGridPane.add(rectangleWithHoles,0,1);
		//invoke createClickableColumns method and store object in rectangleList
		List<Rectangle> rectangleList =createClickableColumns();
		//add all object to gridPane
		for(Rectangle rectangle:rectangleList)
		{
			rootGridPane.add(rectangle,0,1);
		}
		setNamesButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				playerNameLabel.setText(PlayerOneTextField.getText());
				PLAYER_ONE=PlayerOneTextField.getText();
				PLAYER_TWO=PlayerTwoTextField.getText();
			}
		});

	}
	//method that provide the structure
	private Shape createGameStructuralGrid()
	{
		//rectangle is created
		Shape rectangleWithHoles = new Rectangle((COLUMNS+1)*CIRCLE_DIA, (ROWS+1)*CIRCLE_DIA);
		//holes are made out from that rectangle
		for(int col=0;col<COLUMNS;col++)
		{
			for(int row=0;row<ROWS;row++)
			{
				//circle are getting ready to cutout
				Circle circle=new Circle();
				circle.setRadius(CIRCLE_DIA/2);
				circle.setCenterX(CIRCLE_DIA/2);
				circle.setCenterY(CIRCLE_DIA/2);
				//used to shift holes from one position to another
				circle.setTranslateY(row*(CIRCLE_DIA+5)+CIRCLE_DIA/4);
				circle.setTranslateX(col*(CIRCLE_DIA+5)+CIRCLE_DIA/4);
				//for smooth edges
				circle.setSmooth(true);
				//circle are now cut out from rectangle
				rectangleWithHoles=Shape.subtract(rectangleWithHoles,circle);
			}
		}
		//color is filled in rectangle
		rectangleWithHoles.setFill(Color.WHITE);
		//method return the shape to createPlayground method
		return rectangleWithHoles;
	}
	private List<Rectangle> createClickableColumns()
	{
		//Create a collection list of shape rectangle
		List<Rectangle> rectangleList=new ArrayList<>();
		//use loop to make 7 rectangle
		for(int col=0;col<COLUMNS;col++)
		{
			//rectangle  is created with same width as holes
			Rectangle rectangle = new Rectangle(CIRCLE_DIA, (ROWS + 1) * CIRCLE_DIA);
			//color is filled in rectangle
			rectangle.setFill(Color.TRANSPARENT);
			//shift them accordingly
			rectangle.setTranslateX(col*(CIRCLE_DIA+5)+CIRCLE_DIA/4);
			//use mouseEntered event and mouseExited event
			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
			//col is made to be final because in lambda expression only final parameters can be used
			final int column = col;
			//use  mouseClicked event
			rectangle.setOnMouseClicked(event -> {
				if(isAllowdedToInsert)
						{
							isAllowdedToInsert=false;
							insertDisc(new Disc (isPlayerOneTurn), column);
						}
					});
			//add all object to list
			rectangleList.add(rectangle);
		}
		//return list of rectangles
		return rectangleList;
	}
	//Method is created to insert disc in holes

	private void insertDisc(Disc disc, int column)
	{
		//this is used for inserting disc at top and prevent overlapping of discs
		int row=ROWS-1;
		//checking condition that given array position is null
		while(row>0)
		{
			if(getDiscIfPresent(row,column)== null)
			{
				break;
			}
			row--;
		}
		//checking that array is full or not
		if(row<0)
			return;
		//insert disc at given position
		insertedDiscsArray [row][column]=disc; //for structural changes only:only for developers
		//add disc top pane
		insertedDiscsPane.getChildren().add(disc);
		//shift it to appropriate position
		disc.setTranslateX(column*(CIRCLE_DIA+5)+CIRCLE_DIA/4);
		//by using animation
		TranslateTransition translateTransition=new TranslateTransition(Duration.seconds(0.5),disc);
		translateTransition.setToY(row*(CIRCLE_DIA+5)+CIRCLE_DIA/4);
		int currentRow=row;
		//changing label of player name
		translateTransition.setOnFinished(event -> {
			isAllowdedToInsert=true;
			if(gameEnded(currentRow,column))
			{
				gameOver();
				return;
			}
			isPlayerOneTurn=!isPlayerOneTurn;
			playerNameLabel.setText(isPlayerOneTurn?PLAYER_ONE:PLAYER_TWO);
		});
		translateTransition.play();

	}

	private void gameOver()
	{
		//used to show dialog for winner
		String winner =isPlayerOneTurn?PLAYER_ONE:PLAYER_TWO;
		System.out.println("winner is :" +winner);
		Alert alertDialog=new Alert((Alert.AlertType.INFORMATION));
		alertDialog.setHeaderText("the winner is "+ winner);
		alertDialog.setContentText("want to play again");
		ButtonType yesBtn =new ButtonType("yes");
		ButtonType noBtn =new ButtonType("no");
		alertDialog.getButtonTypes().setAll(yesBtn,noBtn);
		//used to display result after transition get over
		Platform.runLater(()->{
			Optional<ButtonType> btnClicked =alertDialog.showAndWait();
			if(btnClicked.isPresent() && btnClicked.get()==yesBtn)
			{
				resetGame();
			}
			else
			{
				System.exit(0);
				Platform.exit();
			}
		});
	}
	//method to reset the game
	public void resetGame()
	{
		//clear visual view
		insertedDiscsPane.getChildren().clear();
		//clearing structural view
		for(int row=0;row<insertedDiscsArray.length;row++)
		{
			for(int col=0;col<insertedDiscsArray[row].length;col++)
			{
				insertedDiscsArray[row][col]=null;
			}
		}
		//setting chance to player 1
		isPlayerOneTurn=true;
		//setting label to player one
		playerNameLabel.setText(PLAYER_ONE);
		//invoke method to create playground again
		createPlayground();
	}
	//method to end the game
	private boolean gameEnded(int currentRow, int column)
	{
		//store all object of disc to check the chain
		List<Point2D> verticalPoints= IntStream.
				rangeClosed(currentRow-3,currentRow+3).
				mapToObj(r -> new Point2D(r,column)).
				collect(Collectors.toList());
		List<Point2D> horizontalPoints= IntStream.
				rangeClosed(column-3,column+3).
				mapToObj(col -> new Point2D(currentRow,col)).
				collect(Collectors.toList());
		Point2D startPoint1=new Point2D(currentRow-3,column+3);
		List<Point2D> diagonalPoints1= IntStream.
				rangeClosed(0,6).
				mapToObj(i -> startPoint1.add(i,-i)).
				collect(Collectors.toList());
		Point2D startPoint2=new Point2D(currentRow-3,column-3);
		List<Point2D> diagonalPoints2= IntStream.
				rangeClosed(0,6).
				mapToObj(i -> startPoint2.add(i,i)).
				collect(Collectors.toList());
		//checking the combination

		boolean isEnded=checkCombination(verticalPoints) || checkCombination(horizontalPoints) || checkCombination(diagonalPoints1) || checkCombination(diagonalPoints2);
		return isEnded;
	}

	private boolean checkCombination(List<Point2D> verticalPoints) {
		int chain = 0;
		for (Point2D point : verticalPoints)
		{
			//separating the coordinates
			int rowIndexForArray = (int) point.getX();
			int columnIndexForArray = (int) point.getY();
			//checking is disc is present at these coordinates
			Disc disc = getDiscIfPresent(rowIndexForArray,columnIndexForArray);
			if (disc != null && disc.isPlayerOneMove == isPlayerOneTurn)
			{
				chain++;
				if (chain == 4)
				{
					return true;
				}
			}
			else
				{
					chain = 0;
				}
		}
		return false;
	}
	private Disc  getDiscIfPresent(int row,int column)
	{
		//checking if there is empty place to insert the disc
		if(row>=ROWS || row<0 || column >=COLUMNS || column<0)
		{
			return null;
		}
		return insertedDiscsArray[row][column];
	}

	// a private class is created to decided the color of the disc for player1 and player2
	//we created this to check whose turn is this and then create a disc of that type

	private static class Disc extends Circle
	{
		private final boolean isPlayerOneMove;
		public Disc( boolean isPlayerOneMove)
		{
			this.isPlayerOneMove = isPlayerOneMove;
			//set color using ternary operator
			setFill(isPlayerOneMove ? Color.valueOf(discColor1): Color.valueOf(discColor2));
			//set properties to disc
			setRadius(CIRCLE_DIA/2);
			setCenterX(CIRCLE_DIA/2);
			setCenterY(CIRCLE_DIA/2);
		}

	}

	@FXML
	public GridPane rootGridPane;
	@FXML
	public Pane insertedDiscsPane;
	@FXML
	public Label playerNameLabel;
	@FXML
	public TextField PlayerOneTextField,PlayerTwoTextField;
	@FXML
	public Button setNamesButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}

