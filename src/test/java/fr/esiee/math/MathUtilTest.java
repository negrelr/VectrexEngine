package fr.esiee.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests unitaires pour {@link MathUtil}. */
class MathUtilTest {

    /* =============== isClose : cas généraux =============== */

    @Test
    void isClose_identical_values_true() {
        assertTrue(MathUtil.isClose(1.0, 1.0));
        assertTrue(MathUtil.isClose(-0.0, 0.0));
    }

    @Test
    void isClose_relative_tolerance_behaves_like_python() {
        double a = 1000.0;
        double b = 1000.000001; // diff relative ~ 1e-9
        assertTrue(MathUtil.isClose(a, b, 1e-8, 0.0));
        assertFalse(MathUtil.isClose(a, b, 1e-10, 0.0));
    }

    @Test
    void isClose_absolute_tolerance_for_small_numbers() {
        double a = 0.0, b = 1e-8;
        assertFalse(MathUtil.isClose(a, b));              // absTol=0 par défaut
        assertTrue(MathUtil.isClose(a, b, 0.0, 1e-6));    // absTol permet la proximité
    }

    @Test
    void isClose_negative_tolerances_throw() {
        assertThrows(IllegalArgumentException.class, () -> MathUtil.isClose(1.0, 1.0, -1e-9, 0.0));
        assertThrows(IllegalArgumentException.class, () -> MathUtil.isClose(1.0, 1.0, 0.0, -1e-9));
    }

    @Test
    void isClose_nan_returns_false() {
        assertFalse(MathUtil.isClose(Double.NaN, 1.0));
        assertFalse(MathUtil.isClose(1.0, Double.NaN));
        assertFalse(MathUtil.isClose(Double.NaN, Double.NaN));
    }

    /* =============== isClose : infinis =============== */

    @Test
    void isClose_same_infinities_true_opposite_false() {
        assertTrue(MathUtil.isClose(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
        assertTrue(MathUtil.isClose(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY));
        assertFalse(MathUtil.isClose(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY));
    }

    @Test
    void isClose_finite_vs_infinite_false() {
        assertFalse(MathUtil.isClose(42.0, Double.POSITIVE_INFINITY));
        assertFalse(MathUtil.isClose(Double.NEGATIVE_INFINITY, -123.0));
    }

    /* =============== clamp & conversions =============== */

    @Test
    void clamp_bounds() {
        assertEquals(5.0, MathUtil.clamp(5.0, 0.0, 10.0));
        assertEquals(0.0, MathUtil.clamp(-3.0, 0.0, 10.0));
        assertEquals(10.0, MathUtil.clamp(15.0, 0.0, 10.0));
    }

    @Test
    void radians_degrees_inverse() {
        double deg = 123.456;
        double rad = MathUtil.toRadians(deg);
        assertEquals(deg, MathUtil.toDegrees(rad), 1e-12);
    }

    @Test
    void known_values_conversions() {
        assertEquals(Math.PI / 2, MathUtil.toRadians(90.0), 1e-12);
        assertEquals(180.0, MathUtil.toDegrees(Math.PI), 1e-12);
    }
}
