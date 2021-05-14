package at.jit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.camunda.bpm.model.dmn.DmnModelInstance;

import java.io.PrintStream;
import java.io.PrintWriter;

import static java.lang.String.format;

public class DmnFromCsvMain {
    static final String CLI_OPTION_CSV_TO_DMN = "c";
    static final String CLI_OPTION_DMN_TO_CSV = "d";
    static final String CLI_OPTION_INPUT_FILE = "i";
    static final String CLI_OPTION_OUTPUT_FILE = "o";
    private final PrintStream sysErr;

    public DmnFromCsvMain() {
        this(System.err);
    }

    DmnFromCsvMain(final PrintStream sysErr) {
        this.sysErr = sysErr;
    }

    public static void main(final String[] args) {
        final DmnFromCsvMain app = new DmnFromCsvMain();
        app.run(args);
    }

    public void run(final String[] args) {
        final Options options = createCommandLineParsingOptions();
        final CommandLine commandLine = parseCommandLine(args, options);

        if (commandLine == null) {
            return;
        }

        if (modeNotSpecified(commandLine, options)) {
            return;
        }

        final String mode = extractMode(commandLine);
        final String inputFileName = commandLine.getOptionValue(CLI_OPTION_INPUT_FILE);
        final String outputFileName = commandLine.getOptionValue(CLI_OPTION_OUTPUT_FILE);

        if (fileExtensionsInvalid(inputFileName, outputFileName, mode, options)) {
            return;
        }

        if (Modes.CSV_TO_DMN.equals(mode)) {
            convertCsvToDmn(inputFileName, outputFileName);
        } else if (Modes.DMN_TO_CSV.equals(mode)) {
            convertDmnToCsv(inputFileName, outputFileName);
        } else {
            sysErr.println("Unexpected error");
            return;
        }
    }

    Options createCommandLineParsingOptions() {
        final Options options = new Options();

        final Option csvToDmn = Option.builder(CLI_OPTION_CSV_TO_DMN)
                .required(false)
                .longOpt("csv-to-dmn")
                .desc("Convert CSV to DMN")
                .build();

        final Option dmnToCsv = Option.builder(CLI_OPTION_DMN_TO_CSV)
                .required(false)
                .longOpt("dmn-to-csv")
                .desc("Convert DMN to CSV")
                .build();

        final Option inputFileOpt = Option.builder(CLI_OPTION_INPUT_FILE)
                .required(true)
                .longOpt("input-file")
                .desc("Input file (CSV or DMN)")
                .build();

        final Option outputFileOpt = Option.builder(CLI_OPTION_OUTPUT_FILE)
                .required(true)
                .longOpt("output-file")
                .desc("Output file (CSV or DMN)")
                .build();

        options.addOption(csvToDmn);
        options.addOption(dmnToCsv);
        options.addOption(inputFileOpt);
        options.addOption(outputFileOpt);
        return options;
    }

    CommandLine parseCommandLine(final String[] args, final Options options) {
        final CommandLineParser parser = new DefaultParser();
        try {
            return parser.parse(options, args);
        } catch (final ParseException e) {
            sysErr.println(format("Invalid input: %s", e.getMessage()));
            printUsageText(options);
            return null;
        }
    }

    boolean fileExtensionsInvalid(final String inputFileName, final String outputFileName, final String mode, final Options options) {
        final boolean fileExtensionsValid = new FileExtensionsValidator().fileExtensionsValid(inputFileName, outputFileName, mode);
        if (!fileExtensionsValid) {
            sysErr.println("One of the entered file extensions is wrong.");
            printUsageText(options);
            return true;
        }
        return false;
    }

    boolean modeNotSpecified(final CommandLine commandLine, final Options options) {
        if (!commandLine.hasOption(CLI_OPTION_DMN_TO_CSV) &&
                !commandLine.hasOption(CLI_OPTION_CSV_TO_DMN)) {
            sysErr.println("Mode (CSV to DMN, DMN to CSV) not specified");
            printUsageText(options);
            return true;
        }
        return false;
    }

     void convertDmnToCsv(String inputFile, String outputFile) {
        DmnToCsvConverter dmnToCsvConverter = new DmnToCsvConverter();
        dmnToCsvConverter.convertDmnToCsv(inputFile, outputFile);
    }

    void convertCsvToDmn(String inputFile, String outputFile) {
        final CsvReader csvReader = new CsvReader();
        final CsvPojo csvPojo = csvReader.readCsv(inputFile);

        final DmnCreator dmnCreator = new DmnCreator();
        final DmnModelInstance dmnModelInstance = dmnCreator.createDmnFromCsvPojo(csvPojo);

        final DmnFileExporter dmnFileExporter = new DmnFileExporter();
        dmnFileExporter.exportToDmnFile(dmnModelInstance, outputFile);
    }

    void printUsageText(final Options options) {
        final HelpFormatter formatter = new HelpFormatter();
        final PrintWriter printWriter = new PrintWriter(sysErr);
        formatter.printHelp(printWriter, HelpFormatter.DEFAULT_WIDTH, "java -jar csv2dmn-converter.jar [-c] [-d] -i inputFile -o outputFile", "Either -c or -d must be provided.", options, HelpFormatter.DEFAULT_LEFT_PAD, HelpFormatter.DEFAULT_DESC_PAD, "");
        printWriter.flush();
    }

    String extractMode(final CommandLine commandLine) {
        if (commandLine.hasOption(CLI_OPTION_DMN_TO_CSV)) {
            return Modes.DMN_TO_CSV;
        } else
        {
            return Modes.CSV_TO_DMN;
        }
    }
}
