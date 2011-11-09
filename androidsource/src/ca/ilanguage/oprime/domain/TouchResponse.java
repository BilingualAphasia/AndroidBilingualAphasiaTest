package ca.ilanguage.oprime.domain;

public class TouchResponse {
	public Long stimulusOnset = System.currentTimeMillis();
	public Long touchTime = (long) 0;
	public Long totalReactionTime = (long) 0;
	public Long reactionTime  = (long) 0;
	public Long audioOffset = (long) 0;
	public float touchx  = 0;
	public float touchy = 0;
	public String touchCode = "";
	public String stimulusCode ="";
	
	
}
