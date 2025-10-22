package fr.esiee.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class VectorTest {

    @Test
    void ctor_fromCoords_copiesDefensively_andLengthOK() {
        Vector v = new Vector(1.0, 2.0, 3.0);
        assertEquals(3, v.length());
        assertArrayEquals(new double[]{1.0, 2.0, 3.0}, v.toArray(), 1e-12);
    }

    @Test
    void factory_filled_ok() {
        Vector v = Vector.filled(4, 7.5);
        assertEquals(4, v.length());
        for (int i = 0; i < v.length(); i++) assertEquals(7.5, v.get(i), 1e-12);
    }

    @Test
    void randomUniform_size_ok_and_values_in_0_1() {
        Vector v = Vector.randomUniform(10);
        assertEquals(10, v.length());
        for (int i = 0; i < v.length(); i++) {
            double x = v.get(i);
            assertTrue(x >= 0.0 && x <= 1.0, "value " + x + " not in [0,1]");
        }
    }

    @Test
    void add_and_scale_and_dot_and_norm_ok() {
        Vector a = new Vector(1, 2, 3);
        Vector b = new Vector(4, 5, 6);

        Vector c = a.add(b);
        assertArrayEquals(new double[]{5, 7, 9}, c.toArray(), 1e-12);

        Vector d = a.scale(2.0);
        assertArrayEquals(new double[]{2, 4, 6}, d.toArray(), 1e-12);

        assertEquals(1*4 + 2*5 + 3*6, a.dot(b), 1e-12);

        assertEquals(Math.sqrt(1+4+9), a.norm(), 1e-12);
    }

    @Test
    void normalize_ok_and_throws_on_zero() {
        Vector a = new Vector(3, 0, 4);
        Vector n = a.normalize();
        assertEquals(1.0, n.norm(), 1e-12);

        Vector z = new Vector(0.0, 0.0);
        assertThrows(ArithmeticException.class, z::normalizeInPlace);
    }

    @Test
    void inPlace_set_get_copy_equals_hash_ok() {
        Vector v = new Vector(2);
        v.set(0, 10.0);
        v.set(1, -2.0);
        assertEquals(10.0, v.get(0), 1e-12);
        assertEquals(-2.0, v.get(1), 1e-12);

        Vector w = v.copy();
        assertEquals(v, w);
        assertEquals(v.hashCode(), w.hashCode());
    }

    @Test
    void homogeneous_to_from_and_normalizeW_ok() {
        Vector p = new Vector(2.0, 4.0, 8.0);
        Vector ph = p.toHomogeneous(); // (2,4,8,1)
        assertArrayEquals(new double[]{2,4,8,1}, ph.toArray(), 1e-12);

        Vector ph2 = new Vector(2, 4, 8, 2);
        Vector p2 = ph2.fromHomogeneous(); // (1,2,4)
        assertArrayEquals(new double[]{1,2,4}, p2.toArray(), 1e-12);

        Vector ph3 = new Vector(2, 4, 8, 2);
        Vector ph3n = ph3.normalizeW(); // (1,2,4,1)
        assertArrayEquals(new double[]{1,2,4,1}, ph3n.toArray(), 1e-12);
    }

    @Test
    void size_mismatch_operations_throw() {
        Vector a = new Vector(1,2,3);
        Vector b = new Vector(1,2);
        assertThrows(IllegalArgumentException.class, () -> a.add(b));
        assertThrows(IllegalArgumentException.class, () -> a.dot(b));
    }

    @Test
    void fromHomogeneous_w_zero_throws() {
        Vector ph = new Vector(1, 2, 0);
        assertThrows(ArithmeticException.class, ph::fromHomogeneousInPlace);
    }

    @Test
    void rangeCheck_outOfBounds_throws() {
        Vector v = new Vector(2);
        assertThrows(IndexOutOfBoundsException.class, () -> v.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> v.set(-1, 0.0));
    }
}
