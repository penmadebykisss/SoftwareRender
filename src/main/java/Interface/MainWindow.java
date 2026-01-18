package Interface;

import Interface.model.Model;
import Interface.model.ModelManager;
import Interface.model.ModelTransformer;
import Interface.objreader.ObjReader;
import Interface.objreader.ObjReaderException;
import Interface.objwriter.ObjWriter;
import Interface.objwriter.ObjWriterException;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MainWindow {
    private Stage stage;
    private Scene scene;
    private BorderPane root;
    private boolean isDarkTheme = true;

    // Элементы интерфейса
    private MenuBar menuBar;
    private ToolBar toolBar;
    private VBox sidebar;
    private Pane viewport;
    private HBox statusBar;

    // Canvas для отрисовки 3D
    private Canvas canvas;
    private SceneRenderer renderer;

    // Управление моделями и камерами
    private ModelManager modelManager;
    private CameraManager cameraManager;
    private ModelTransformer modelTransformer;

    // Поля трансформации
    private TextField translateX, translateY, translateZ;
    private TextField rotateX, rotateY, rotateZ;
    private TextField scaleX, scaleY, scaleZ;

    // ComboBox для списка моделей и камер
    private ComboBox<ModelManager.ModelEntry> toolbarModelList;
    private ComboBox<CameraManager.CameraEntry> cameraComboBox;

    // Статус бар элементы
    private Label modelInfoLabel;
    private Label renderModeLabel;
    private Label cameraLabel;
    private Label cursorLabel;

    private File lastSavedFile;

    public MainWindow(Stage stage) {
        this.stage = stage;
        this.modelManager = new ModelManager();
        this.cameraManager = new CameraManager();
        this.modelTransformer = new ModelTransformer();
        initUI();
    }

    private void initUI() {
        root = new BorderPane();

        // Создание компонентов
        createMenuBar();
        createToolBar();
        createSidebar();
        createViewport();
        createStatusBar();

        // Размещение компонентов
        root.setTop(new VBox(menuBar, toolBar));
        root.setLeft(sidebar);
        root.setCenter(viewport);
        root.setBottom(statusBar);

        // Создание сцены
        scene = new Scene(root, 1200, 800);
        applyTheme();

        stage.setTitle("3D Редактор Моделей");
        stage.setScene(scene);

        // Обновляем отрисовку при запуске
        updateScene();
    }

    private void createMenuBar() {
        menuBar = new MenuBar();

        // Меню "Файл"
        Menu fileMenu = new Menu("Файл");
        MenuItem openItem = new MenuItem("Открыть модель");
        MenuItem saveItem = new MenuItem("Сохранить модель");
        MenuItem saveAsItem = new MenuItem("Сохранить как");
        MenuItem exitItem = new MenuItem("Выход");

        openItem.setOnAction(e -> openModel());
        saveItem.setOnAction(e -> saveModel());
        saveAsItem.setOnAction(e -> saveModelAs());
        exitItem.setOnAction(e -> stage.close());

        fileMenu.getItems().addAll(openItem, saveItem, saveAsItem, new SeparatorMenuItem(), exitItem);

        // Меню "Режимы отрисовки"
        Menu renderMenu = new Menu("Режимы отрисовки");
        CheckMenuItem wireframeItem = new CheckMenuItem("Рисовать полигональную сетку (Wireframe)");
        CheckMenuItem filledItem = new CheckMenuItem("Заливка полигонов");
        CheckMenuItem textureItem = new CheckMenuItem("Использовать текстуру (Texture)");
        CheckMenuItem lightingItem = new CheckMenuItem("Использовать освещение (Lighting)");
        CheckMenuItem zBufferItem = new CheckMenuItem("Использовать Z-буфер (Z-Buffer)");

        wireframeItem.setSelected(true);
        filledItem.setSelected(false);
        textureItem.setSelected(false);
        lightingItem.setSelected(false);
        zBufferItem.setSelected(false);

        wireframeItem.setOnAction(e -> {
            renderer.setDrawWireframe(wireframeItem.isSelected());
            updateScene();
            updateRenderModeLabel();
        });

        filledItem.setOnAction(e -> {
            renderer.setDrawFilled(filledItem.isSelected());
            updateScene();
            updateRenderModeLabel();
        });

        renderMenu.getItems().addAll(wireframeItem, filledItem, textureItem, lightingItem, zBufferItem);

        // Меню "Камеры"
        Menu cameraMenu = new Menu("Камеры");
        MenuItem addCameraItem = new MenuItem("Добавить камеру");
        MenuItem removeCameraItem = new MenuItem("Удалить камеру");
        MenuItem switchCameraItem = new MenuItem("Переключиться на камеру");
        MenuItem showHideCameraItem = new MenuItem("Показать/скрыть камеру в сцене");

        addCameraItem.setOnAction(e -> addCamera());
        removeCameraItem.setOnAction(e -> removeCamera());

        cameraMenu.getItems().addAll(addCameraItem, removeCameraItem, switchCameraItem, showHideCameraItem);

        // Меню "Настройки"
        Menu settingsMenu = new Menu("Настройки");
        MenuItem themeItem = new MenuItem("Тема: Светлая / Тёмная");
        themeItem.setOnAction(e -> toggleTheme());

        settingsMenu.getItems().add(themeItem);

        menuBar.getMenus().addAll(fileMenu, renderMenu, cameraMenu, settingsMenu);
    }

    private void createToolBar() {
        toolBar = new ToolBar();

        // Кнопки управления сценой
        Button addModelBtn = new Button("Добавить модель");
        Button removeModelBtn = new Button("Удалить модель");
        toolbarModelList = new ComboBox<>();
        toolbarModelList.setPromptText("Список моделей");
        toolbarModelList.setPrefWidth(150);
        Button clearSceneBtn = new Button("Очистить сцену");

        addModelBtn.setOnAction(e -> openModel());
        removeModelBtn.setOnAction(e -> removeSelectedModel());
        clearSceneBtn.setOnAction(e -> clearScene());

        toolbarModelList.setOnAction(e -> {
            ModelManager.ModelEntry selected = toolbarModelList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                modelManager.setSelectedModel(selected.getId());
                updateStatusBar();
            }
        });

        toolBar.getItems().addAll(
                addModelBtn,
                removeModelBtn,
                toolbarModelList,
                clearSceneBtn,
                new Separator()
        );
    }

    private void createSidebar() {
        sidebar = new VBox(10);
        sidebar.setPadding(new Insets(10));
        sidebar.setPrefWidth(250);

        // Секция трансформации
        TitledPane transformPane = new TitledPane();
        transformPane.setText("Управление выбранной моделью");
        transformPane.setExpanded(true);

        VBox transformBox = new VBox(10);

        // Перемещение
        Label translateLabel = new Label("Перемещение (Translation):");
        translateLabel.setStyle("-fx-font-weight: bold;");
        HBox translateInputBox = new HBox(5);
        translateX = new TextField("0");
        translateY = new TextField("0");
        translateZ = new TextField("0");
        translateX.setPrefWidth(60);
        translateY.setPrefWidth(60);
        translateZ.setPrefWidth(60);
        translateInputBox.getChildren().addAll(
                new Label("X:"), translateX,
                new Label("Y:"), translateY,
                new Label("Z:"), translateZ
        );

        HBox translateBtns = new HBox(5);
        Button applyTranslateBtn = new Button("Применить");
        Button resetTranslateBtn = new Button("Сброс");
        applyTranslateBtn.setOnAction(e -> applyTranslation());
        resetTranslateBtn.setOnAction(e -> {
            translateX.setText("0");
            translateY.setText("0");
            translateZ.setText("0");
        });
        translateBtns.getChildren().addAll(applyTranslateBtn, resetTranslateBtn);

        // Вращение
        Label rotateLabel = new Label("Вращение (Rotation):");
        rotateLabel.setStyle("-fx-font-weight: bold;");
        HBox rotateInputBox = new HBox(5);
        rotateX = new TextField("0");
        rotateY = new TextField("0");
        rotateZ = new TextField("0");
        rotateX.setPrefWidth(60);
        rotateY.setPrefWidth(60);
        rotateZ.setPrefWidth(60);
        rotateInputBox.getChildren().addAll(
                new Label("X:"), rotateX,
                new Label("Y:"), rotateY,
                new Label("Z:"), rotateZ
        );

        HBox rotateBtns = new HBox(5);
        Button applyRotateBtn = new Button("Применить");
        Button resetRotateBtn = new Button("Сброс");
        applyRotateBtn.setOnAction(e -> applyRotation());
        resetRotateBtn.setOnAction(e -> {
            rotateX.setText("0");
            rotateY.setText("0");
            rotateZ.setText("0");
        });
        rotateBtns.getChildren().addAll(applyRotateBtn, resetRotateBtn);

        // Масштабирование
        Label scaleLabel = new Label("Масштабирование (Scaling):");
        scaleLabel.setStyle("-fx-font-weight: bold;");
        HBox scaleInputBox = new HBox(5);
        scaleX = new TextField("1");
        scaleY = new TextField("1");
        scaleZ = new TextField("1");
        scaleX.setPrefWidth(60);
        scaleY.setPrefWidth(60);
        scaleZ.setPrefWidth(60);
        scaleInputBox.getChildren().addAll(
                new Label("X:"), scaleX,
                new Label("Y:"), scaleY,
                new Label("Z:"), scaleZ
        );

        HBox scaleBtns = new HBox(5);
        Button applyScaleBtn = new Button("Применить");
        Button resetScaleBtn = new Button("Сброс");

        applyScaleBtn.setOnAction(e -> applyScaling());
        resetScaleBtn.setOnAction(e -> {
            scaleX.setText("1");
            scaleY.setText("1");
            scaleZ.setText("1");
        });
        scaleBtns.getChildren().addAll(applyScaleBtn, resetScaleBtn);

        transformBox.getChildren().addAll(
                translateLabel, translateInputBox, translateBtns,
                new Separator(),
                rotateLabel, rotateInputBox, rotateBtns,
                new Separator(),
                scaleLabel, scaleInputBox, scaleBtns
        );

        transformPane.setContent(transformBox);

        // Секция удаления элементов
        TitledPane deletePane = new TitledPane();
        deletePane.setText("Удаление элементов модели");
        deletePane.setExpanded(false);

        VBox deleteBox = new VBox(10);
        Button deleteModeBtn = new Button("Режим удаления вершин/полигонов");
        ComboBox<String> deleteTypeCombo = new ComboBox<>();
        deleteTypeCombo.getItems().addAll("Вершина (Vertex)", "Полигон (Polygon)");
        deleteTypeCombo.setValue("Вершина (Vertex)");
        Button deleteSelectedBtn = new Button("Удалить выбранное");

        deleteBox.getChildren().addAll(deleteModeBtn, deleteTypeCombo, deleteSelectedBtn);
        deletePane.setContent(deleteBox);

        // Секция настроек отрисовки
        TitledPane renderPane = new TitledPane();
        renderPane.setText("Настройки отрисовки");
        renderPane.setExpanded(false);

        VBox renderBox = new VBox(10);
        Button colorPickerBtn = new Button("Выбор цвета модели");
        Button loadTextureBtn = new Button("Выбор текстуры");
        Slider lightIntensitySlider = new Slider(0, 100, 50);
        lightIntensitySlider.setShowTickLabels(true);
        lightIntensitySlider.setShowTickMarks(true);
        Label lightLabel = new Label("Яркость освещения:");

        CheckBox smoothNormalsCheckBox = new CheckBox("Сглаживание нормалей");

        renderBox.getChildren().addAll(
                colorPickerBtn,
                loadTextureBtn,
                lightLabel,
                lightIntensitySlider,
                smoothNormalsCheckBox
        );
        renderPane.setContent(renderBox);

        // Секция управления камерой
        TitledPane cameraPane = new TitledPane();
        cameraPane.setText("Управление камерой");
        cameraPane.setExpanded(false);

        VBox cameraBox = new VBox(10);
        cameraComboBox = new ComboBox<>();
        cameraComboBox.setPromptText("Выбор активной камеры");
        cameraComboBox.setPrefWidth(220);

        cameraComboBox.setOnAction(e -> switchCamera());

        Button addCamBtn = new Button("Добавить камеру");
        Button removeCamBtn = new Button("Удалить камеру");
        Button resetCamBtn = new Button("Сброс камеры");
        Button attachLightBtn = new Button("Привязать свет к камере");

        addCamBtn.setOnAction(e -> addCamera());
        removeCamBtn.setOnAction(e -> removeCamera());
        resetCamBtn.setOnAction(e -> resetCamera());

        cameraBox.getChildren().addAll(cameraComboBox, addCamBtn, removeCamBtn, resetCamBtn, attachLightBtn);
        cameraPane.setContent(cameraBox);

        sidebar.getChildren().addAll(
                transformPane,
                deletePane,
                renderPane,
                cameraPane
        );

        ScrollPane scrollPane = new ScrollPane(sidebar);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        sidebar = new VBox(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Обновляем список камер
        updateCameraList();
    }

    private void createViewport() {
        viewport = new Pane();
        viewport.setStyle("-fx-background-color: #2b2b2b;");

        // Создаём Canvas для отрисовки
        canvas = new Canvas(800, 600);
        renderer = new SceneRenderer(canvas);

        // Привязываем размер canvas к размеру viewport
        canvas.widthProperty().bind(viewport.widthProperty());
        canvas.heightProperty().bind(viewport.heightProperty());

        // Перерисовываем при изменении размера
        canvas.widthProperty().addListener((obs, oldVal, newVal) -> updateScene());
        canvas.heightProperty().addListener((obs, oldVal, newVal) -> updateScene());

        viewport.getChildren().add(canvas);

        // Добавление отслеживания курсора
        viewport.setOnMouseMoved(e -> {
            cursorLabel.setText(String.format("Координаты: X:%.0f, Y:%.0f", e.getX(), e.getY()));
        });
    }

    private void createStatusBar() {
        statusBar = new HBox(20);
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        statusBar.setStyle("-fx-background-color: #3c3c3c;");

        modelInfoLabel = new Label("Модель: не загружена");
        renderModeLabel = new Label("Режим: Wireframe");
        cameraLabel = new Label("Камера: Default Camera");
        cursorLabel = new Label("Координаты: X:0, Y:0");

        Region spacer1 = new Region();
        Region spacer2 = new Region();
        Region spacer3 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        HBox.setHgrow(spacer3, Priority.ALWAYS);

        statusBar.getChildren().addAll(
                modelInfoLabel,
                spacer1,
                renderModeLabel,
                spacer2,
                cameraLabel,
                spacer3,
                cursorLabel
        );
    }

    // Методы трансформации
    private void applyTranslation() {
        ModelManager.ModelEntry selected = modelManager.getSelectedModel();
        if (selected == null) {
            showError("Нет модели", "Выберите модель для трансформации");
            return;
        }

        try {
            float tx = Float.parseFloat(translateX.getText());
            float ty = Float.parseFloat(translateY.getText());
            float tz = Float.parseFloat(translateZ.getText());

            modelTransformer.translate(selected.getModel(), tx, ty, tz);
            updateScene();

            // Сбрасываем значения
            translateX.setText("0");
            translateY.setText("0");
            translateZ.setText("0");
        } catch (NumberFormatException e) {
            showError("Ошибка ввода", "Введите корректные числовые значения");
        }
    }

    private void applyRotation() {
        ModelManager.ModelEntry selected = modelManager.getSelectedModel();
        if (selected == null) {
            showError("Нет модели", "Выберите модель для трансформации");
            return;
        }

        try {
            float rx = Float.parseFloat(rotateX.getText());
            float ry = Float.parseFloat(rotateY.getText());
            float rz = Float.parseFloat(rotateZ.getText());

            modelTransformer.rotate(selected.getModel(), rx, ry, rz);
            updateScene();

            // Сбрасываем значения
            rotateX.setText("0");
            rotateY.setText("0");
            rotateZ.setText("0");
        } catch (NumberFormatException e) {
            showError("Ошибка ввода", "Введите корректные числовые значения");
        }
    }

    private void applyScaling() {
        ModelManager.ModelEntry selected = modelManager.getSelectedModel();
        if (selected == null) {
            showError("Нет модели", "Выберите модель для трансформации");
            return;
        }

        try {
            float sx = Float.parseFloat(scaleX.getText());
            float sy = Float.parseFloat(scaleY.getText());
            float sz = Float.parseFloat(scaleZ.getText());

            if (sx == 0 || sy == 0 || sz == 0) {
                showError("Ошибка масштаба", "Масштаб не может быть равен 0");
                return;
            }

            modelTransformer.scale(selected.getModel(), sx, sy, sz);
            updateScene();

            // Сбрасываем значения
            scaleX.setText("1");
            scaleY.setText("1");
            scaleZ.setText("1");
        } catch (NumberFormatException e) {
            showError("Ошибка ввода", "Введите корректные числовые значения");
        }
    }

    // Методы работы с камерами
    private void addCamera() {
        TextInputDialog dialog = new TextInputDialog("Camera " + (cameraManager.getCameraCount() + 1));
        dialog.setTitle("Добавить камеру");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        dialog.setContentText("Введите название камеры:");

        // Применяем тему к диалогу
        applyThemeToDialog(dialog.getDialogPane());

        dialog.showAndWait().ifPresent(name -> {
            cameraManager.addCamera(name);
            updateCameraList();
            updateStatusBar();
        });
    }

    private void removeCamera() {
        CameraManager.CameraEntry selected = cameraComboBox.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Нет камеры", "Выберите камеру для удаления");
            return;
        }

        if (cameraManager.getCameraCount() <= 1) {
            showError("Нельзя удалить", "Нельзя удалить последнюю камеру");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setHeaderText(null);
        confirm.setGraphic(null);
        confirm.setContentText("Вы действительно хотите удалить камеру: " + selected.getName() + "?");

        // Применяем тему к диалогу
        applyThemeToDialog(confirm.getDialogPane());

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                cameraManager.removeCamera(selected.getId());
                updateCameraList();
                updateStatusBar();
                updateScene();
            }
        });
    }

    private void switchCamera() {
        CameraManager.CameraEntry selected = cameraComboBox.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cameraManager.setActiveCamera(selected.getId());
            updateStatusBar();
            updateScene();
        }
    }

    private void resetCamera() {
        cameraManager.resetActiveCamera();
        updateScene();
    }

    private void updateCameraList() {
        cameraComboBox.getItems().clear();
        cameraComboBox.getItems().addAll(cameraManager.getAllCameras());

        CameraManager.CameraEntry active = cameraManager.getActiveCamera();
        if (active != null) {
            cameraComboBox.getSelectionModel().select(active);
        }
    }

    // Методы работы с моделями
    private void openModel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть модель");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("OBJ Files", "*.obj")
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                String content = Files.readString(file.toPath());
                Model model = ObjReader.read(content);

                String modelName = file.getName().replace(".obj", "");
                modelManager.addModel(model, modelName);

                updateModelList();
                updateStatusBar();
                updateScene();

                showInfo("Открытие модели", "Модель успешно загружена: " + file.getName() +
                        "\nВершин: " + model.getVertices().size() +
                        "\nПолигонов: " + model.getPolygons().size());
            } catch (IOException e) {
                showError("Ошибка чтения файла", "Не удалось прочитать файл: " + e.getMessage());
            } catch (ObjReaderException e) {
                showError("Ошибка парсинга OBJ", e.getMessage());
            }
        }
    }

    private void saveModel() {
        if (modelManager.isEmpty()) {
            showError("Нет модели", "Нет загруженной модели для сохранения");
            return;
        }

        if (lastSavedFile != null) {
            saveModelToFile(lastSavedFile);
        } else {
            saveModelAs();
        }
    }

    private void saveModelAs() {
        if (modelManager.isEmpty()) {
            showError("Нет модели", "Нет загруженной модели для сохранения");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить модель как");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("OBJ Files", "*.obj")
        );

        ModelManager.ModelEntry selected = modelManager.getSelectedModel();
        if (selected != null) {
            fileChooser.setInitialFileName(selected.getName() + ".obj");
        }

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            saveModelToFile(file);
            lastSavedFile = file;
        }
    }

    private void saveModelToFile(File file) {
        try {
            ModelManager.ModelEntry selected = modelManager.getSelectedModel();
            if (selected != null) {
                ObjWriter.write(selected.getModel(), file.getAbsolutePath());
                showInfo("Сохранение", "Модель успешно сохранена: " + file.getName());
            }
        } catch (IOException e) {
            showError("Ошибка записи", "Не удалось сохранить файл: " + e.getMessage());
        } catch (ObjWriterException e) {
            showError("Ошибка записи OBJ", e.getMessage());
        }
    }

    private void removeSelectedModel() {
        ModelManager.ModelEntry selected = modelManager.getSelectedModel();
        if (selected != null) {
            modelManager.removeModel(selected.getId());
            updateModelList();
            updateStatusBar();
            updateScene();
        }
    }

    private void clearScene() {
        modelManager.clearAll();
        updateModelList();
        updateStatusBar();
        updateScene();
        lastSavedFile = null;
    }

    private void updateModelList() {
        toolbarModelList.getItems().clear();
        toolbarModelList.getItems().addAll(modelManager.getAllModels());

        ModelManager.ModelEntry selected = modelManager.getSelectedModel();
        if (selected != null) {
            toolbarModelList.getSelectionModel().select(selected);
        }
    }

    private void updateStatusBar() {
        if (modelManager.isEmpty()) {
            modelInfoLabel.setText("Модель: не загружена");
        } else {
            ModelManager.ModelEntry selected = modelManager.getSelectedModel();
            if (selected != null) {
                modelInfoLabel.setText("Модели: " + modelManager.getModelCount() +
                        " | Активная: " + selected.getName());
            }
        }

        CameraManager.CameraEntry activeCamera = cameraManager.getActiveCamera();
        if (activeCamera != null) {
            cameraLabel.setText("Камера: " + activeCamera.getName());
        }
    }

    private void updateRenderModeLabel() {
        String mode = "";
        if (renderer.isDrawWireframe()) mode += "Wireframe ";
        if (renderer.isDrawFilled()) mode += "Filled ";
        if (mode.isEmpty()) mode = "None";

        renderModeLabel.setText("Режим: " + mode.trim());
    }

    private void updateScene() {
        renderer.renderScene(modelManager, cameraManager);
    }

    private void toggleTheme() {
        isDarkTheme = !isDarkTheme;
        applyTheme();
        updateScene();
    }

    private void applyTheme() {
        if (isDarkTheme) {
            renderer.setBackgroundColor(Color.rgb(43, 43, 43));
            // Тёмная тема
            root.setStyle("-fx-background-color: #1e1e1e;");
            menuBar.setStyle("-fx-background-color: #2d2d2d;");
            for (Menu menu : menuBar.getMenus()) {
                menu.setStyle("-fx-text-fill: white;");
            }
            toolBar.setStyle("-fx-background-color: #2d2d2d;");
            toolBar.getItems().forEach(item -> {
                if (item instanceof Button) {
                    item.setStyle("-fx-text-fill: white; -fx-background-color: #3c3c3c;");
                } else if (item instanceof ComboBox) {
                    item.setStyle("-fx-background-color: #3c3c3c; -fx-text-fill: white;");
                }
            });
            sidebar.setStyle("-fx-background-color: #252526;");
            viewport.setStyle("-fx-background-color: #2b2b2b;");
            statusBar.setStyle("-fx-background-color: #3c3c3c;");

            modelInfoLabel.setStyle("-fx-text-fill: white;");
            renderModeLabel.setStyle("-fx-text-fill: white;");
            cameraLabel.setStyle("-fx-text-fill: white;");
            cursorLabel.setStyle("-fx-text-fill: white;");

            scene.getStylesheets().clear();
            String css =
                    ".root { -fx-background-color: #1e1e1e; } " +
                            ".menu-bar { -fx-background-color: #2d2d2d; } " +
                            ".menu-bar .label { -fx-text-fill: white; } " +
                            ".menu-bar .menu .label { -fx-text-fill: white; } " +
                            ".menu-item { -fx-background-color: #2d2d2d; } " +
                            ".menu-item .label { -fx-text-fill: white; } " +
                            ".menu-item:focused { -fx-background-color: #3c3c3c; } " +
                            ".menu-item:hover { -fx-background-color: #3c3c3c; } " +
                            ".context-menu { -fx-background-color: #2d2d2d; } " +
                            ".check-menu-item { -fx-background-color: #2d2d2d; } " +
                            ".check-menu-item .label { -fx-text-fill: white; } " +
                            ".separator .line { -fx-border-color: #4c4c4c; } " +
                            ".button { -fx-background-color: #3c3c3c; -fx-text-fill: white; } " +
                            ".button:hover { -fx-background-color: #4c4c4c; } " +
                            ".text-field { -fx-background-color: #3c3c3c; -fx-text-fill: white; -fx-prompt-text-fill: #888888; } " +
                            ".combo-box { -fx-background-color: #3c3c3c; } " +
                            ".combo-box .list-cell { -fx-background-color: #2d2d2d; -fx-text-fill: white; } " +
                            ".combo-box-popup .list-view { -fx-background-color: #2d2d2d; } " +
                            ".combo-box .arrow-button { -fx-background-color: #3c3c3c; } " +
                            ".combo-box .text-input { -fx-text-fill: white; } " +
                            ".combo-box:editable .text-field { -fx-text-fill: white; } " +
                            ".titled-pane { -fx-text-fill: white; -fx-background-color: #252526; } " +
                            ".titled-pane > .title { -fx-background-color: #2d2d2d; -fx-text-fill: white; } " +
                            ".titled-pane > .content { -fx-background-color: #252526; } " +
                            ".label { -fx-text-fill: white; } " +
                            ".check-box { -fx-text-fill: white; } " +
                            ".check-box .box { -fx-background-color: #3c3c3c; } " +
                            ".slider { -fx-text-fill: white; } " +
                            ".scroll-pane { -fx-background-color: #252526; } " +
                            ".scroll-pane > .viewport { -fx-background-color: #252526; } ";

            scene.getStylesheets().add("data:text/css;base64," +
                    java.util.Base64.getEncoder().encodeToString(css.getBytes()));
        } else {
            renderer.setBackgroundColor(Color.rgb(208, 208, 208));
            // Светлая тема
            root.setStyle("-fx-background-color: #f0f0f0;");
            menuBar.setStyle("-fx-background-color: #e0e0e0;");
            for (Menu menu : menuBar.getMenus()) {
                menu.setStyle("-fx-text-fill: black;");
            }
            toolBar.setStyle("-fx-background-color: #e0e0e0;");
            toolBar.getItems().forEach(item -> {
                if (item instanceof Button) {
                    item.setStyle("-fx-text-fill: black; -fx-background-color: #d0d0d0;");
                } else if (item instanceof ComboBox) {
                    item.setStyle("-fx-background-color: #d0d0d0; -fx-text-fill: black;");
                }
            });
            sidebar.setStyle("-fx-background-color: #f5f5f5;");
            viewport.setStyle("-fx-background-color: #d0d0d0;");
            statusBar.setStyle("-fx-background-color: #c0c0c0;");

            modelInfoLabel.setStyle("-fx-text-fill: black;");
            renderModeLabel.setStyle("-fx-text-fill: black;");
            cameraLabel.setStyle("-fx-text-fill: black;");
            cursorLabel.setStyle("-fx-text-fill: black;");

            scene.getStylesheets().clear();
            String css =
                    ".root { -fx-background-color: #f0f0f0; } " +
                            ".menu-bar { -fx-background-color: #e0e0e0; } " +
                            ".menu-bar .label { -fx-text-fill: black; } " +
                            ".menu-bar .menu .label { -fx-text-fill: black; } " +
                            ".menu-item { -fx-background-color: #f0f0f0; } " +
                            ".menu-item .label { -fx-text-fill: black; } " +
                            ".menu-item:focused { -fx-background-color: #e0e0e0; } " +
                            ".menu-item:hover { -fx-background-color: #e0e0e0; } " +
                            ".context-menu { -fx-background-color: #f0f0f0; } " +
                            ".check-menu-item { -fx-background-color: #f0f0f0; } " +
                            ".check-menu-item .label { -fx-text-fill: black; } " +
                            ".separator .line { -fx-border-color: #c0c0c0; } " +
                            ".button { -fx-background-color: #d0d0d0; -fx-text-fill: black; } " +
                            ".button:hover { -fx-background-color: #c0c0c0; } " +
                            ".text-field { -fx-background-color: white; -fx-text-fill: black; } " +
                            ".combo-box { -fx-background-color: #d0d0d0; } " +
                            ".combo-box .list-cell { -fx-background-color: #f0f0f0; -fx-text-fill: black; } " +
                            ".combo-box-popup .list-view { -fx-background-color: #f0f0f0; } " +
                            ".combo-box .arrow-button { -fx-background-color: #d0d0d0; } " +
                            ".combo-box .text-input { -fx-text-fill: black; } " +
                            ".combo-box:editable .text-field { -fx-text-fill: black; } " +
                            ".titled-pane { -fx-text-fill: black; -fx-background-color: #f5f5f5; } " +
                            ".titled-pane > .title { -fx-background-color: #e0e0e0; -fx-text-fill: black; } " +
                            ".titled-pane > .content { -fx-background-color: #f5f5f5; } " +
                            ".label { -fx-text-fill: black; } " +
                            ".check-box { -fx-text-fill: black; } " +
                            ".check-box .box { -fx-background-color: white; } " +
                            ".slider { -fx-text-fill: black; } " +
                            ".scroll-pane { -fx-background-color: #f5f5f5; } " +
                            ".scroll-pane > .viewport { -fx-background-color: #f5f5f5; } ";

            scene.getStylesheets().add("data:text/css;base64," +
                    java.util.Base64.getEncoder().encodeToString(css.getBytes()));
        }
    }

    private void applyThemeToDialog(DialogPane dialogPane) {
        if (isDarkTheme) {
            String darkDialogCss =
                    ".dialog-pane { -fx-background-color: #2d2d2d; } " +
                            ".dialog-pane .label { -fx-text-fill: white; } " +
                            ".dialog-pane .content.label { -fx-text-fill: white; } " +
                            ".dialog-pane .header-panel .label { -fx-text-fill: white; } " +
                            ".dialog-pane .button { -fx-background-color: #3c3c3c; -fx-text-fill: white; } " +
                            ".dialog-pane .button:hover { -fx-background-color: #4c4c4c; } " +
                            ".dialog-pane .text-field { -fx-background-color: #3c3c3c; -fx-text-fill: white; -fx-prompt-text-fill: #888888; } " +
                            ".dialog-pane .text-input { -fx-text-fill: white; } " +
                            ".dialog-pane .combo-box { -fx-background-color: #3c3c3c; } " +
                            ".dialog-pane .combo-box .list-cell { -fx-background-color: #2d2d2d; -fx-text-fill: white; } " +
                            ".dialog-pane .combo-box .text-input { -fx-text-fill: white; }";

            dialogPane.getStylesheets().clear();
            dialogPane.getStylesheets().add("data:text/css;base64," +
                    java.util.Base64.getEncoder().encodeToString(darkDialogCss.getBytes()));

        } else {
            String lightDialogCss =
                    ".dialog-pane { -fx-background-color: #f0f0f0; } " +
                            ".dialog-pane .label { -fx-text-fill: black; } " +
                            ".dialog-pane .content.label { -fx-text-fill: black; } " +
                            ".dialog-pane .header-panel .label { -fx-text-fill: black; } " +
                            ".dialog-pane .button { -fx-background-color: #d0d0d0; -fx-text-fill: black; } " +
                            ".dialog-pane .button:hover { -fx-background-color: #c0c0c0; } " +
                            ".dialog-pane .text-field { -fx-background-color: white; -fx-text-fill: black; } " +
                            ".dialog-pane .text-input { -fx-text-fill: black; } " +
                            ".dialog-pane .combo-box { -fx-background-color: #d0d0d0; } " +
                            ".dialog-pane .combo-box .list-cell { -fx-background-color: #f0f0f0; -fx-text-fill: black; } " +
                            ".dialog-pane .combo-box .text-input { -fx-text-fill: black; }";

            dialogPane.getStylesheets().clear();
            dialogPane.getStylesheets().add("data:text/css;base64," +
                    java.util.Base64.getEncoder().encodeToString(lightDialogCss.getBytes()));
        }
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText(message);
        applyThemeToDialog(alert.getDialogPane());
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText(message);
        applyThemeToDialog(alert.getDialogPane());
        alert.showAndWait();
    }

    public void show() {
        stage.show();
    }
}