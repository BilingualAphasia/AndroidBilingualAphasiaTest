package ca.ilanguage.oprime.domain;

public class Experiment {
	private String experimentName;
    private String experimentStatus;
    private String experimentFolder;
    private String experimentStimuliFile;
    private String readme;
    
    public String getExperimentName() {
        return experimentName;
    }
    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
    }
    public String getExperimentStatus() {
        return experimentStatus;
    }
    public void setExperimentStatus(String experimentStatus) {
        this.experimentStatus = experimentStatus;
    }
    public String getExperimentFolder() {
        return experimentFolder;
    }
    public void setExperimentFolder(String experimentFolder) {
        this.experimentFolder = experimentFolder;
    }
    public String getExperimentStimuliFile(){
    	return experimentStimuliFile;
    }
    public void setExperimentStimuliFile(String experimentStimuliFile){
    	this.experimentStimuliFile =experimentStimuliFile;
    }
    public String getReadme(){
    	return readme;
    }
    public void setReadme(String readme){
    	this.readme=readme;
    }
}
