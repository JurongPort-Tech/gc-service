package sg.com.jp.generalcargo.domain;

import java.sql.Timestamp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BillVesselInfoVO extends UserTimestampVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// member variable
		private String vesselCallCode;
		private String name;
		private String voyageIn;
		private String voyageOut;
		private String scheme;
		private Timestamp actualTimeOfBerth;
		private Timestamp actualTimeOfUnberth;
		private Timestamp completionOfDischarge;
		private Timestamp completionOfLoad;
		private Timestamp estimatedTimeOfUnberth;
		private Timestamp estimatedTimeOfBerth;
		private int lengthOfVessel;
		private String vesselOperator;
		private String berth;
		private Timestamp lastActivity;
		private String portFr;
		private String portTo;
		
	    public BillVesselInfoVO() {
	    }
		public void doGet(Object object) {}
		public void doSet(Object object) {}

		
		public void setVesselCallCode(String vesselCallCode) { this.vesselCallCode = vesselCallCode; }
		public void setName(String name) { this.name = name; }
		public void setVoyageIn(String voyageIn) { this.voyageIn = voyageIn; }
		public void setVoyageOut(String voyageOut) { this.voyageOut = voyageOut; }
		public void setScheme(String scheme) { this.scheme = scheme; }
		public void setActualTimeOfBerth(Timestamp actualTimeOfBerth) { this.actualTimeOfBerth = actualTimeOfBerth; }
		public void setActualTimeOfUnberth(Timestamp actualTimeOfUnberth) { this.actualTimeOfUnberth = actualTimeOfUnberth; }
		public void setCompletionOfDischarge(Timestamp completionOfDischarge) { this.completionOfDischarge = completionOfDischarge; }
		public void setCompletionOfLoad(Timestamp completionOfLoad) { this.completionOfLoad = completionOfLoad; }
		public void setEstimatedTimeOfUnberth(Timestamp estimatedTimeOfUnberth) { this.estimatedTimeOfUnberth = estimatedTimeOfUnberth; }
		public void setEstimatedTimeOfBerth(Timestamp estimatedTimeOfBerth) { this.estimatedTimeOfBerth = estimatedTimeOfBerth; }
		public void setLengthOfVessel(int lengthOfVessel){ this.lengthOfVessel = lengthOfVessel; }
		public void setVesselOperator(String vesselOperator){ this.vesselOperator = vesselOperator; }
		public void setBerth(String berth) { this.berth = berth; }
		public void setLastActivity(Timestamp lastActivity) { this.lastActivity = lastActivity; }

		public String getVesselCallCode() { return (this.vesselCallCode); }
		public String getName() { return (this.name); }
		public String getVoyageIn() { return (this.voyageIn); }
		public String getVoyageOut() { return (this.voyageOut); }
		public String getScheme() { return (this.scheme); }
		public Timestamp getActualTimeOfBerth() { return (this.actualTimeOfBerth); }
		public Timestamp getActualTimeOfUnberth() { return (this.actualTimeOfUnberth); }
		public Timestamp getCompletionOfDischarge(){ return (this.completionOfDischarge); }
		public Timestamp getCompletionOfLoad(){ return (this.completionOfLoad); }
		public Timestamp getEstimatedTimeOfUnberth() { return (this.estimatedTimeOfUnberth); }
		public Timestamp getEstimatedTimeOfBerth() { return (this.estimatedTimeOfBerth); }
		public int getLengthOfVessel() { return (this.lengthOfVessel); }
		public String getVesselOperator() { return (this.vesselOperator); }
		public String getBerth() { return (this.berth); }
		public Timestamp getLastActivity() { return (this.lastActivity); }
		public String getPortFr() {
			return portFr;
		}
		public void setPortFr(String portFr) {
			this.portFr = portFr;
		}
		public String getPortTo() {
			return portTo;
		}
		public void setPortTo(String portTo) {
			this.portTo = portTo;
		}
		
		@Override
		public String toString() {
			try {
				return new ObjectMapper().writeValueAsString(this);
			} catch (JsonProcessingException e) {
				return "";
			}
		}
}
