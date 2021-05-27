package at.jit;

import java.io.ByteArrayOutputStream;

final class Utils {
    public static String extractAndNormalizeActualOutput(final ByteArrayOutputStream baos) {
        return baos.toString().trim().replaceAll("\\r\\n?", "\n");
    }

}
