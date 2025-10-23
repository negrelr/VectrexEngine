package fr.esiee.math;

/**
 * Classe d'utilitaires mathématiques génériques.
 *
 * <p>Cette classe s'inspire de la fonction
 * <a href="https://docs.python.org/3/library/math.html#math.isclose">math.isclose</a> de Python.
 * Elle fournit notamment une méthode {@link #isClose(double, double, double, double)}
 * permettant de comparer deux valeurs réelles en tenant compte d'une tolérance
 * relative et d'une tolérance absolue.
 *
 * <p>Les autres méthodes utilitaires proposées incluent :
 * <ul>
 *     <li>{@link #clamp(double, double, double)} pour borner une valeur dans un intervalle donné,</li>
 *     <li>{@link #toRadians(double)} et {@link #toDegrees(double)} pour convertir des angles.</li>
 * </ul>
 */
public final class MathUtil {

    private MathUtil() {
        // Classe utilitaire : pas d'instance
    }

    /**
     * Tolérance relative par défaut utilisée dans {@link #isClose(double, double)}.
     * <p>Valeur inspirée du comportement par défaut de Python : 1e-9.</p>
     */
    public static final double REL_TOL = 1e-9;

    /**
     * Tolérance absolue par défaut utilisée dans {@link #isClose(double, double)}.
     * <p>Valeur inspirée du comportement par défaut de Python : 0.0.</p>
     */
    public static final double ABS_TOL = 0.0;

     /**
     * Compare deux valeurs réelles avec les tolérances par défaut
     * {@link #REL_TOL} et {@link #ABS_TOL}.
     *
     * <pre>
     * abs(a - b) <= max(rel_tol * max(abs(a), abs(b)), abs_tol)
     * </pre>
     */
    public static boolean isClose(double a, double b) {
        return isClose(a, b, REL_TOL, ABS_TOL);
    }

    /**
     * Compare deux valeurs réelles selon une tolérance relative et absolue.
     *
     * <p>Renvoie {@code true} si et seulement si :</p>
     * <pre>
     * abs(a - b) <= max(rel_tol * max(abs(a), abs(b)), abs_tol)
     * </pre>
     *
     * <p>Cas particuliers (alignés sur Python) :</p>
     * <ul>
     *   <li>Si {@code a == b} (y compris {@code +∞ == +∞} et {@code -0.0 == 0.0}) → {@code true}.</li>
     *   <li>Si l'un est NaN → {@code false}.</li>
     *   <li>Si l'un au moins est infini → {@code a == b} (donc {@code +∞} vs {@code -∞} → {@code false}).</li>
     * </ul>
     *
     * @param a      première valeur
     * @param b      seconde valeur
     * @param relTol tolérance relative (≥ 0)
     * @param absTol tolérance absolue (≥ 0)
     * @return {@code true} si les deux valeurs sont proches numériquement
     * @throws IllegalArgumentException si {@code relTol} ou {@code absTol} est négatif
     */
    public static boolean isClose(double a, double b, double relTol, double absTol) {
        if (relTol < 0.0 || absTol < 0.0)
            throw new IllegalArgumentException("relTol and absTol must be non-negative");

        // 1) Égalité exacte (couvre +∞ == +∞, -0.0 == 0.0)
        if (a == b) return true;

        // 2) NaN -> false
        if (Double.isNaN(a) || Double.isNaN(b)) return false;

        // 3) Infinis : si l’un est infini, on ne considère proches que s'ils sont égaux (déjà testé)
        if (Double.isInfinite(a) || Double.isInfinite(b)) return false;

        // 4) Cas général
        double diff = Math.abs(a - b);
        double maxAbs = Math.max(Math.abs(a), Math.abs(b));
        double threshold = Math.max(relTol * maxAbs, absTol);
        return diff <= threshold;
    }

    /**
     * Borne une valeur {@code v} dans l'intervalle [min, max].
     *
     * @param v   valeur à borner
     * @param min borne inférieure
     * @param max borne supérieure
     * @return {@code min} si {@code v < min}, {@code max} si {@code v > max}, sinon {@code v}
     */
    public static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    /**
     * Convertit un angle en degrés vers un angle en radians.
     *
     * @param degrees angle exprimé en degrés
     * @return l'angle correspondant en radians
     */
    public static double toRadians(double degrees) {
        return degrees * Math.PI / 180.0;
    }

    /**
     * Convertit un angle en radians vers un angle en degrés.
     *
     * @param radians angle exprimé en radians
     * @return l'angle correspondant en degrés
     */
    public static double toDegrees(double radians) {
        return radians * 180.0 / Math.PI;
    }
}
