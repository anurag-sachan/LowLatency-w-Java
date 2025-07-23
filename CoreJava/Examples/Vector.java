package Examples;

// Vector API (Preview)
// Requires: --add-modules jdk.incubator.vector
class Vector {
    public static void main(String[] args) {
        var species = jdk.incubator.vector.FloatVector.SPECIES_PREFERRED;
        float[] input = {1f, 2f, 3f, 4f};
        var vector = jdk.incubator.vector.FloatVector.fromArray(species, input, 0);
        var result = vector.mul(2f);
        result.intoArray(input, 0);
        for (float f : input) System.out.println(f);
    }
}

// import jdk.incubator.vector.*;

// public class Vector {
//     public static void main(String[] args) {
//         float[] a = {1f, 2f, 3f, 4f};
//         float[] b = {5f, 6f, 7f, 8f};
//         float[] result = new float[4];

//         // Choose best supported float vector species for current platform
//         VectorSpecies<Float> species = FloatVector.SPECIES_PREFERRED;

//         for (int i = 0; i < a.length; i += species.length()) {
//             // Load chunks into FloatVector
//             FloatVector va = FloatVector.fromArray(species, a, i);
//             FloatVector vb = FloatVector.fromArray(species, b, i);

//             // Perform SIMD addition
//             FloatVector vc = va.add(vb);

//             // Store result back
//             vc.intoArray(result, i);
//         }

//         for (float f : result) {
//             System.out.println(f);
//         }
//     }
// }
