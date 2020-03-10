package com.marketwinks.livepricefeeder.model;

import java.util.List;

import org.bson.types.ObjectId;
import org.json.simple.JSONObject;
import org.springframework.data.annotation.Id;

public class uk_lse_5mins_livemarketmacdjson {

	@Id
	public ObjectId _id;

	private String macdjsonref;
	private List<JSONObject> macdjson;

	// ObjectId needs to be converted to string
	public String get_id() {
		return _id.toHexString();
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	/**
	 * @return the macdjsonref
	 */
	public String getMacdjsonref() {
		return macdjsonref;
	}

	/**
	 * @param macdjsonref
	 *            the macdjsonref to set
	 */
	public void setMacdjsonref(String macdjsonref) {
		this.macdjsonref = macdjsonref;
	}

	/**
	 * @return the macdjson
	 */
	public List<JSONObject> getMacdjson() {
		return macdjson;
	}

	/**
	 * @param macdDataforSaving
	 *            the macdjson to set
	 */
	public void setMacdjson(List<JSONObject> macdDataforSaving) {
		this.macdjson = macdDataforSaving;
	}
}
