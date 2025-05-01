//Copyright (c) 2023 Vereign AG [https://www.vereign.com]

package api.test.rest.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Member {

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("givenname")
    @Expose
    private String givenname;
    @SerializedName("familyname")
    @Expose
    private String familyname;
    @SerializedName("middlename")
    @Expose
    private String middlename;
    @SerializedName("phonenumber")
    @Expose
    private String phoneNumber;
    @SerializedName("guardedUuid")
    @Expose
    private String guardedUuid;
    @SerializedName("childUuid")
    @Expose
    private String childUuid;
    @SerializedName("resourceId")
    @Expose
    private String resourceId;

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return givenname;
    }

    public void setName(String givenname) {
        this.givenname = givenname;
    }

    public String getFamily() {
        return familyname;
    }

    public void setFamily(String familyname) {
        this.familyname = familyname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGuardedUuid() {
        return guardedUuid;
    }

    public void setGuardedUuid(String guardedUuid) {
        this.guardedUuid = guardedUuid;
    }

    public String getChildUuid() {
        return childUuid;
    }

    public void setChildUuid(String childUuid) {
        this.childUuid = childUuid;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}
