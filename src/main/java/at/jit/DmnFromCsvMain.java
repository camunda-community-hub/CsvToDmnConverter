package at.jit;

import org.camunda.bpm.model.dmn.DmnModelInstance;

public class DmnFromCsvMain {
    public void run(final String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java -jar CsvToDmnConverter mode input-file output-file");
        }

        final String mode = args[0].trim();

        if(mode.equals(Modes.CSV_TO_DMN)||mode.equals("2")){
            final String inputFile = args[1];
            final String outputFile = args[2];
            if(!new FileExtensionsValidator().fileExtensionsValid(inputFile, outputFile, mode)){
                System.out.println("One of the entered file extensions is wrong, exiting program..");
                return;
            }
            if(mode.equals(Modes.CSV_TO_DMN)) {
                final CsvReader csvReader = new CsvReader();
                final CsvPojo csvPojo = csvReader.readCsv(inputFile);

                final DmnCreator dmnCreator = new DmnCreator();
                final DmnModelInstance dmnModelInstance = dmnCreator.createDmnFromCsvPojo(csvPojo);

                final DmnFileExporter dmnFileExporter = new DmnFileExporter();
                dmnFileExporter.exportToDmnFile(dmnModelInstance, outputFile);
            }
            else {
                DmnToCsvConverter dmnToCsvConverter = new DmnToCsvConverter();
                dmnToCsvConverter.convertDmnToCsv(inputFile, outputFile);
            }
        }
        else {
            System.out.println("Entered option is not 1 or 2, exiting program..");
            return;
        }
    }
    public static void main(final String[] args) {
        final DmnFromCsvMain app = new DmnFromCsvMain();
        app.run(args);
    }
}
