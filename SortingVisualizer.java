import javafx.application.Application;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.event.*;
import javafx.animation.*;
import javafx.geometry.*;
import java.util.*;
import javafx.util.*;

// imports
// main class that creates the stage
public class SortingVisualizer extends Application {
    @Override
    public void start(Stage stage) {
        stage.initStyle(StageStyle.TRANSPARENT);
        Sorter random = new Sorter(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}

// child class of textfield: only accepts neumeric value
class NumberTextField extends TextField {
    @Override
    public void replaceText(int start, int end, String text) {
        if (text.matches("[0-9]*"))
            super.replaceText(start, end, text);
    }

    @Override
    public void replaceSelection(String text) {
        if (text.matches("[0-9]*"))
            super.replaceSelection(text);
    }
}

// visual represnetation of an array with height and width
// contains animation property for moving in x direction
// contains height and weidth of the pane (height is most important!)
class Card extends Pane {
    double cardHeight;
    double cardWidth;

    public double getCardHeight() {
        return cardHeight;
    }

    public void setCardWidth(double cardWidth) {
        this.cardWidth = cardWidth;
    }

    public double getCardWidth() {
        return cardWidth;
    }

    public void setCardHeight(double cardHeight) {
        this.cardHeight = cardHeight;
    }

    public TranslateTransition moveX(Double x) {
        TranslateTransition translation = new TranslateTransition();
        translation.setNode(this);
        translation.setDuration(Duration.millis(Sorter.SORTING_SPEED));
        translation.setByX(x);
        return translation;
    }
}

// interface that maintains the style structure of the visual elements
interface STYLE_STRINGS {
    static String ROOT_STYLE = "-fx-background-color: linear-gradient(to bottom right,  #323a40, #63737d); ";
    static String CONTAINER_STYLE = "-fx-background-color: linear-gradient(to bottom right,  #49555c, #606f78); -fx-border-color: #83939d; -fx-border-width: 4;";
    static String CARD_STYLE = "-fx-background-color: linear-gradient(to bottom right, #8998a2, #8993af); -fx-text-fill: snow; -fx-font-weight: bold;"
            + "-fx-border-color: #303538;";
    static String CARD_STYLE_HOVERED = "-fx-background-color: linear-gradient(to bottom right, #748087, #8993af); -fx-text-fill: snow; -fx-font-weight: bold;"
            + "-fx-border-color: #303538;";
    static String CARD_LABEL_STYLE = "-fx-text-fill: #dbdbdb;";
    static String UNIT_LABEL_STYLE = "-fx-text-fill: #dbdbdb; -fx-font-weight: bold; -fx-font-size: 22 ;";
    static String APPLABEL_STYLE = "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 26 ;";
    //
    static String INPUT_STYLE = "    -fx-background-color: linear-gradient(from 0% 130% to 6% 220%, #8998a2 , #8993af);\n"
            + " -fx-background-radius: 18;\n"
            + " -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 0.0, 0.0);"
            + " -fx-text-fill: #dbdbdb; -fx-font-weight: bold;  -fx-prompt-text-fill: #cbcbcb; "
            + "-fx-alignment: center; -fx-font-size: 17;";
    //
    static String BUTTON_STYLE = "-fx-background-color: linear-gradient(to bottom right, #8998a2, #8993af); -fx-background-radius: 15; -fx-font-size: 14;"
            + " -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 0.0, 0.0); -fx-text-fill: #dbdbdb; -fx-font-weight: bold;";
    //
    //
    static String CLOSE_NOT_HOVERED = "-fx-font-size: 9;"
            + "    -fx-background-color: linear-gradient(to bottom right, #bf211d, #ff6561);\n"
            + "    -fx-stroke-width: 20;\n" + "    -fx-background-radius: 50%;\n" + "    -fx-color: white;\n"
            + "    -fx-text-fill : white;";
    static String CLOSE_HOVERED = "-fx-font-size: 9;"
            + "    -fx-background-color: linear-gradient(to bottom right, #bf211d, #ff6561);\n"
            + "    -fx-stroke-width: 20;\n" + "    -fx-background-radius: 50%;\n" + "    -fx-color: white;\n"
            + "    -fx-text-fill : white; -fx-effect: innershadow(gaussian, #656566, 25, 0, 4.12, 4.12);";
}

// sorter class, with style
class Sorter implements STYLE_STRINGS {
    // needed datafields
    Pane root;
    Scene scene;
    Stage stage;
    Button randomizeButton, sortButton, closeButton;
    NumberTextField input;
    Label appLabel;
    static Label cardUnitsLabel;
    static double SORTING_SPEED;
    Card[] cards = null;
    Pane container;
    static ArrayList<Transition> transitions;
    static SequentialTransition sequentialTransition;

    // setter method for static speed of animation
    public static void setSORTING_SPEED(double sORTING_SPEED) {
        SORTING_SPEED = sORTING_SPEED;
    }

    // returns appropiate speed, for different units of cards on the array.
    public static double visuallyPleasingSortingSpeed(int numberOfCards) {
        double pleasingNumber = ((15 * 10)) - 8;
        for (int i = 1; i <= numberOfCards; i++) {
            if (i % 10 == 0)
                pleasingNumber -= 15;
        }
        if (pleasingNumber <= 0.0) {
            if (numberOfCards >= 120)
                pleasingNumber = 4;
            else
                pleasingNumber = 5.5;
        }
        return pleasingNumber;
    }

    Sorter(Stage stage) {
        this.stage = stage;
        root = new Pane();
        container = new Pane();
        input = new NumberTextField();
        randomizeButton = new Button("Randomize");
        sortButton = new Button("Sort");
        closeButton = new Button("X");
        appLabel = new Label("S O R T I N G   V I S U A L I Z E R");
        cardUnitsLabel = new Label("N O D E S  :  " + "0");
        //
        root.setPrefSize(1000, 650);
        makeDraggable(root);
        //
        // label of the app
        appLabel.setStyle(APPLABEL_STYLE);
        appLabel.setLayoutX(80);
        appLabel.setLayoutY(20);
        root.getChildren().add(appLabel);
        //
        // label of each cards
        cardUnitsLabel.setStyle(UNIT_LABEL_STYLE);
        cardUnitsLabel.setLayoutX(805);
        cardUnitsLabel.setLayoutY(70);
        root.getChildren().add(cardUnitsLabel);
        //
        // card container
        container.setPrefSize(1000, 410);
        container.setLayoutY(110);
        container.setStyle(CONTAINER_STYLE);
        root.getChildren().add(container);
        //
        // input bar (numberfield)
        input.setPrefSize(100, 40);
        input.setLayoutX(450);
        input.setLayoutY(530);
        input.setFocusTraversable(false);
        input.setPromptText("1-150");
        input.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(""))
                newValue = "0";
            if (Integer.parseInt(newValue) > 150)
                input.setText(oldValue);
        });
        input.setStyle(INPUT_STYLE);
        root.getChildren().add(input);
        //
        // randomize button
        randomizeButton.setPrefSize(100, 40);
        randomizeButton.setLayoutX(390);
        randomizeButton.setLayoutY(590);
        root.getChildren().add(randomizeButton);
        randomizeButton.setOnAction(e -> {
            // randomize button action
            if (cards != null)
                for (Card current : cards)
                    root.getChildren().remove(current);
            cards = generateArray(input.getText());
            if (cards != null) {
                // unComment to clear input, when randomize button is pressed
                // input.setText("");
                if (cards.length > 0)
                    root.getChildren().addAll(Arrays.asList(cards));
            }
        });
        randomizeButton.setStyle(BUTTON_STYLE);
        //
        // close button
        closeButton.setLayoutX(26.0);
        closeButton.setLayoutY(27.0);
        closeButton.setPrefHeight(22.0);
        closeButton.setPrefWidth(23.0);
        closeButton.setStyle(CLOSE_NOT_HOVERED);
        closeButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            closeButton.setStyle(CLOSE_HOVERED);
        });
        closeButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            closeButton.setStyle(CLOSE_NOT_HOVERED);
        });
        closeButton.setOnAction(e -> {
            stage.close();
        });
        root.getChildren().add(closeButton);
        // sort button
        sortButton.setPrefSize(100, 40);
        sortButton.setLayoutX(510);
        sortButton.setLayoutY(590);
        sortButton.setOnAction(e -> {
            // sort button action
            if (cards != null) {
                input.setText("");
                transitions = new ArrayList<Transition>();
                setSORTING_SPEED(visuallyPleasingSortingSpeed(cards.length));
                // bubble sort optimized implementation
                for (int i = 0; i < cards.length; i++) {
                    boolean swapped = false;
                    for (int j = 0; j < cards.length - i - 1; j++) {
                        if (cards[j].getCardHeight() > cards[j + 1].getCardHeight()) {
                            transitions.add(swap(cards, j, j + 1));
                            swapped = true;
                        }
                    }
                    if (!swapped)
                        break;
                }
                sequentialTransition = new SequentialTransition();
                sequentialTransition.getChildren().addAll(transitions);
                sequentialTransition.play();
            }
            // future note: need to add effecient sorting algorithms.
        });
        sortButton.setStyle(BUTTON_STYLE);
        root.getChildren().add(sortButton);
        root.setStyle(ROOT_STYLE);
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Sorting Visualizer");
        stage.show();
    }

    // returns transtion (to bee added in sequential transition: to animate);
    ParallelTransition swap(Card cards[], int i, int j) {
        ParallelTransition paraTransition = new ParallelTransition();
        double X = (j - i) * cards[i].getCardWidth();
        paraTransition.getChildren().addAll(cards[i].moveX(X), cards[j].moveX(-X));
        //
        Card temp = cards[i];
        cards[i] = cards[j];
        cards[j] = temp;
        return paraTransition;
    }

    // generates and returns array of sizeStr unit of cards, with random height for
    // each of them.
    public static Card[] generateArray(String sizeStr) {
        int size = 0;
        if ((sizeStr.length() < 4) && !sizeStr.equals(""))
            size = Integer.parseInt(sizeStr);
        if (size == 0) {
            size = (int) Math.floor(Math.random() * (50 - 1 + 1) + 1);
        }
        sizeStr = "" + size;
        String unit = "";
        for (int i = 0; i < sizeStr.length(); i++) {
            unit += " " + sizeStr.charAt(i);
        }
        Sorter.cardUnitsLabel.setText("N O D E S  : " + unit);
        Card[] cards = new Card[size];
        double minimum = 1, maximum = 100, height, lx = 5.0;
        double width = 990.0 / (double) size;
        for (int i = 0; i < size; i++) {
            Card current = new Card();
            //
            current.setStyle(CARD_STYLE);
            current.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
                current.setStyle(CARD_STYLE_HOVERED);
            });
            current.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
                current.setStyle(CARD_STYLE);
            });
            height = Math.floor(Math.random() * (maximum - minimum + 1) + minimum);
            //
            if (size <= 30) {
                Label currentLabel = new Label("" + height);
                currentLabel.setStyle(CARD_LABEL_STYLE);
                currentLabel.layoutXProperty()
                        .bind(current.widthProperty().subtract(currentLabel.widthProperty()).divide(2));
                current.getChildren().add(currentLabel);
            }
            //
            current.setPrefHeight(height * 4);
            current.setPrefWidth(width);
            //
            current.setLayoutX(lx);
            current.setLayoutY(115);
            //
            cards[i] = current;
            cards[i].setCardHeight(height);
            cards[i].setCardWidth(width);
            lx += width;
        }
        return cards;
    }

    // makes the scene draggable
    private double x = 350.0, y = 350.0;

    public void makeDraggable(Pane pane) {
        pane.setOnMousePressed((event) -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });
        pane.setOnMouseDragged((event) -> {
            Stage stage = (Stage) pane.getScene().getWindow();
            stage.setX(event.getScreenX() - x);
            stage.setY(event.getScreenY() - y);
        });
    }
}
