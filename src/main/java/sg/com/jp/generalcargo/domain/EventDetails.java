package sg.com.jp.generalcargo.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EventDetails implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8423214682773607840L;
	private String vv_cd;
	private String create_user_id;
	private String event_type;
	private String source_system;
	private String kafka_resp_status;
	public Object kafka_msg;

	public String getVv_cd() {
		return vv_cd;
	}

	public void setVv_cd(String vv_cd) {
		this.vv_cd = vv_cd;
	}

	public String getCreate_user_id() {
		return create_user_id;
	}

	public void setCreate_user_id(String create_user_id) {
		this.create_user_id = create_user_id;
	}

	public String getEvent_type() {
		return event_type;
	}

	public void setEvent_type(String event_type) {
		this.event_type = event_type;
	}

	public String getSource_system() {
		return source_system;
	}

	public void setSource_system(String source_system) {
		this.source_system = source_system;
	}

	public String getKafka_resp_status() {
		return kafka_resp_status;
	}

	public void setKafka_resp_status(String kafka_resp_status) {
		this.kafka_resp_status = kafka_resp_status;
	}

	public Object getKafka_msg() {
		return kafka_msg;
	}

	public void setKafka_msg(Object kafka_msg) {
		this.kafka_msg = kafka_msg;
	}

	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class VslEvent {
		private String vvCode;

		private String vslName;

		private String inVoy;

		private String outVoy;

		private String atu;

		private String atb;

		private String etu;

		private String etb;

		private String fat;

		private String lat;

		public void setVvCode(String vvCode) {
			this.vvCode = vvCode;
		}

		public void setVslName(String vslName) {
			this.vslName = vslName;
		}

		public void setInVoy(String inVoy) {
			this.inVoy = inVoy;
		}

		public void setOutVoy(String outVoy) {
			this.outVoy = outVoy;
		}

		public void setAtu(String atu) {
			this.atu = atu;
		}

		public void setAtb(String atb) {
			this.atb = atb;
		}

		public void setEtu(String etu) {
			this.etu = etu;
		}

		public void setEtb(String etb) {
			this.etb = etb;
		}

		public void setFat(String fat) {
			this.fat = fat;
		}

		public void setLat(String lat) {
			this.lat = lat;
		}

		public boolean equals(Object o) {
			if (o == this)
				return true;
			if (!(o instanceof VslEvent))
				return false;
			VslEvent other = (VslEvent) o;
			if (!other.canEqual(this))
				return false;
			Object this$vvCode = getVvCode(), other$vvCode = other.getVvCode();
			if ((this$vvCode == null) ? (other$vvCode != null) : !this$vvCode.equals(other$vvCode))
				return false;
			Object this$vslName = getVslName(), other$vslName = other.getVslName();
			if ((this$vslName == null) ? (other$vslName != null) : !this$vslName.equals(other$vslName))
				return false;
			Object this$inVoy = getInVoy(), other$inVoy = other.getInVoy();
			if ((this$inVoy == null) ? (other$inVoy != null) : !this$inVoy.equals(other$inVoy))
				return false;
			Object this$outVoy = getOutVoy(), other$outVoy = other.getOutVoy();
			if ((this$outVoy == null) ? (other$outVoy != null) : !this$outVoy.equals(other$outVoy))
				return false;
			Object this$atu = getAtu(), other$atu = other.getAtu();
			if ((this$atu == null) ? (other$atu != null) : !this$atu.equals(other$atu))
				return false;
			Object this$atb = getAtb(), other$atb = other.getAtb();
			if ((this$atb == null) ? (other$atb != null) : !this$atb.equals(other$atb))
				return false;
			Object this$etu = getEtu(), other$etu = other.getEtu();
			if ((this$etu == null) ? (other$etu != null) : !this$etu.equals(other$etu))
				return false;
			Object this$etb = getEtb(), other$etb = other.getEtb();
			if ((this$etb == null) ? (other$etb != null) : !this$etb.equals(other$etb))
				return false;
			Object this$fat = getFat(), other$fat = other.getFat();
			if ((this$fat == null) ? (other$fat != null) : !this$fat.equals(other$fat))
				return false;
			Object this$lat = getLat(), other$lat = other.getLat();
			return !((this$lat == null) ? (other$lat != null) : !this$lat.equals(other$lat));
		}

		protected boolean canEqual(Object other) {
			return other instanceof VslEvent;
		}

		public int hashCode() {
			int PRIME = 59;
			int result = 1;
			Object $vvCode = getVvCode();
			result = result * 59 + (($vvCode == null) ? 43 : $vvCode.hashCode());
			Object $vslName = getVslName();
			result = result * 59 + (($vslName == null) ? 43 : $vslName.hashCode());
			Object $inVoy = getInVoy();
			result = result * 59 + (($inVoy == null) ? 43 : $inVoy.hashCode());
			Object $outVoy = getOutVoy();
			result = result * 59 + (($outVoy == null) ? 43 : $outVoy.hashCode());
			Object $atu = getAtu();
			result = result * 59 + (($atu == null) ? 43 : $atu.hashCode());
			Object $atb = getAtb();
			result = result * 59 + (($atb == null) ? 43 : $atb.hashCode());
			Object $etu = getEtu();
			result = result * 59 + (($etu == null) ? 43 : $etu.hashCode());
			Object $etb = getEtb();
			result = result * 59 + (($etb == null) ? 43 : $etb.hashCode());
			Object $fat = getFat();
			result = result * 59 + (($fat == null) ? 43 : $fat.hashCode());
			Object $lat = getLat();
			return result * 59 + (($lat == null) ? 43 : $lat.hashCode());
		}

		public VslEvent() {
		}

		public VslEvent(String vvCode, String vslName, String inVoy, String outVoy, String atu, String atb, String etu,
				String etb, String fat, String lat) {
			this.vvCode = vvCode;
			this.vslName = vslName;
			this.inVoy = inVoy;
			this.outVoy = outVoy;
			this.atu = atu;
			this.atb = atb;
			this.etu = etu;
			this.etb = etb;
			this.fat = fat;
			this.lat = lat;
		}

		public static VslEventBuilder builder() {
			return new VslEventBuilder();
		}

		public VslEventBuilder toBuilder() {
			return (new VslEventBuilder()).vvCode(this.vvCode).vslName(this.vslName).inVoy(this.inVoy).outVoy(this.outVoy)
					.atu(this.atu).atb(this.atb).etu(this.etu).etb(this.etb).fat(this.fat).lat(this.lat);
		}

		public static class VslEventBuilder {
			private String vvCode;

			private String vslName;

			private String inVoy;

			private String outVoy;

			private String atu;

			private String atb;

			private String etu;

			private String etb;

			private String fat;

			private String lat;

			public VslEventBuilder vvCode(String vvCode) {
				this.vvCode = vvCode;
				return this;
			}

			public VslEventBuilder vslName(String vslName) {
				this.vslName = vslName;
				return this;
			}

			public VslEventBuilder inVoy(String inVoy) {
				this.inVoy = inVoy;
				return this;
			}

			public VslEventBuilder outVoy(String outVoy) {
				this.outVoy = outVoy;
				return this;
			}

			public VslEventBuilder atu(String atu) {
				this.atu = atu;
				return this;
			}

			public VslEventBuilder atb(String atb) {
				this.atb = atb;
				return this;
			}

			public VslEventBuilder etu(String etu) {
				this.etu = etu;
				return this;
			}

			public VslEventBuilder etb(String etb) {
				this.etb = etb;
				return this;
			}

			public VslEventBuilder fat(String fat) {
				this.fat = fat;
				return this;
			}

			public VslEventBuilder lat(String lat) {
				this.lat = lat;
				return this;
			}

			public VslEvent build() {
				return new VslEvent(this.vvCode, this.vslName, this.inVoy, this.outVoy, this.atu, this.atb, this.etu,
						this.etb, this.fat, this.lat);
			}

			public String toString() {
				return "VslEvent.VslEventBuilder(vvCode=" + this.vvCode + ", vslName=" + this.vslName + ", inVoy="
						+ this.inVoy + ", outVoy=" + this.outVoy + ", atu=" + this.atu + ", atb=" + this.atb + ", etu="
						+ this.etu + ", etb=" + this.etb + ", fat=" + this.fat + ", lat=" + this.lat + ")";
			}
		}

		public String getVvCode() {
			return this.vvCode;
		}

		public String getVslName() {
			return this.vslName;
		}

		public String getInVoy() {
			return this.inVoy;
		}

		public String getOutVoy() {
			return this.outVoy;
		}

		public String getAtu() {
			return this.atu;
		}

		public String getAtb() {
			return this.atb;
		}

		public String getEtu() {
			return this.etu;
		}

		public String getEtb() {
			return this.etb;
		}

		public String getFat() {
			return this.fat;
		}

		public String getLat() {
			return this.lat;
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

}
