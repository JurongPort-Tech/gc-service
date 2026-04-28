
package sg.com.jp.generalcargo.domain;

import java.io.Serializable;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TopsModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private ArrayList<Serializable> arrayList;
    private int collectionSize;
    private int index;

    /** Creates new RASCollection */
    public TopsModel() {
        arrayList = new ArrayList<Serializable>(1);
    }

    public TopsModel(int size) {
        arrayList = new ArrayList<Serializable>(size);
    }

    public int getSize() {
        return collectionSize;
    }

    public ArrayList getTopsModel() {
        return arrayList;
    }

    public void put(Serializable topsObject) {
        arrayList.add(collectionSize++, topsObject);
    }

    public Serializable get(int index) {
        return (Serializable) arrayList.get(index);
    }

    public Serializable get() {
        return (Serializable) arrayList.get(0);
    }

    public boolean hasMore() {
        if (index < collectionSize)
            return true;
        return false;
    }

    public Serializable nextValue() {
        // if (index < collectionSize)
        return (Serializable) arrayList.get(index++);
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
