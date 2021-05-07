package at.jit;

import at.jit.SequenceGenerator.SequenceGenerator;
import at.jit.SequenceGenerator.SequenceGeneratorImpl;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.Decision;
import org.camunda.bpm.model.dmn.instance.DecisionTable;
import org.camunda.bpm.model.dmn.instance.Definitions;
import org.camunda.bpm.model.dmn.instance.Description;
import org.camunda.bpm.model.dmn.instance.Input;
import org.camunda.bpm.model.dmn.instance.InputEntry;
import org.camunda.bpm.model.dmn.instance.InputExpression;
import org.camunda.bpm.model.dmn.instance.Output;
import org.camunda.bpm.model.dmn.instance.OutputEntry;
import org.camunda.bpm.model.dmn.instance.Rule;
import org.camunda.bpm.model.dmn.instance.Text;

import java.util.List;

import static java.lang.String.format;

class DmnCreator {
    private DmnModelInstance modelInstance;
    private DecisionTable decisionTable;
    private SequenceGenerator sequenceGenerator;

    public DmnModelInstance createDmnFromCsvPojo(CsvPojo csvPojo) {
        sequenceGenerator = new SequenceGeneratorImpl();

        initializeBasicDmnModel(csvPojo);

        createInputHeaders(csvPojo.getInColName(), csvPojo.getInColDataType());
        createOutputHeaders(csvPojo.getOutColName(), csvPojo.getOutColDataType());

        final List<List<String>> inData = csvPojo.getInData();
        final List<List<String>> outData = csvPojo.getOutData();
        final List<String> annotations = csvPojo.getRowAnnotation();

        final int entries = inData.size();

        if (inData.size() == outData.size()) {
            for (int i = 0; i < entries; i++) {
                createRules(inData.get(i), outData.get(i), annotations.get(i), csvPojo.getInColDataType(), csvPojo.getOutColDataType());
            }
        }

        Dmn.validateModel(modelInstance);

        return modelInstance;
    }

    public void initializeBasicDmnModel(CsvPojo csvPojo) {
        modelInstance = Dmn.createEmptyModel();
        final Definitions definitions = modelInstance.newInstance(Definitions.class);
        definitions.setNamespace("http://camunda.org/schema/1.0/dmn");
        definitions.setName(Converter.DMN_NAME);
        definitions.setId(Converter.DMN_ID);
        modelInstance.setDefinitions(definitions);

        final Decision decision = modelInstance.newInstance(Decision.class);

        decision.setId(csvPojo.getDmnId().substring(1)); //here a substring is mandatory otherwise there will be an error validating the DMN, somehow the CSV reader reads some character into the variable at index 0
        decision.setName(csvPojo.getDmnTitle());
        definitions.addChildElement(decision);

        decisionTable = modelInstance.newInstance(DecisionTable.class);
        decisionTable.setId(Converter.DECISION_TABLE_ID);
        decisionTable.setHitPolicy(csvPojo.getHitPolicy());
        decision.addChildElement(decisionTable);
    }

    public void createInputHeaders(List<String> labels, List<String> dataTypes) {
        for (int i = 0; i < labels.size(); i++) {
            final Input input = modelInstance.newInstance(Input.class);
            input.setId(composeId(Converter.INPUT_ID));
            input.setLabel(labels.get(i));
            InputExpression inputExpression = modelInstance.newInstance(InputExpression.class);
            inputExpression.setId(Converter.INPUT_EXPRESSION_ID + sequenceGenerator.getNext());
            inputExpression.setTypeRef(dataTypes.get(i));
            input.addChildElement(inputExpression);
            decisionTable.addChildElement(input);
        }
    }
    private String composeId(final String id) {
        return format("%s%d", id, sequenceGenerator.getNext());
    }

    public void createOutputHeaders(List<String> labels, List<String> dataTypes) {
        for (int i = 0; i < labels.size(); i++) {
            final Output output = modelInstance.newInstance(Output.class);
            output.setId(Converter.OUTPUT_ID + sequenceGenerator.getNext());
            output.setLabel(labels.get(i));
            output.setName(labels.get(i));
            output.setTypeRef(dataTypes.get(i));
            decisionTable.addChildElement(output);
        }
    }

    public void createRules(final List<String> inRow,
                            final List<String> outRow,
                            final String annotation,
                            final List<String> inDataTypes,
                            final List<String> outDataTypes) {
        final Rule rule = modelInstance.newInstance(Rule.class);
        rule.setId(composeId(Converter.RULE_ID));

        //section for creating the input of the rule
        for (int i = 0; i < inRow.size(); i++) {
            final Text inputText = modelInstance.newInstance(Text.class);
            final String inRowText = inRow.get(i);
            final String curDataType = inDataTypes.get(i);
            if (curDataType.equals(DmnDataTypes.STRING.value())) {
                if (!StringUtils.isBlank(inRowText)) {
                    inputText.setTextContent(format("\"%s\"", inRowText));
                } else {
                    inputText.setTextContent("");
                }
            } else {
                inputText.setTextContent(inRowText);
            }

            final InputEntry inputEntry = modelInstance.newInstance(InputEntry.class);
            inputEntry.setId(composeId(Converter.INPUT_ID));
            inputEntry.addChildElement(inputText);

            rule.addChildElement(inputEntry);
        }

        //section for creating the output of the rule
        for (int i = 0; i < outRow.size(); i++) {
            Text outputText = modelInstance.newInstance(Text.class);
            if (outDataTypes.get(i).equals(DmnDataTypes.STRING.value())) {
                if (!outRow.get(i).equals("")) {
                    outputText.setTextContent("\"" + outRow.get(i) + "\"");
                } else {
                    outputText.setTextContent("");
                }
            } else {
                outputText.setTextContent(outRow.get(i));
            }

            final OutputEntry outputEntry = modelInstance.newInstance(OutputEntry.class);
            outputEntry.setId(composeId(Converter.OUTPUT_ID));
            outputEntry.addChildElement(outputText);

            rule.addChildElement(outputEntry);
        }

        final Description description = modelInstance.newInstance(Description.class);
        description.setTextContent(annotation);
        rule.setDescription(description);

        decisionTable.addChildElement(rule);
    }
}
