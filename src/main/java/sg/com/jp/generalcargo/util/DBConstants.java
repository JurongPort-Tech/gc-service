package sg.com.jp.generalcargo.util;

public class DBConstants {

	public interface MISC_APP {
		public static final String TABLE_NAME = "MISC_APP";
		public static final String REF_NBR="REF_NBR";
		public static final String APP_DTTM ="APP_DTTM";
		public static final String COMPANY_NAME="COMPANY NAME";


	}
	public interface MISC_MACHINE {
		public static final String TABLE_NAME = "MISC_MACHINE";
		public static final String MAC_TYPE="MAC_TYPE";
		public static final String FR_DTTM="FR_DTTM";
	}
	public interface MISC_MACHINE_DET {
		public static final String TABLE_NAME = "MISC_MACHINE_DET";
		public static final String REG_NBR ="REG_NBR";
		public static final String LIFT_CAPACITY="LIFT_CAPACITY";
		public static final String INSURANCE_NBR="INSURANCE_NBR";
		public static final String INSURANCE_EXP_DTTM="INSURANCE_EXP_DTTM";

	}
	public interface COMPANY_CODE {
		public static final String TABLE_NAME = "COMPANY_CODE";
		public static final String CO_NM ="CO_NM";

	}
	public interface MAIL {
		public static final String TABLE_NAME = "EXCEPTION_ALERT";
		public static final String TO_ADDRESS ="ACCOUNT";

	}

	public interface MISC_TYPE_CODE {
			public static final String MISC_TYPE_NM = "MISC_TYPE_NM";
	}
}
