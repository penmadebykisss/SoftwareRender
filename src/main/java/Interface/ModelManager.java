package Interface;

import Interface.model.Model;

import java.util.ArrayList;
import java.util.List;

public class ModelManager {
    private List<ModelEntry> models;
    private int nextId = 1;
    private ModelEntry selectedModel;

    public ModelManager() {
        this.models = new ArrayList<>();
    }

    public ModelEntry addModel(Model model, String name) {
        ModelEntry entry = new ModelEntry(nextId++, model, name);
        models.add(entry);
        if (selectedModel == null) {
            selectedModel = entry;
        }
        return entry;
    }

    public void removeModel(int id) {
        ModelEntry toRemove = null;
        for (ModelEntry entry : models) {
            if (entry.getId() == id) {
                toRemove = entry;
                break;
            }
        }

        if (toRemove != null) {
            models.remove(toRemove);
            if (selectedModel == toRemove) {
                selectedModel = models.isEmpty() ? null : models.get(0);
            }
        }
    }

    public void clearAll() {
        models.clear();
        selectedModel = null;
        nextId = 1;
    }

    public ModelEntry getSelectedModel() {
        return selectedModel;
    }

    public void setSelectedModel(int id) {
        for (ModelEntry entry : models) {
            if (entry.getId() == id) {
                selectedModel = entry;
                break;
            }
        }
    }

    public List<ModelEntry> getAllModels() {
        return new ArrayList<>(models);
    }

    public boolean isEmpty() {
        return models.isEmpty();
    }

    public int getModelCount() {
        return models.size();
    }

    public static class ModelEntry {
        private final int id;
        private final Model model;
        private String name;

        public ModelEntry(int id, Model model, String name) {
            this.id = id;
            this.model = model;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public Model getModel() {
            return model;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}