package Interface;

import Math.cam.Camera;
import Math.vector.Vector3D;

import java.util.ArrayList;
import java.util.List;

public class CameraManager {
    private List<CameraEntry> cameras;
    private int nextId = 1;
    private CameraEntry activeCamera;

    public CameraManager() {
        this.cameras = new ArrayList<>();
        addDefaultCamera();
    }

    private void addDefaultCamera() {
        Camera defaultCamera = new Camera(
                new Vector3D(0, 0, 5),    // позиция камеры
                new Vector3D(0, 0, 0),    // цель (куда смотрит)
                60.0f,                     // FOV
                16.0f / 9.0f,             // aspect ratio
                0.1f,                      // near plane
                100.0f                     // far plane
        );
        addCamera(defaultCamera, "Default Camera");
    }

    public CameraEntry addCamera(Camera camera, String name) {
        CameraEntry entry = new CameraEntry(nextId++, camera, name);
        cameras.add(entry);
        if (activeCamera == null) {
            activeCamera = entry;
        }
        return entry;
    }

    public CameraEntry addCamera(String name) {
        Camera camera = new Camera(
                new Vector3D(0, 0, 5),
                new Vector3D(0, 0, 0),
                60.0f,
                16.0f / 9.0f,
                0.1f,
                100.0f
        );
        return addCamera(camera, name);
    }

    public void removeCamera(int id) {
        if (cameras.size() <= 1) {
            throw new IllegalStateException("Cannot remove the last camera");
        }

        CameraEntry toRemove = null;
        for (CameraEntry entry : cameras) {
            if (entry.getId() == id) {
                toRemove = entry;
                break;
            }
        }

        if (toRemove != null) {
            cameras.remove(toRemove);
            if (activeCamera == toRemove) {
                activeCamera = cameras.get(0);
            }
        }
    }

    public void setActiveCamera(int id) {
        for (CameraEntry entry : cameras) {
            if (entry.getId() == id) {
                activeCamera = entry;
                break;
            }
        }
    }

    public CameraEntry getActiveCamera() {
        return activeCamera;
    }

    public List<CameraEntry> getAllCameras() {
        return new ArrayList<>(cameras);
    }

    public int getCameraCount() {
        return cameras.size();
    }

    public void resetActiveCamera() {
        if (activeCamera != null) {
            activeCamera.getCamera().setPosition(new Vector3D(0, 0, 5));
            activeCamera.getCamera().setTarget(new Vector3D(0, 0, 0));
        }
    }

    public static class CameraEntry {
        private final int id;
        private final Camera camera;
        private String name;
        private boolean visible = true;

        public CameraEntry(int id, Camera camera, String name) {
            this.id = id;
            this.camera = camera;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public Camera getCamera() {
            return camera;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}