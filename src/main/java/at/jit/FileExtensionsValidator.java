package at.jit;

import org.apache.commons.io.FilenameUtils;

class FileExtensionsValidator {
    public boolean fileExtensionsValid(final String srcFileName,
                                       final String targetFileName,
                                       final String mode) {
        final String srcExtension = FilenameUtils.getExtension(srcFileName);
        final String targetExtension = FilenameUtils.getExtension(targetFileName);
        if (Modes.CSV_TO_DMN.equals(mode)) {
            return srcExtension.equalsIgnoreCase("csv") && targetExtension.equalsIgnoreCase("dmn");
        } else if (Modes.DMN_TO_CSV.equals(mode)) {
            return srcExtension.equalsIgnoreCase("dmn") && targetExtension.equalsIgnoreCase("csv");
        } else {
            return false;
        }
    }
}
