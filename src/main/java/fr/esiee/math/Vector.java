package fr.esiee.math;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Vector à taille quelconque, stocké comme un tableau de doubles.
 * Convention : si on travaille en coordonnées homogènes, la dernière composante est w.
 * Aucun état interne "cartésien vs homogène" n'est stocké.
 */
public final class Vector {

    private double[] data; // mutable pour permettre les opérations "in place"

    /* ====================== Constructeurs ====================== */

    /** Construit un vecteur nul de taille n. */
    public Vector(final int n) {
        if (n <= 0) throw new IllegalArgumentException("Vector size must be positive, got " + n);
        this.data = new double[n];
    }

    /** Construit un vecteur à partir des composantes. */
    public Vector(final double... coords) {
        Objects.requireNonNull(coords, "coords must not be null");
        if (coords.length == 0) throw new IllegalArgumentException("Vector requires at least one component");
        this.data = coords.clone(); // copie défensive
    }

    /* ==================== Fabriques statiques ==================== */

    /** Vecteur rempli d'une même valeur : [value, ..., value] de taille n. */
    public static Vector filled(final int n, final double value) {
        if (n <= 0) throw new IllegalArgumentException("Vector size must be positive, got " + n);
        final double[] a = new double[n];
        Arrays.fill(a, value);
        return new Vector(a);
    }

    /** Vecteur aléatoire uniforme U[0,1] de taille n. */
    public static Vector randomUniform(final int n) {
        if (n <= 0) throw new IllegalArgumentException("Vector size must be positive, got " + n);
        final double[] a = new double[n];
        final ThreadLocalRandom rng = ThreadLocalRandom.current();
        for (int i = 0; i < n; i++) a[i] = rng.nextDouble(0.0, 1.0);
        return new Vector(a);
    }

    /** Vecteur aléatoire normal N(0,1) de taille n. */
    public static Vector randomNormal(final int n) {
        if (n <= 0) throw new IllegalArgumentException("Vector size must be positive, got " + n);
        final double[] a = new double[n];
        final ThreadLocalRandom rng = ThreadLocalRandom.current();
        for (int i = 0; i < n; i++) a[i] = rng.nextGaussian();
        return new Vector(a);
    }

    /* =================== Méthodes d'accès =================== */

    public int length() { return data.length; }

    public double get(final int i) {
        rangeCheck(i);
        return data[i];
    }

    public void set(final int i, final double value) {
        rangeCheck(i);
        data[i] = value;
    }

    public double[] toArray() { return data.clone(); }

    /* ========== Coordonnées homogènes (structurelle) ========== */

    public Vector toHomogeneous() {
        final Vector out = this.copy();
        out.toHomogeneousInPlace();
        return out;
    }

    public void toHomogeneousInPlace() {
        final double[] out = Arrays.copyOf(data, data.length + 1);
        out[out.length - 1] = 1.0;
        this.data = out;
    }

    public Vector fromHomogeneous() {
        final Vector out = this.copy();
        out.fromHomogeneousInPlace();
        return out;
    }

    public void fromHomogeneousInPlace() {
        ensureHasLastComponentForHomogeneous();
        if (data.length == 1) throw new IllegalStateException("Cannot drop w from a 1D vector [w]");
        final double w = data[data.length - 1];
        if (w == 0.0) throw new ArithmeticException("Cannot convert from homogeneous: w == 0");
        final double invW = 1.0 / w;
        for (int i = 0; i < data.length - 1; i++) data[i] *= invW;
        this.data = Arrays.copyOf(data, data.length - 1);
    }

    public Vector normalizeW() {
        final Vector out = this.copy();
        out.normalizeWInPlace();
        return out;
    }

    public void normalizeWInPlace() {
        ensureHasLastComponentForHomogeneous();
        final double w = data[data.length - 1];
        if (w == 0.0) throw new ArithmeticException("Cannot normalize by w: w == 0");
        final double invW = 1.0 / w;
        for (int i = 0; i < data.length - 1; i++) data[i] *= invW;
        data[data.length - 1] = 1.0;
    }

    /* ===================== Algèbre linéaire ===================== */

    public Vector add(final Vector other) {
        final Vector out = this.copy();
        out.addInPlace(other);
        return out;
    }

    public void addInPlace(final Vector other) {
        Objects.requireNonNull(other, "other must not be null");
        ensureSameLength(this, other, "addInPlace");
        for (int i = 0; i < data.length; i++) this.data[i] += other.data[i];
    }

    public Vector scale(final double s) {
        final Vector out = this.copy();
        out.scaleInPlace(s);
        return out;
    }

    public void scaleInPlace(final double s) {
        for (int i = 0; i < data.length; i++) data[i] *= s;
    }

    /** Produit scalaire ⟨this, other⟩. */
    public double dot(final Vector other) {
        Objects.requireNonNull(other, "other must not be null");
        ensureSameLength(this, other, "dot");
        double acc = 0.0;
        for (int i = 0; i < data.length; i++) acc += this.data[i] * other.data[i];
        return acc;
    }

    /** Norme euclidienne du vecteur. */
    public double norm() {
        double sumSq = 0.0;
        for (final double v : data) sumSq += v * v;
        return Math.sqrt(sumSq);
    }

    /** Retourne un nouveau vecteur normalisé (longueur 1). */
    public Vector normalize() {
        final Vector out = this.copy();
        out.normalizeInPlace();
        return out;
    }

    /** Normalise ce vecteur en place. */
    public void normalizeInPlace() {
        final double n = this.norm();
        if (n == 0.0) throw new ArithmeticException("Cannot normalize zero-length vector");
        this.scaleInPlace(1.0 / n);
    }

    /* ====================== Utilitaires ====================== */

    public Vector copy() { return new Vector(this.data); }

    @Override public String toString() { return "Vector" + Arrays.toString(data); }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector)) return false;
        final Vector vector = (Vector) o;
        return Arrays.equals(this.data, vector.data);
    }

    @Override
    public int hashCode() { return Arrays.hashCode(this.data); }

    /* ====================== Helpers privés ====================== */

    private void rangeCheck(final int i) {
        if (i < 0 || i >= data.length)
            throw new IndexOutOfBoundsException("Index " + i + " out of bounds for length " + data.length);
    }

    private void ensureHasLastComponentForHomogeneous() {
        if (data.length < 1)
            throw new IllegalStateException("Vector has no components to interpret as homogeneous");
    }

    private static void ensureSameLength(final Vector a, final Vector b, final String op) {
        if (a.data.length != b.data.length)
            throw new IllegalArgumentException(
                "Incompatible vector sizes for '" + op + "': " + a.data.length + " vs " + b.data.length
            );
    }
}