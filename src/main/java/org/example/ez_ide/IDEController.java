package org.example.ez_ide;

import Basic.LexerUnit;
import Basic.Parser;
import ExceptionLogic.CompilerException;
import Interpret.ConvertASTToInterpretTreeVisitor;
import Interpret.InterpretTree;
import PrettyPrinters.PrettyPrinterSecond;
import SemanticCheckLogic.SemanticCheck;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.Stack;

public class IDEController implements Initializable {
    @FXML private TextArea codeEditor;
    @FXML private TextArea consoleOutput;
    @FXML private Label statusLabel;
    @FXML private Label fileNameLabel;
    @FXML private Label lineInfoLabel;
    @FXML private Label memoryInfoLabel;
    @FXML private Label compileTimeLabel;
    @FXML private Label runTimeLabel;

    @FXML private Button compileButton;
    @FXML private Button runButton;
    @FXML private Button undoButton;
    @FXML private Button redoButton;
    @FXML private Button saveButton;

    private Stage stage;
    private Stack<String> undoStack;
    private Stack<String> redoStack;
    private File currentFile;
    private boolean isModified = false;

    @Override public void initialize(URL location, ResourceBundle resources) {

    }

    private void setComponent(){
        undoStack = new Stack<>();
        redoStack = new Stack<>();

        saveToUndo();

        codeEditor.setStyle("-fx-font-family: 'Consolas', 'Monospace'; -fx-font-size: 14px;");
        consoleOutput.setStyle("-fx-font-family: 'Consolas', 'Monospace'; -fx-font-size: 12px;");

        updateTitle();
    }

    // Проверка изменений в окне с кодом
    private void setupEventHandler(){
        codeEditor.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!observable.equals(newValue)){
                isModified = true;
                updateTitle();
                saveToUndo();
            }
        });
    }

    // Создание нового файла
    @FXML private void handleNewFile() {
        if (isModified) {
            boolean confirm = showConfirmation("Создать новый файл",
                    "Несохраненные изменения будут потеряны. Продолжить?");
            if (!confirm) return;
        }

        codeEditor.clear();
        currentFile = null;
        isModified = false;
        updateTitle();
        saveToUndo();
        statusLabel.setText("Создан новый файл");
    }

    @FXML private void handleOpenFile() {
        if(isModified){
            boolean confirm =  showConfirmation("Открыть файл",
                    "Несохраненные изменения будут потеряны. Продолжить?");
            if (!confirm) return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть файл");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text File", "*.txt"),
                new FileChooser.ExtensionFilter("All files", "*.*"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try{
                var content = new String(Files.readAllBytes(file.toPath()));
                codeEditor.setText(content);
                currentFile = file;
                isModified = false;
                updateTitle();
                saveToUndo();
                statusLabel.setText("Файл загружен: " + file.getName());
            }
            catch (IOException e) {
                showError("Ошибка загрузки", "Не удалось загрузить файл: " + e.getMessage());
            }
        }
    }

    @FXML private void handleSaveFile() {
        if(currentFile == null)
            handleSaveAsFile();
        else saveToFile(currentFile);
    }

    @FXML private void handleSaveAsFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить файл как");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt"),
                new FileChooser.ExtensionFilter("Все файлы", "*.*")
        );
        File file = fileChooser.showSaveDialog(stage);
        if (file != null)
            saveToFile(file);
    }

    private void saveToFile(File file) {
        try(PrintWriter printWriter = new PrintWriter(file)){
            printWriter.print(codeEditor.getText());
            currentFile = file;
            isModified = false;
            updateTitle();
            statusLabel.setText("Файл сохранен: " + file.getName());
        } catch (IOException e) {
            showError("Ошибка сохранения", "Не удалось сохранить файл: " + e.getMessage());
        }
    }

    @FXML private void handleUndo() {
        if(undoStack.size() > 1){
            redoStack.push(undoStack.pop());
            codeEditor.setText(undoStack.pop());
            statusLabel.setText("Откат выполнен");
        }
    }

    @FXML
    private void handleRedo() {
        if (!redoStack.isEmpty()) {
            String text = redoStack.pop();
            undoStack.push(text);
            codeEditor.setText(text);
            statusLabel.setText("Повтор выполнен");
        }
    }

    @FXML
    private void handleCompile() {
        consoleOutput.appendText("=== КОМПИЛЯЦИЯ ===\n");
        statusLabel.setText("Компиляция...");

        long startTime = System.currentTimeMillis();

        try {
            String code = codeEditor.getText();
            if (code.trim().isEmpty()) {
                consoleOutput.appendText("Ошибка: Пустая программа\n");
                statusLabel.setText("Ошибка компиляции");
                return;
            }

            LexerUnit.Lexer lex = new LexerUnit.Lexer(code);

            try {
                Parser par = new Parser(lex);
                Basic.ASTNodes.StatementNode progr = par.mainProgram();
                progr.visitP(new SemanticCheck());
                InterpretTree.StatementNodeI rooti = (InterpretTree.StatementNodeI) progr.visit(new ConvertASTToInterpretTreeVisitor());
                long endTime = System.currentTimeMillis();
                compileTimeLabel.setText(String.format("Время компиляции: %dms", endTime - startTime));
                consoleOutput.appendText("✓ Компиляция успешно завершена\n");
                consoleOutput.appendText("✓ Лексический анализ: " + lex.tokens.size() + " токенов\n");
                consoleOutput.appendText("✓ Синтаксический анализ: AST построено\n");
                consoleOutput.appendText("✓ Семантический анализ пройден\n");
                consoleOutput.appendText("✓ Дерево интерпретации построено\n");
                statusLabel.setText("Компиляция успешна");

            } catch (CompilerException.LexerException e) {
                consoleOutput.appendText("✗ Лексическая ошибка: " + e.getMessage() + "\n");
                statusLabel.setText("Лексическая ошибка");

            } catch (CompilerException.SyntaxException e) {
                consoleOutput.appendText("✗ Синтаксическая ошибка: " + e.getMessage() + "\n");
                statusLabel.setText("Синтаксическая ошибка");
            }

        } catch (Exception e) {
            consoleOutput.appendText("✗ Ошибка компиляции: " + e.getMessage() + "\n");
            statusLabel.setText("Ошибка компиляции");
        }
        consoleOutput.appendText("\n");
    }

    @FXML
    private void handleRun() {
        consoleOutput.appendText("=== ВЫПОЛНЕНИЕ ===\n");
        statusLabel.setText("Выполнение...");

        long startTime = System.currentTimeMillis();

        try {
            String code = codeEditor.getText();
            if (code.trim().isEmpty()) {
                consoleOutput.appendText("Ошибка: Пустая программа\n");
                statusLabel.setText("Ошибка выполнения");
                return;
            }
            ConsoleCapture consoleCapture = new ConsoleCapture();
            consoleCapture.startCapture();
            LexerUnit.Lexer lex = new LexerUnit.Lexer(code);

            try {
                Parser par = new Parser(lex);
                Basic.ASTNodes.StatementNode progr = par.mainProgram();

                var semanticStart = System.currentTimeMillis();
                progr.visitP(new SemanticCheck());
                var semanticEnd = System.currentTimeMillis();

                var convertStart = System.currentTimeMillis();
                InterpretTree.StatementNodeI rooti = (InterpretTree.StatementNodeI) progr.visit(new ConvertASTToInterpretTreeVisitor());
                var convertEnd = System.currentTimeMillis();

                var executeStart = System.currentTimeMillis();
                rooti.execute();
                var executeEnd = System.currentTimeMillis();

                PrettyPrinterSecond pp = new PrettyPrinterSecond();
                String prettyAST = progr.visit(pp);

                String capturedOutput = consoleCapture.stopCapture();
                if (!capturedOutput.isEmpty()) {
                    consoleOutput.appendText(capturedOutput);
                }

                consoleOutput.appendText("\n--- ИНФОРМАЦИЯ О ВЫПОЛНЕНИИ ---\n");
                consoleOutput.appendText("✓ Семантический анализ: " + (semanticEnd - semanticStart) + "ms\n");
                consoleOutput.appendText("✓ Конвертация AST: " + (convertEnd - convertStart) + "ms\n");
                consoleOutput.appendText("✓ Выполнение: " + (executeEnd - executeStart) + "ms\n");
                consoleOutput.appendText("✓ Общее время: " + (System.currentTimeMillis() - startTime) + "ms\n");

            } catch (CompilerException.LexerException e) {
                String errorOutput = consoleCapture.stopCapture();
                if (!errorOutput.isEmpty()) {
                    consoleOutput.appendText(errorOutput);
                }
                consoleOutput.appendText("✗ Лексическая ошибка: " + e.getMessage() + "\n");
                statusLabel.setText("Лексическая ошибка");
                return;

            } catch (CompilerException.SyntaxException e) {
                String errorOutput = consoleCapture.stopCapture();
                if (!errorOutput.isEmpty()) {
                    consoleOutput.appendText(errorOutput);
                }
                consoleOutput.appendText("✗ Синтаксическая ошибка: " + e.getMessage() + "\n");
                statusLabel.setText("Синтаксическая ошибка");
                return;
            }

            long endTime = System.currentTimeMillis();
            runTimeLabel.setText(String.format("Время выполнения: %dms", endTime - startTime));

            consoleOutput.appendText("✓ Программа выполнена успешно\n");
            statusLabel.setText("Выполнение завершено");

        } catch (Exception e) {
            consoleOutput.appendText("✗ Неожиданная ошибка: " + e.getMessage() + "\n");
            statusLabel.setText("Ошибка выполнения");
            e.printStackTrace();
        }
        consoleOutput.appendText("\n");
    }

    @FXML
    private void handleClearConsole() {
        consoleOutput.clear();
        statusLabel.setText("Консоль очищена");
    }

    @FXML
    private void handleFormat() {
        String code = codeEditor.getText();
        String[] lines = code.split("\n");
        StringBuilder formatted = new StringBuilder();

        for (String line : lines) {
            formatted.append(line.trim()).append("\n");
        }

        codeEditor.setText(formatted.toString().trim());
        statusLabel.setText("Код отформатирован");
    }

    @FXML
    private void updateLineInfo() {
        String text = codeEditor.getText();
        int caretPosition = codeEditor.getCaretPosition();

        // Вычисляем строку и колонку
        String textBeforeCaret = text.substring(0, caretPosition);
        int line = textBeforeCaret.split("\n", -1).length;
        int column = caretPosition - textBeforeCaret.lastIndexOf('\n');

        lineInfoLabel.setText(String.format("Строка: %d, Колонка: %d", line, column));
    }

    @FXML
    private void insertTemplate() {
        String template = """
        // Пример программы на языке
        x = 10;
        y = 20;
        sum = x + y;
        print(sum);

        if sum > 15 then
            print("Сумма больше 15");
        else
            print("Сумма меньше или равна 15");

        i = 0;
        while i < 5 do {
            print(i);
            i = i + 1;
        }

        for (i = 0; i < 3; i = i + 1) do
            print(i * 2);
        """;

        codeEditor.setText(template);
        statusLabel.setText("Вставлен шаблон программы");
    }



    private void saveToUndo() {
        undoStack.push(codeEditor.getText());
        redoStack.clear();
        updateUndoRedoButtons();
    }

    private void updateUndoRedoButtons() {
        undoButton.setDisable(undoStack.size() <= 1);
        redoButton.setDisable(redoStack.isEmpty());
    }

    private void updateTitle() {
        String title = (currentFile != null) ? currentFile.getName() : "Безымянный файл";
        if (isModified)
            title += " *"; // Добавляем звездочку если есть несохраненные изменения
        fileNameLabel.setText(title);
    }

    // Вывод ошибки
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}