package mff.forwardmodel.slim.core;

import mff.agents.astarHelper.MarioAction;
import mff.forwardmodel.slim.sprites.FireballSlim;
import mff.forwardmodel.slim.sprites.ShellSlim;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class MarioUpdateContextSlim {
    public MarioWorldSlim world;
    public boolean[] actions;
    public int fireballsOnScreen;

    public MarioAction currentAction;

    public final ArrayList<FireballSlim> fireballsToCheck = new ArrayList<>();
    public final ArrayList<ShellSlim> shellsToCheck = new ArrayList<>();
    final ArrayList<MarioSpriteSlim> addedSprites = new ArrayList<>();
    final ArrayList<MarioSpriteSlim> removedSprites = new ArrayList<>();

    private static final ThreadLocal<ArrayDeque<MarioUpdateContextSlim>> localPool =
            ThreadLocal.withInitial(ArrayDeque::new);

    private static final ThreadLocal<Double> maxFitnessLocal = ThreadLocal.withInitial(() -> -1.0);

    public static MarioUpdateContextSlim get() {
        ArrayDeque<MarioUpdateContextSlim> pool = localPool.get();
        MarioUpdateContextSlim ctx = pool.poll();
        if (ctx != null) return ctx;

        MarioUpdateContextSlim newCtx = new MarioUpdateContextSlim();
        newCtx.addedSprites.ensureCapacity(10);
        newCtx.removedSprites.ensureCapacity(10);
        return newCtx;
    }

    public static void back(MarioUpdateContextSlim ctx) {
        localPool.get().add(ctx);
    }

    private MarioUpdateContextSlim() {}
}