package at.jit.service;

import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class DmnFileExporter {
    public void exportToDmnFile(DmnModelInstance dmnModelInstance, String path) {
        try (FileWriter fileWriter = new FileWriter(path)) {
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(Dmn.convertToString(dmnModelInstance));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
