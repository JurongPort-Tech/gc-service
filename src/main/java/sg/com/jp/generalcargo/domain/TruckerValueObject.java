package sg.com.jp.generalcargo.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TruckerValueObject {
	
	
	//Multi Adp/Trucker
		public static final int MAX_ADP_TRUCKER = 5; 
		public static final String MAX_ADP_TRUCKER_STRING = "5"; 
		
	    private String truckerIc;
	    private String truckerNm;
	    private String truckerContact;
	    private String truckerPkgs;
	    private String truckerAdd;
	    private String truckerCd;
	    private boolean truckerChbx;

	    /**
		 * @return the truckerChbx
		 */
		public boolean isTruckerChbx() {
			return truckerChbx;
		}

		/**
		 * @param truckerChbx the truckerChbx to set
		 */
		public void setTruckerChbx(boolean truckerChbx) {
			this.truckerChbx = truckerChbx;
		}

		public TruckerValueObject() {
	    }

		public String getTruckerIc() {
			return truckerIc;
		}

		public void setTruckerIc(String truckerIc) {
			this.truckerIc = truckerIc;
		}

		public String getTruckerNm() {
			return truckerNm;
		}

		public void setTruckerNm(String truckerNm) {
			this.truckerNm = truckerNm;
		}

		public String getTruckerContact() {
			return truckerContact;
		}

		public void setTruckerContact(String truckerContact) {
			this.truckerContact = truckerContact;
		}

		public String getTruckerPkgs() {
			return truckerPkgs;
		}

		public void setTruckerPkgs(String truckerPkgs) {
			this.truckerPkgs = truckerPkgs;
		}

		/**
		 * @return the truckerAdd
		 */
		public String getTruckerAdd() {
			return truckerAdd;
		}

		/**
		 * @param truckerAdd the truckerAdd to set
		 */
		public void setTruckerAdd(String truckerAdd) {
			this.truckerAdd = truckerAdd;
		}

		/**
		 * @return the truckerCd
		 */
		public String getTruckerCd() {
			return truckerCd;
		}

		/**
		 * @param truckerCd the truckerCd to set
		 */
		public void setTruckerCd(String truckerCd) {
			this.truckerCd = truckerCd;
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
