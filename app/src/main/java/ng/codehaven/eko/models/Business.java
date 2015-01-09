package ng.codehaven.eko.models;

import com.orm.SugarRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Thompson on 09/01/2015.
 * Business model
 */
public class Business extends SugarRecord<Business> {
    protected String name, businessId, ownerId, PhoneNumber, Address, AgentPhoneNumber;
    protected int businessType;

    public Business() {
    }

    public static ArrayList<Business> fromJson(JSONArray jsonArray) throws JSONException {
        ArrayList<Business> businesses = new ArrayList<>(jsonArray.length());
        // Process each result in json array, decode and convert to business object
        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject businessJson;
            try {
                businessJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Business business = Business.fromJSON(businessJson);
            if (business != null) {
                businesses.add(business);
            }
        }

        return businesses;
    }

    public Business(String name, String businessId, String ownerId, String PhoneNumber, String Address, String AgentPhoneNumber, int businessType){
        this.name = name;
        this.businessId = businessId;
        this.ownerId = ownerId;
        this.PhoneNumber = PhoneNumber;
        this.Address = Address;
        this.AgentPhoneNumber = AgentPhoneNumber;
        this.businessType = businessType;
    }

    public static Business fromJSON(JSONObject business) throws JSONException {
        Business b = new Business();
        b.name = business.getString("name");
        b.businessId = business.getString("businessId");
        b.ownerId = business.getString("ownerId");
        b.PhoneNumber = business.getString("PhoneNumber");
        b.Address = business.getString("Address");
        b.AgentPhoneNumber = business.getString("AgentPhoneNumber");
        b.businessType = business.getInt("businessType");
        return b;
    }

    public String getName() {
        return name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public String getAddress() {
        return Address;
    }

    public String getAgentPhoneNumber() {
        return AgentPhoneNumber;
    }

    public int getBusinessType() {
        return businessType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public void setAgentPhoneNumber(String agentPhoneNumber) {
        AgentPhoneNumber = agentPhoneNumber;
    }

    public void setBusinessType(int businessType) {
        this.businessType = businessType;
    }
}
