package at.jit;

import org.camunda.bpm.model.dmn.DmnModelInstance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DmnFromCsvMain {
    public void run(final String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java -jar CsvToDmnConverter mode input-file output-file");
        }

        final String mode = args[0].trim();

        if(mode.equals("1")||mode.equals("2")){
            final String inputFile = args[1];
            final String outputFile = args[2];
            if(!fileExtensionValid(inputFile, outputFile, mode)){
                System.out.println("One of the entered file extensions is wrong, exiting program..");
                return;
            }
            if(mode.equals("1")) {
                CsvReader csvReader = new CsvReader();

                CsvPojo csvPojo = csvReader.readCsv(args[1]);

                DmnCreator dmnCreator = new DmnCreator();
                DmnModelInstance dmnModelInstance = dmnCreator.createDmnFromCsvPojo(csvPojo);

                DmnFileExporter dmnFileExporter = new DmnFileExporter();
                dmnFileExporter.exportToDmnFile(dmnModelInstance, args[2]);
            }
            else {
                DmnToCsvConverter dmnToCsvConverter = new DmnToCsvConverter();
                dmnToCsvConverter.convertDmnToCsv(args[1], args[2]);
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

    private static boolean fileExtensionValid(String source, String destination, String option){
        if(option.equals("1")){
            return source.substring(source.lastIndexOf(".")+1).equals("csv") && destination.substring(destination.lastIndexOf(".")+1).equals("dmn");
        }
        else {
            return source.substring(source.lastIndexOf(".")+1).equals("dmn") && destination.substring(destination.lastIndexOf(".")+1).equals("csv");
        }
    }
}
