package at.jit.helper;

import at.jit.constants.Modes;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

public class FileExtensionsValidator {
    public boolean fileExtensionsValid(final String srcFileName,
                                       final String targetFileName,
                                       final String mode) {
        if (StringUtils.isBlank(mode) || StringUtils.isBlank(srcFileName) || StringUtils.isBlank(targetFileName)) {
            return false;
        }
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
