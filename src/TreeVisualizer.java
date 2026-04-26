import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class TreeVisualizer extends Application {

    private ArvoreBinariaBusca arvore = new ArvoreBinariaBusca();
    private Canvas canvas;
    private ScrollPane scrollPane;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Ranking de Jogadores - ABB");

        loadCSV("players.csv");

        canvas = new Canvas(1200, 800);
        redrawTree();

        scrollPane = new ScrollPane(new Group(canvas));
        scrollPane.setPrefSize(1200, 600);
        scrollPane.setStyle("-fx-background: #1a1a2e;");

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #1a1a2e;");

        Label title = new Label("Ranking de Jogadores");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #e0e0ff;");

        HBox insertBox = buildInsertBox();
        HBox searchBox = buildSearchBox();
        HBox removeBox = buildRemoveBox();

        root.getChildren().addAll(title, insertBox, searchBox, removeBox, scrollPane);

        Scene scene = new Scene(root, 1200, 750);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox buildInsertBox() {
        TextField nickField = styledField("Nickname");
        TextField rankField = styledField("Ranking");
        Button btn = styledButton("Inserir", "#4CAF50");
        Label result = styledLabel();

        btn.setOnAction(e -> {
            String nick = nickField.getText().trim();
            String rankStr = rankField.getText().trim();
            if (nick.isEmpty() || rankStr.isEmpty()) {
                result.setText("Preencha os dois campos.");
                return;
            }
            try {
                int rank = Integer.parseInt(rankStr);
                arvore.insert(new Player(nick, rank));
                redrawTree();
                result.setText("Jogador inserido!");
                nickField.clear();
                rankField.clear();
            } catch (NumberFormatException ex) {
                result.setText("Ranking deve ser um numero.");
            }
        });

        HBox box = new HBox(8, styledLabel("Inserir:"), nickField, rankField, btn, result);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private HBox buildSearchBox() {
        TextField nickField = styledField("Nickname");
        Button btn = styledButton("Buscar", "#2196F3");
        Label result = styledLabel();

        btn.setOnAction(e -> {
            String nick = nickField.getText().trim();
            if (nick.isEmpty()) { result.setText("Informe o nickname."); return; }
            boolean found = arvore.search(nick);
            result.setText(found ? "Jogador encontrado!" : "Jogador nao encontrado.");
            nickField.clear();
        });

        HBox box = new HBox(8, styledLabel("Buscar:"), nickField, btn, result);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private HBox buildRemoveBox() {
        TextField nickField = styledField("Nickname");
        Button btn = styledButton("Remover", "#f44336");
        Label result = styledLabel();

        btn.setOnAction(e -> {
            String nick = nickField.getText().trim();
            if (nick.isEmpty()) { result.setText("Informe o nickname."); return; }
            Player removed = arvore.remove(nick);
            if (removed != null) {
                redrawTree();
                result.setText("Removido: " + removed.getNickname() + " (rank " + removed.getRanking() + ")");
            } else {
                result.setText("Jogador nao encontrado.");
            }
            nickField.clear();
        });

        HBox box = new HBox(8, styledLabel("Remover:"), nickField, btn, result);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private void loadCSV(String path) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                line = line.replace("\r", "").trim();
                if (line.isEmpty()) continue;
                if (first) { first = false; continue; }
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String nick = parts[0].trim();
                    int rank = Integer.parseInt(parts[1].trim());
                    arvore.insert(new Player(nick, rank));
                }
            }
        } catch (Exception e) {
            showAlert("Erro ao ler o arquivo: " + e.getMessage());
        }
    }

    private void redrawTree() {
        int height = arvore.getHeight();
        double canvasHeight = Math.max(800, 100 + height * 120.0);
        double canvasWidth = Math.max(1200, Math.pow(2, height) * 50.0);

        canvas.setWidth(canvasWidth);
        canvas.setHeight(canvasHeight);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        gc.setFill(Color.web("#1a1a2e"));
        gc.fillRect(0, 0, canvasWidth, canvasHeight);

        if (arvore.root != null) {
            drawNode(gc, arvore.root, canvasWidth / 2, 50, canvasWidth / 4, 1);
        }
    }

    private void drawNode(GraphicsContext gc, Node node, double x, double y, double xOffset, int level) {
        if (node == null) return;

        double radius = 22;

        if (node.left != null) {
            double nx = x - xOffset;
            double ny = y + 120;
            gc.setStroke(Color.web("#7c83d0"));
            gc.setLineWidth(1.5);
            gc.strokeLine(x, y + radius, nx, ny - radius);
            drawNode(gc, node.left, nx, ny, xOffset / 2, level + 1);
        }

        if (node.right != null) {
            double nx = x + xOffset;
            double ny = y + 120;
            gc.setStroke(Color.web("#7c83d0"));
            gc.setLineWidth(1.5);
            gc.strokeLine(x, y + radius, nx, ny - radius);
            drawNode(gc, node.right, nx, ny, xOffset / 2, level + 1);
        }

        gc.setFill(Color.web("#16213e"));
        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        gc.setStroke(Color.web("#e94560"));
        gc.setLineWidth(2);
        gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);

        String nick = node.player.getNickname();
        gc.setFill(Color.web("#e0e0ff"));
        gc.setFont(javafx.scene.text.Font.font("Monospace", 9));

        double textX = x - (nick.length() * 3.2);
        gc.fillText(nick, textX, y + 4);
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(150);
        tf.setStyle("-fx-background-color: #16213e; -fx-text-fill: #e0e0ff; -fx-prompt-text-fill: #888; -fx-border-color: #7c83d0; -fx-border-radius: 4;");
        return tf;
    }

    private Button styledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 4; -fx-background-radius: 4;");
        return btn;
    }

    private Label styledLabel(String... text) {
        Label l = new Label(text.length > 0 ? text[0] : "");
        l.setStyle("-fx-text-fill: #e0e0ff; -fx-font-size: 13px;");
        return l;
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}