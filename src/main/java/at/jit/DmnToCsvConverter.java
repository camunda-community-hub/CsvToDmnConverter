package at.jit;

import com.opencsv.CSVWriter;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.impl.instance.DecisionImpl;
import org.camunda.bpm.model.dmn.instance.*;

import java.io.File;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DmnToCsvConverter {

    private List<List<String>> csvFormatArray;

    public DmnToCsvConverter() {
        this.csvFormatArray = new ArrayList<>();
    }

    public void convertDmnToCsv(String dmnFile, String csv) {
        DmnModelInstance readFile = Dmn.readModelFromFile(new File(dmnFile));
        Definitions def = readFile.getDefinitions();
        DecisionImpl decImpl = (DecisionImpl) new ArrayList(def.getDrgElements()).get(0);
        DecisionTable decisionTable = (DecisionTable) new ArrayList(decImpl.getChildElementsByType(DecisionTable.class)).get(0);

        List<Rule> ruleList = new ArrayList<>(decisionTable.getRules());

        setHeaders(decImpl, decisionTable);
        setContent(ruleList);

        writeToFile(csv, csvFormatArray);
    }

    private void setHeaders(DecisionImpl decImpl, DecisionTable decisionTable) {
        List<String> idNameHit = new ArrayList<>();

        List<Rule> ruleList = new ArrayList<>(decisionTable.getRules());

        List<Input> inputLabels = new ArrayList<>(decisionTable.getInputs());
        List<Output> outputsLabels = new ArrayList<>(decisionTable.getOutputs());

        idNameHit.add(decImpl.getId());
        idNameHit.add(decImpl.getName());
        idNameHit.add(decisionTable.getHitPolicy().toString());

        csvFormatArray.add(idNameHit);

        List<String> inOutLabels = new ArrayList<>();

        for (int i = 0; i < new ArrayList(ruleList.get(0).getInputEntries()).size(); i++) {
            inOutLabels.add(Converter.INPUT);
        }

        for (int i = 0; i < new ArrayList(ruleList.get(0).getOutputEntries()).size(); i++) {
            inOutLabels.add(Converter.OUTPUT);
        }

        csvFormatArray.add(inOutLabels);

        List<String> labels = new ArrayList<>();

        for (Input input : inputLabels) {
            labels.add(input.getLabel());
        }

        for (Output output : outputsLabels) {
            labels.add(output.getLabel());
        }

        csvFormatArray.add(labels);

        List<Input> inputColProperties = (List<Input>) decisionTable.getChildElementsByType(Input.class);
        List<Output> outputColProperties = (List<Output>) decisionTable.getChildElementsByType(Output.class);

        List<String> datatypes = new ArrayList<>();

        for(Input input: inputColProperties){
            datatypes.add(((List<InputExpression>)input.getChildElementsByType(InputExpression.class)).get(0).getTypeRef());
        }

        for(Output output: outputColProperties){
            datatypes.add(output.getTypeRef());
        }

        csvFormatArray.add(datatypes);
    }

    private void setContent(List<Rule> ruleList) {
        for (Rule rule : ruleList) {
            List<InputEntry> inputEntryList = new ArrayList(rule.getInputEntries());
            List<OutputEntry> outputEntryList = new ArrayList(rule.getOutputEntries());

            List<String> singleRule = new ArrayList<>();

            for (InputEntry inputEntry : inputEntryList) {
                setRuleElement(singleRule, inputEntry);
            }

            for (OutputEntry outputEntry : outputEntryList) {
                setRuleElement(singleRule, outputEntry);
            }

            singleRule.add(rule.getDescription().getTextContent());

            csvFormatArray.add(singleRule);
        }
    }

    public void setRuleElement(List<String> singleRule, DmnElement entry) {
        Text text = (Text) new ArrayList(entry.getChildElementsByType(Text.class)).get(0);
        String textContent = text.getTextContent();
        if (textContent.startsWith("\"") && textContent.endsWith("\"")) {
            singleRule.add(textContent.substring(1, textContent.length() - 1));
        } else {
            singleRule.add(text.getTextContent());
        }
    }

    public void writeToFile(String csv, List<List<String>> csvData) {

        try (
                //Writer writer = Files.newBufferedWriter(Paths.get(new URI("file:///" + csv)));
                Writer writer = Files.newBufferedWriter(Paths.get(csv));

                CSVWriter csvWriter = new CSVWriter(writer,
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END);
        ) {

            csvData.forEach(line -> csvWriter.writeNext(line.stream().toArray(String[]::new), false));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
