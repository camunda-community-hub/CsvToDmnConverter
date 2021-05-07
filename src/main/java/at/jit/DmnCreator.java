package at.jit;

import at.jit.SequenceGenerator.SequenceGenerator;
import at.jit.SequenceGenerator.SequenceGeneratorImpl;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.*;

import java.util.List;

class DmnCreator {
    private DmnModelInstance modelInstance;
    private DecisionTable decisionTable;
    private SequenceGenerator sequenceGenerator;

    public DmnModelInstance createDmnFromCsvPojo(CsvPojo csvPojo) {
        sequenceGenerator = new SequenceGeneratorImpl();

        initializeBasicDmnModel(csvPojo);

        createInputHeaders(csvPojo.getInColName(), csvPojo.getInColDataType());
        createOutputHeaders(csvPojo.getOutColName(), csvPojo.getOutColDataType());

        List<List<String>> inData = csvPojo.getInData();
        List<List<String>> outData = csvPojo.getOutData();
        List<String> annotations = csvPojo.getRowAnnotation();

        int entries = inData.size();

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
        Definitions definitions = modelInstance.newInstance(Definitions.class);
        definitions.setNamespace("http://camunda.org/schema/1.0/dmn");
        definitions.setName(Converter.DMN_NAME);
        definitions.setId(Converter.DMN_ID);
        modelInstance.setDefinitions(definitions);

        Decision decision = modelInstance.newInstance(Decision.class);

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
            Input input = modelInstance.newInstance(Input.class);
            input.setId(Converter.INPUT_ID + sequenceGenerator.getNext());
            input.setLabel(labels.get(i));
            InputExpression inputExpression = modelInstance.newInstance(InputExpression.class);
            inputExpression.setId(Converter.INPUT_EXPRESSION_ID + sequenceGenerator.getNext());
            inputExpression.setTypeRef(dataTypes.get(i));
            input.addChildElement(inputExpression);
            decisionTable.addChildElement(input);
        }
    }

    public void createOutputHeaders(List<String> labels, List<String> dataTypes) {
        for (int i = 0; i < labels.size(); i++) {
            Output output = modelInstance.newInstance(Output.class);
            output.setId(Converter.OUTPUT_ID + sequenceGenerator.getNext());
            output.setLabel(labels.get(i));
            output.setName(labels.get(i));
            output.setTypeRef(dataTypes.get(i));
            decisionTable.addChildElement(output);
        }
    }

    public void createRules(List<String> inRow, List<String> outRow, String annotation, List<String> inDataTypes, List<String> outDataTypes) {
        Rule rule = modelInstance.newInstance(Rule.class);
        rule.setId(Converter.RULE_ID + sequenceGenerator.getNext());

        //section for creating the input of the rule
        for (int i = 0; i < inRow.size(); i++) {
            Text inputText = modelInstance.newInstance(Text.class);
            if (inDataTypes.get(i).equals(DmnDataTypes.STRING.value())) {
                if (!inRow.get(i).equals("")) {
                    inputText.setTextContent("\"" + inRow.get(i) + "\"");
                } else {
                    inputText.setTextContent("");
                }
            } else {
                inputText.setTextContent(inRow.get(i));
            }

            InputEntry inputEntry = modelInstance.newInstance(InputEntry.class);
            inputEntry.setId(Converter.INPUT_ID + sequenceGenerator.getNext());
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

            OutputEntry outputEntry = modelInstance.newInstance(OutputEntry.class);
            outputEntry.setId(Converter.OUTPUT_ID + sequenceGenerator.getNext());
            outputEntry.addChildElement(outputText);

            rule.addChildElement(outputEntry);
        }

        Description description = modelInstance.newInstance(Description.class);
        description.setTextContent(annotation);
        rule.setDescription(description);

        decisionTable.addChildElement(rule);
    }
}
