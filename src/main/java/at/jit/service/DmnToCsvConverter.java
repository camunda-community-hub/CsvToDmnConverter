package at.jit.service;

import at.jit.constants.Converter;
import com.opencsv.CSVWriter;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.impl.instance.DecisionImpl;
import org.camunda.bpm.model.dmn.instance.DecisionTable;
import org.camunda.bpm.model.dmn.instance.Definitions;
import org.camunda.bpm.model.dmn.instance.DmnElement;
import org.camunda.bpm.model.dmn.instance.Input;
import org.camunda.bpm.model.dmn.instance.InputEntry;
import org.camunda.bpm.model.dmn.instance.InputExpression;
import org.camunda.bpm.model.dmn.instance.Output;
import org.camunda.bpm.model.dmn.instance.OutputEntry;
import org.camunda.bpm.model.dmn.instance.Rule;
import org.camunda.bpm.model.dmn.instance.Text;

import java.io.File;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DmnToCsvConverter {
    private final List<List<String>> csvFormatArray;

    public DmnToCsvConverter() {
        this.csvFormatArray = new ArrayList<>();
    }

    public void convertDmnToCsv(final String dmnFile, final String csv) {
        final DmnModelInstance readFile = Dmn.readModelFromFile(new File(dmnFile));
        final Definitions def = readFile.getDefinitions();
        final DecisionImpl decImpl = (DecisionImpl) new ArrayList(def.getDrgElements()).get(0);
        final DecisionTable decisionTable = (DecisionTable) new ArrayList(decImpl.getChildElementsByType(DecisionTable.class)).get(0);

        final List<Rule> ruleList = new ArrayList<>(decisionTable.getRules());

        setHeaders(decImpl, decisionTable);
        setContent(ruleList);

        writeToFile(csv, csvFormatArray);
    }

    private void setHeaders(final DecisionImpl decImpl, final DecisionTable decisionTable) {
        final List<String> idNameHit = new ArrayList<>();

        final List<Rule> ruleList = new ArrayList<>(decisionTable.getRules());

        final List<Input> inputLabels = new ArrayList<>(decisionTable.getInputs());
        final List<Output> outputsLabels = new ArrayList<>(decisionTable.getOutputs());

        idNameHit.add(decImpl.getId());
        idNameHit.add(decImpl.getName());
        idNameHit.add(decisionTable.getHitPolicy().toString());

        csvFormatArray.add(idNameHit);

        final List<String> inOutLabels = new ArrayList<>();

        final Rule firstRuleList = ruleList.get(0);
        for (int i = 0; i < firstRuleList.getInputEntries().size(); i++) {
            inOutLabels.add(Converter.INPUT);
        }

        for (int i = 0; i < firstRuleList.getOutputEntries().size(); i++) {
            inOutLabels.add(Converter.OUTPUT);
        }

        csvFormatArray.add(inOutLabels);

        final List<String> labels = new ArrayList<>();

        for (final Input input : inputLabels) {
            labels.add(input.getLabel());
        }

        for (final Output output : outputsLabels) {
            labels.add(output.getLabel());
        }

        csvFormatArray.add(labels);

        final List<Input> inputColProperties = (List<Input>) decisionTable.getChildElementsByType(Input.class);
        final List<Output> outputColProperties = (List<Output>) decisionTable.getChildElementsByType(Output.class);

        final List<String> datatypes = new ArrayList<>();

        for (final Input input : inputColProperties) {
            datatypes.add(((List<InputExpression>) input.getChildElementsByType(InputExpression.class)).get(0).getTypeRef());
        }

        for (final Output output : outputColProperties) {
            datatypes.add(output.getTypeRef());
        }

        csvFormatArray.add(datatypes);
    }

    private void setContent(List<Rule> ruleList) {
        for (final Rule rule : ruleList) {
            final List<InputEntry> inputEntryList = new ArrayList(rule.getInputEntries());
            final List<OutputEntry> outputEntryList = new ArrayList(rule.getOutputEntries());

            final List<String> singleRule = new ArrayList<>();

            for (final InputEntry inputEntry : inputEntryList) {
                setRuleElement(singleRule, inputEntry);
            }

            for (final OutputEntry outputEntry : outputEntryList) {
                setRuleElement(singleRule, outputEntry);
            }

            if (rule.getDescription() != null) {
                singleRule.add(rule.getDescription().getTextContent());
            }

            csvFormatArray.add(singleRule);
        }
    }

    public void setRuleElement(final List<String> singleRule, final DmnElement entry) {
        final Text text = (Text) new ArrayList(entry.getChildElementsByType(Text.class)).get(0);
        final String textContent = text.getTextContent();
        if (textContent.startsWith("\"") && textContent.endsWith("\"")) {
            singleRule.add(textContent.substring(1, textContent.length() - 1));
        } else {
            singleRule.add(text.getTextContent());
        }
    }

    public void writeToFile(String csv, List<List<String>> csvData) {
        try (
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
