package sg.com.jp.generalcargo.util;

import java.util.Comparator;

import sg.com.jp.generalcargo.domain.EnquireSummarySlotValueObject;

public class SummaryRowComparator implements Comparator<Object> {

	public int compare(Object o1, Object o2) {
		if (!(o1 instanceof EnquireSummarySlotValueObject && o2 instanceof EnquireSummarySlotValueObject)) {
			throw new ClassCastException(
					"One of object in collection is null or not instance of EnquireSummarySlotValueObject.class");
		}

		EnquireSummarySlotValueObject obj1 = (EnquireSummarySlotValueObject) o1;
		EnquireSummarySlotValueObject obj2 = (EnquireSummarySlotValueObject) o2;

		int areaCompare = obj1.getAreaCode().compareTo(obj2.getAreaCode());
		int slotTypeCompare = obj1.getSlotType().compareTo(obj2.getSlotType());
		int slotNumberCompare = obj1.getSlotNumber().compareTo(obj2.getSlotNumber());

		if (areaCompare != 0) {
			return areaCompare;
		}

		if (slotTypeCompare != 0) {
			return slotTypeCompare;
		}

		if (obj1.getTrailerSize() > obj2.getTrailerSize()) {
			return 1;
		} else if (obj1.getTrailerSize() < obj2.getTrailerSize()) {
			return -1;
		}

		if (obj1.getTrailerType() != null && obj1.getTrailerType() != null) {
			int trailerTypeCompare = obj1.getTrailerType().compareTo(obj2.getTrailerType());
			if (trailerTypeCompare != 0) {
				return trailerTypeCompare;
			}
		}

		if (slotNumberCompare != 0) {
			return slotNumberCompare;
		} else {
			return 0;
		}

	}

}
