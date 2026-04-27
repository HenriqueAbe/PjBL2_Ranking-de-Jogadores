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
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class TreeVisualizer extends Application {

    private ArvoreBinariaBusca arvore = new ArvoreBinariaBusca();
    private Canvas canvas;
    private ScrollPane scrollPane;
    private Label statsLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Ranking de Jogadores - ABB");
        loadCSV("players.csv");

        canvas = new Canvas(1200, 800);
        statsLabel = styledLabel("");
        updateStats();
        redrawTree();

        scrollPane = new ScrollPane(new Group(canvas));
        scrollPane.setPrefSize(1200, 600);
        scrollPane.setStyle("-fx-background: #1a1a2e;");

        VBox rootLayout = new VBox(10);
        rootLayout.setPadding(new Insets(15));
        rootLayout.setStyle("-fx-background-color: #1a1a2e;");

        Label title = new Label("Ranking de Jogadores");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #e0e0ff;");

        HBox controls = new HBox(15, buildInsertBox(), buildSearchBox(), buildRemoveBox(), buildUpdateBox(), buildRankingBox());
        controls.setAlignment(Pos.CENTER_LEFT);

        rootLayout.getChildren().addAll(title, statsLabel, controls, scrollPane);

        Scene scene = new Scene(rootLayout, 1250, 750);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateStats() {
        int total = arvore.getTotalPlayers();
        Player maior = arvore.buscarMaiorRanking();
        String texto = "Total de Jogadores: " + total;
        if (maior != null) {
            texto += " | Melhor Player: " + maior.getNickname() + " (" + maior.getRanking() + ")";
        }
        statsLabel.setText(texto);
    }

    private VBox buildInsertBox() {
        TextField nickField = styledField("Nickname");
        TextField rankField = styledField("Ranking");
        Button btn = styledButton("Inserir", "#4CAF50");
        btn.setOnAction(e -> {
            try {
                String nick = nickField.getText();
                int rank = Integer.parseInt(rankField.getText());
                if (arvore.existeRanking(rank)) {
                    showAlert("Ja existe um jogador cadastrado com o ranking: " + rank);
                } else {
                    arvore.insert(new Player(nick, rank));
                    redrawTree();
                    updateStats();
                    nickField.clear();
                    rankField.clear();
                }
            } catch (Exception ex) {
                showAlert("Entrada invalida.");
            }
        });
        return new VBox(5, styledLabel("Inserir:"), nickField, rankField, btn);
    }

    private VBox buildSearchBox() {
        TextField nickField = styledField("Nickname");
        Button btn = styledButton("Buscar", "#2196F3");
        Label res = styledLabel("");
        btn.setOnAction(e -> {
            String name = nickField.getText();
            if (arvore.search(name)) {
                int depth = arvore.getDepth(name);
                int height = arvore.getNodeHeight(name);
                res.setText("Localizado!\nProfundidade: " + depth + " | Altura: " + height);
            } else {
                res.setText("Nao encontrado.");
            }
        });
        return new VBox(5, styledLabel("Buscar:"), nickField, btn, res);
    }

    private VBox buildUpdateBox() {
        TextField nickField = styledField("Nickname");
        TextField newRankField = styledField("Novo Ranking");
        Button btn = styledButton("Atualizar", "#FF9800");
        btn.setOnAction(e -> {
            try {
                String nick = nickField.getText();
                int newRank = Integer.parseInt(newRankField.getText());
                if (!arvore.search(nick)) {
                    showAlert("Jogador nao encontrado.");
                } else if (arvore.existeRanking(newRank)) {
                    showAlert("Este ranking ja esta ocupado.");
                } else {
                    arvore.atualizarRanking(nick, newRank);
                    redrawTree();
                    updateStats();
                    nickField.clear();
                    newRankField.clear();
                }
            } catch (Exception ex) {
                showAlert("Entrada invalida.");
            }
        });
        return new VBox(5, styledLabel("Atualizar Rank:"), nickField, newRankField, btn);
    }

    private VBox buildRemoveBox() {
        TextField nickField = styledField("Nickname");
        Button btn = styledButton("Remover", "#f44336");
        btn.setOnAction(e -> {
            arvore.remove(nickField.getText());
            redrawTree();
            updateStats();
            nickField.clear();
        });
        return new VBox(5, styledLabel("Remover:"), nickField, btn);
    }

    private VBox buildRankingBox() {
        Button btn = styledButton("Ver Ranking", "#9c27b0");
        btn.setOnAction(e -> {
            Lista ranking = arvore.gerarRankingList();
            ranking.printRanking();
        });
        return new VBox(5, styledLabel("Console:"), btn);
    }

    private void loadCSV(String path) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    arvore.insert(new Player(parts[0].trim(), Integer.parseInt(parts[1].trim())));
                }
            }
        } catch (Exception e) {
            System.out.println("Erro CSV: " + e.getMessage());
        }
    }

    private void redrawTree() {
        double canvasWidth = Math.max(1200, Math.pow(2, arvore.getHeight()) * 40);
        canvas.setWidth(canvasWidth);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.web("#1a1a2e"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        if (arvore.root != null) drawNode(gc, arvore.root, canvasWidth / 2, 50, canvasWidth / 4);
    }

    private void drawNode(GraphicsContext gc, Node node, double x, double y, double xOffset) {
        if (node == null) return;
        double r = 20;
        gc.setStroke(Color.web("#7c83d0"));
        if (node.left != null) {
            gc.strokeLine(x, y, x - xOffset, y + 80);
            drawNode(gc, node.left, x - xOffset, y + 80, xOffset / 2);
        }
        if (node.right != null) {
            gc.strokeLine(x, y, x + xOffset, y + 80);
            drawNode(gc, node.right, x + xOffset, y + 80, xOffset / 2);
        }
        gc.setFill(Color.web("#16213e"));
        gc.fillOval(x - r, y - r, r * 2, r * 2);
        gc.setStroke(Color.web("#e94560"));
        gc.strokeOval(x - r, y - r, r * 2, r * 2);
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(10));
        gc.fillText(node.player.getNickname(), x - 15, y + 5);
    }

    private TextField styledField(String p) {
        TextField tf = new TextField();
        tf.setPromptText(p);
        tf.setStyle("-fx-background-color: #16213e; -fx-text-fill: white; -fx-border-color: #7c83d0;");
        return tf;
    }

    private Button styledButton(String t, String c) {
        Button b = new Button(t);
        b.setStyle("-fx-background-color: " + c + "; -fx-text-fill: white; -fx-cursor: hand;");
        return b;
    }

    private Label styledLabel(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-text-fill: #e0e0ff;");
        return l;
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}