package at.jit.entity;

import org.camunda.bpm.model.dmn.HitPolicy;

import java.util.List;

public class CsvPojo {
    private String dmnId;
    private String dmnTitle;
    private HitPolicy hitPolicy;

    private List<String> inColName;
    private List<String> inColDataType;

    private List<String> outColName;
    private List<String> outColDataType;

    private List<List<String>> inData;
    private List<List<String>> outData;

    private List<String> rowAnnotation;

    public String getDmnId() {
        return dmnId;
    }

    public void setDmnId(String dmnId) {
        this.dmnId = dmnId;
    }

    public String getDmnTitle() {
        return dmnTitle;
    }

    public void setDmnTitle(String dmnTitle) {
        this.dmnTitle = dmnTitle;
    }

    public HitPolicy getHitPolicy() {
        return hitPolicy;
    }

    public void setHitPolicy(String hitPolicy) {
        switch (hitPolicy.toLowerCase()) {
            case "unique":
                this.hitPolicy = HitPolicy.UNIQUE;
                break;
            case "any":
                this.hitPolicy = HitPolicy.ANY;
                break;
            case "collect":
                this.hitPolicy = HitPolicy.COLLECT;
                break;
            case "first":
                this.hitPolicy = HitPolicy.FIRST;
                break;
            case "output order":
                this.hitPolicy = HitPolicy.OUTPUT_ORDER;
                break;
            case "priority":
                this.hitPolicy = HitPolicy.PRIORITY;
                break;
            case "rule order":
                this.hitPolicy = HitPolicy.RULE_ORDER;
                break;
            default:
                this.hitPolicy = HitPolicy.UNIQUE;
                break;
        }
    }

    public List<String> getInColName() {
        return inColName;
    }

    public void setInColName(List<String> inColName) {
        this.inColName = inColName;
    }

    public List<String> getOutColName() {
        return outColName;
    }

    public void setOutColName(List<String> outColName) {
        this.outColName = outColName;
    }

    public List<String> getInColDataType() {
        return inColDataType;
    }

    public void setInColDataType(List<String> inColDataType) {
        this.inColDataType = inColDataType;
    }

    public List<String> getOutColDataType() {
        return outColDataType;
    }

    public void setOutColDataType(List<String> outColDataType) {
        this.outColDataType = outColDataType;
    }

    public List<List<String>> getInData() {
        return inData;
    }

    public void setInData(List<List<String>> inData) {
        this.inData = inData;
    }

    public List<List<String>> getOutData() {
        return outData;
    }

    public void setOutData(List<List<String>> outData) {
        this.outData = outData;
    }

    public List<String> getRowAnnotation() {
        return rowAnnotation;
    }

    public void setRowAnnotation(List<String> rowAnnotation) {
        this.rowAnnotation = rowAnnotation;
    }

    @Override
    public String toString() {
        return "CsvPojo{" +
                "dmnId='" + dmnId + '\'' +
                ", dmnTitle='" + dmnTitle + '\'' +
                ", hitPolicy='" + hitPolicy + '\'' +
                ", inColName=" + inColName +
                ", outColName=" + outColName +
                ", inData=" + inData +
                ", outData=" + outData +
                ", rowAnnotation=" + rowAnnotation +
                '}';
    }
}
