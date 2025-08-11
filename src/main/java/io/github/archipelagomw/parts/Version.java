package io.github.archipelagomw.parts;

import com.google.gson.annotations.SerializedName;

public class Version {
    int major;
    int minor;
    int build;
    @SerializedName("class")
    String fakeClass = "Version";

    public Version(int major, int minor, int build) {
        this.major = major;
        this.minor = minor;
        this.build = build;
    }
}
