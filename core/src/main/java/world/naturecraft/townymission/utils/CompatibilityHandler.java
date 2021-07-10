package world.naturecraft.townymission.utils;

public class CompatibilityHandler {

    private CompatibilityHandler singleton;

    public CompatibilityHandler getInstance() {
        if (singleton == null) {
            singleton = new CompatibilityHandler();
        }

        return singleton;
    }
}
