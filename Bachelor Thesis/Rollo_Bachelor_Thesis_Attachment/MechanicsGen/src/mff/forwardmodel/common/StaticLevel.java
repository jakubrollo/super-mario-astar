package mff.forwardmodel.common;

public class StaticLevel {
    public LevelTile[][] tiles;
    public boolean containsBulletBill = false;

    public void initialize() {
        containsBulletBill = false;
        for (LevelTile[] row : tiles) {
            for (LevelTile tile : row) {
                if (tile != null && tile.levelPart != null && isBulletBillTile(tile.levelPart)) {
                    containsBulletBill = true;
                    return;
                }
            }
        }
    }

    private boolean isBulletBillTile(LevelPart part) {
        return part == LevelPart.BULLET_BILL_CANNON ||
                part == LevelPart.BULLET_BILL_BASE ||
                part == LevelPart.BULLET_BILL_COLUMN;
    }

    public static class LevelTile {
        public final int id;
        public final LevelPart levelPart;

        public LevelTile(int id, LevelPart levelPart) {
            this.id = id;
            this.levelPart = levelPart;
        }
    }
}
