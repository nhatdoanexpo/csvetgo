package com.bot.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DeviceProfile {
 private String chrome_id;
 private String qrCode;
 private String lastUpdate;
 private String linkProfile;
 private String phone;
 private boolean deleted;
}
 