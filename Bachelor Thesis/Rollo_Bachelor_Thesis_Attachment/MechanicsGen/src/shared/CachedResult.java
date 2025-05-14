package shared;

public class CachedResult {
    public final double fitness;
    public final double constraints;
    public final int[] dimensions;

    public CachedResult(double fitness, double constraints, int[] dimensions) {
        this.fitness = fitness;
        this.constraints = constraints;
        this.dimensions = dimensions;
    }
}
