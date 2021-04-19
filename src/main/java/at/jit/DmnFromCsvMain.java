package at.jit;

import org.camunda.bpm.model.dmn.DmnModelInstance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DmnFromCsvMain {
    public static void main(String[] args) {

        System.out.println("Usage:");
        System.out.println("Enter 1 to convert a CSV to DMN");
        System.out.println("Enter 2 to convert a DMN to CSV");
        System.out.println();

        System.out.print("1/2: ");

        String option = readInput();

        if(option.equals("1")||option.equals("2")){
            if(!fileExtensionValid(args[0], args[1], option)){
                System.out.println("One of the entered file extensions is wrong, exiting program..");
                return;
            }
            if(option.equals("1")) {
                CsvReader csvReader = new CsvReader();

                CsvPojo csvPojo = csvReader.readCsv(args[0]);

                DmnCreator dmnCreator = new DmnCreator();
                DmnModelInstance dmnModelInstance = dmnCreator.createDmnFromCsvPojo(csvPojo);

                DmnFileExporter dmnFileExporter = new DmnFileExporter();
                dmnFileExporter.exportToDmnFile(dmnModelInstance, args[1]);
            }
            else {
                DmnToCsvConverter dmnToCsvConverter = new DmnToCsvConverter();
                dmnToCsvConverter.convertDmnToCsv(args[0], args[1]);
            }
        }
        else {
            System.out.println("Entered option is not 1 or 2, exiting program..");
            return;
        }
    }

    private static String readInput(){
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));

        String option = null;
        try {
            option = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return option;
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
